/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.timezones;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.Subdivision;
import org.geoname.data.Subdivisions;
import org.geoname.data.utility.Utility;
import us.fatehi.pointlocation6709.Longitude;

/** Time zone utilities. */
public final class DefaultTimezones {

  private static final Logger LOGGER = Logger.getLogger(DefaultTimezones.class.getName());

  private static final Map<Country, List<String>> defaultTimezones = new HashMap<>();
  private static final Map<String, List<String>> allTimezoneIds = new HashMap<>();
  private static final Set<TimeZoneDisplay> allTimezones = new TreeSet<>();

  /**
   * Loads default time zones from the internal database. These are default time zone ids for every
   * time zone for each country.
   */
  static {
    final List<Country> allCountries = Countries.getAllCountries();
    for (final Country country : allCountries) {
      defaultTimezones.put(country, new ArrayList<>());
    }

    final CSVFormat format =
        CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setCommentMarker('#')
            .setIgnoreEmptyLines(true)
            .get();
    try (CSVParser csvParser =
        format.parse(
            new InputStreamReader(
                DefaultTimezones.class
                    .getClassLoader()
                    .getResourceAsStream("default.timezones.data"),
                "UTF-8"))) {
      for (final CSVRecord record : csvParser) {
        final String iso3166CountryCode2 = record.get("country_code");
        final String timezoneId = record.get("timezone");
        if (iso3166CountryCode2 == null
            || timezoneId == null
            || iso3166CountryCode2.length() != 2
            || timezoneId.isEmpty()) {
          continue;
        }
        final ZoneId defaultZoneId = ZoneId.of(timezoneId);
        final Country country = Countries.lookupIso3166CountryCode2(iso3166CountryCode2);
        final List<String> defaultTimezonesForCountry = defaultTimezones.get(country);
        if (defaultTimezonesForCountry != null) {
          defaultTimezonesForCountry.add(defaultZoneId.getId());
        }
      }
    } catch (final IOException e) {
      throw new IllegalStateException("Cannot read data from internal database", e);
    }
  }

  static {
    final Set<String> allTimeZoneIds = ZoneId.getAvailableZoneIds();
    for (final String timeZoneId : allTimeZoneIds) {
      if (timeZoneId.startsWith("Etc/")) {
        continue;
      }
      allTimezones.add(new TimeZoneDisplay(ZoneId.of(timeZoneId)));
      final List<String> timeZoneIdParts = splitTimeZoneId(timeZoneId);
      if (timeZoneIdParts.size() > 0) {
        allTimezoneIds.put(timeZoneId, timeZoneIdParts);
      }
    }
  }

  /**
   * Infers the best timezone ID for a location using a four-step strategy.
   *
   * <ol>
   *   <li><b>Single default</b> — if the country has exactly one registered default timezone,
   *       return it immediately; no further lookup is needed because there is no ambiguity.
   *   <li><b>City name match</b> — search all JVM timezone IDs for one whose city segment matches
   *       the supplied city name (e.g. {@code "New York"} → {@code "America/New_York"}). Only
   *       reached when the country has zero or more than one default timezone.
   *   <li><b>Longitude proximity</b> — when the country has multiple defaults and the city name
   *       matched nothing, pick the default whose standard UTC offset is closest to the
   *       longitude-derived nominal offset ({@code longitude ÷ 15}).
   *   <li><b>GMT fallback</b> — if no default exists and the city name match failed, synthesise a
   *       {@code GMT±HH:MM} ID directly from the longitude. This is also the result when the
   *       country is completely unknown.
   * </ol>
   *
   * @param city City name (used for step 2 city-name matching)
   * @param country Country (used to look up registered default timezones)
   * @param longitude Geographic longitude (used for step 3 proximity and step 4 fallback)
   * @return A timezone ID string, or {@code null} if {@code longitude} is {@code null}
   */
  public static String attemptTimeZoneMatch(
      final String city, final Country country, final Longitude longitude) {

    if (longitude == null) {
      return null;
    }

    // Nominal UTC offset derived from the location's longitude (1 hour per 15° of longitude).
    double tzOffsetHours = longitude.getDegrees() / 15D;
    tzOffsetHours = roundToNearestFraction(tzOffsetHours, 0.5D);

    final List<String> defaultTimezonesForCountry = defaultTimezones.get(country);

    // Step 1: Single default timezone.
    // When a country has exactly one registered timezone there is no ambiguity — return it
    // without running the more expensive city-name search.
    if (defaultTimezonesForCountry != null && defaultTimezonesForCountry.size() == 1) {
      return defaultTimezonesForCountry.get(0);
    }

    // Step 2: City name match.
    // For countries with zero or multiple defaults, try to find a timezone whose city segment
    // matches the supplied city name (e.g. "New York" → "America/New_York").
    final String cityMatchedTimeZoneId = findBestTimeZoneId(city, country);
    if (cityMatchedTimeZoneId != null) {
      LOGGER.log(
          Level.INFO,
          "Time zone id for \""
              + city
              + ", "
              + country
              + "\" matched by city name: \""
              + cityMatchedTimeZoneId
              + "\"");
      return cityMatchedTimeZoneId;
    }

    // Step 3: Longitude proximity.
    // For countries with multiple defaults and no city-name match, pick the default whose
    // standard UTC offset is closest to the longitude-derived nominal offset.
    if (defaultTimezonesForCountry != null && defaultTimezonesForCountry.size() > 1) {
      double leastDifference = Double.MAX_VALUE;
      String timeZoneId = null;
      for (final String defaultTimeZoneId : defaultTimezonesForCountry) {
        final double difference =
            Math.abs(getStandardTimeZoneOffsetHours(defaultTimeZoneId) - tzOffsetHours);
        if (difference < leastDifference) {
          leastDifference = difference;
          timeZoneId = defaultTimeZoneId;
        }
      }
      if (timeZoneId != null) {
        LOGGER.log(
            Level.INFO,
            "Time zone id for \""
                + city
                + ", "
                + country
                + "\" matched by longitude proximity: \""
                + timeZoneId
                + "\"");
        return timeZoneId;
      }
    }

    // Step 4: GMT fallback.
    // No registered defaults exist for this country (or proximity matching yielded nothing).
    // Synthesise a GMT±HH:MM timezone directly from the longitude.
    return createGMTTimeZoneId(tzOffsetHours);
  }

  /**
   * Create a STANDARD GMT-based timezone id.
   *
   * @param longitude Longitude
   * @return Time zone id string
   */
  public static String createGMTTimeZoneId(final Longitude longitude) {
    if (longitude == null) {
      return ZoneId.systemDefault().getId();
    }

    final double tzOffsetHours = longitude.getDegrees() / 15D;
    StringBuilder timeZoneId = new StringBuilder("GMT");
    if (tzOffsetHours < 0) {
      timeZoneId.append("-");
    } else {
      timeZoneId.append("+");
    }

    final int[] hourFields = Utility.sexagesimalSplit(Math.abs(tzOffsetHours));

    final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(2);

    timeZoneId
        .append(numberFormat.format(hourFields[0]))
        .append(":")
        .append(numberFormat.format(hourFields[1]));
    return timeZoneId.toString();
  }

  /**
   * Gets all time zones.
   *
   * @return All time zones
   */
  public static List<TimeZoneDisplay> getAllTimeZonesForDisplay() {
    final ArrayList<TimeZoneDisplay> allTimeZonesList = new ArrayList<>(allTimezones);
    Collections.sort(allTimeZonesList);
    return allTimeZonesList;
  }

  /**
   * Calculates the STANDARD time zone offset, in hours.
   *
   * @param timeZoneId Time zone id
   * @return Time zone offset, in hours
   */
  public static double getStandardTimeZoneOffsetHours(final String timeZoneId) {
    if (timeZoneId == null) {
      return 0D;
    }
    final ZoneId zoneId = ZoneId.of(timeZoneId);
    return zoneId.getRules().getStandardOffset(Instant.now()).getTotalSeconds() / (60D * 60D);
  }

  /**
   * Utility to round a number to the nearest half.
   *
   * @param number Number to round
   * @param fraction Fraction to round to
   * @return Rounded numbers
   */
  public static double roundToNearestFraction(final double number, final double fraction) {
    return new BigDecimal(number / fraction)
            .round(new MathContext(1, RoundingMode.HALF_UP))
            .doubleValue()
        * fraction;
  }

  /**
   * Create a STANDARD GMT-based timezone id.
   *
   * @param tzOffsetHours Time zone offset, in hours
   * @return Time zone id string
   */
  private static String createGMTTimeZoneId(final double tzOffsetHours) {
    StringBuilder timeZoneId = new StringBuilder("GMT");
    if (tzOffsetHours < 0) {
      timeZoneId.append("-");
    } else {
      timeZoneId.append("+");
    }

    final int[] hourFields = Utility.sexagesimalSplit(Math.abs(tzOffsetHours));

    final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(2);

    timeZoneId
        .append(numberFormat.format(hourFields[0]))
        .append(":")
        .append(numberFormat.format(hourFields[1]));
    return timeZoneId.toString();
  }

  /**
   * Gets best possible timezone ID for a location. Returns a null if no good match is found.
   *
   * @param city City.
   * @param strCountry Country.
   * @return String, best possible time zone id.
   */
  private static String findBestTimeZoneId(final String city, final Country country) {

    if (city == null || city.length() == 0 || country == null) {
      return null;
    }

    final List<String> locationParts = new ArrayList<>();
    for (final String locationPart : city.split(",")) {
      locationParts.add(locationPart.trim().replaceAll(" |-", "_"));
    }
    if ("US".equals(country.alpha2Code()) && locationParts.size() >= 2) {
      final String stateString = locationParts.getLast();
      final Subdivision state = Subdivisions.lookupSubdivision("US-" + stateString);
      if (state != null) {
        // Replace state code with full state name
        locationParts.remove(stateString);
        locationParts.add(state.name());
      }
    }
    locationParts.add(country.name());

    String bestTimeZoneId = null;

    for (final Entry<String, List<String>> entry : allTimezoneIds.entrySet()) {
      final String timeZoneId = entry.getKey();
      final List<String> timeZoneParts = entry.getValue();
      final String locationPart1 = locationParts.getFirst().toLowerCase(Locale.ENGLISH);
      final String timeZonePart1 = timeZoneParts.getFirst().toLowerCase(Locale.ENGLISH);
      if (locationPart1.equals(timeZonePart1)) {
        if (timeZoneParts.size() <= 1) {
          bestTimeZoneId = timeZoneId;
          break;
        }
        final String locationPart2 = locationParts.get(1).toLowerCase(Locale.ENGLISH);
        final String timeZonePart2 = timeZoneParts.get(1).toLowerCase(Locale.ENGLISH);
        if (locationPart2.equals(timeZonePart2)) {
          bestTimeZoneId = timeZoneId;
          break;
        }
      }
    }

    return bestTimeZoneId;
  }

  private static List<String> splitTimeZoneId(final String timeZoneId) {
    final List<String> timeZoneParts = new ArrayList<>(Arrays.asList(timeZoneId.split("/")));
    // If the first part is not a country, it is a continent, so
    // remove it
    final String firstPart = timeZoneParts.getFirst();
    final Country country = Countries.lookupCountry(firstPart);
    if (country == null) {
      timeZoneParts.removeFirst(); // Remove the continent
    }
    Collections.reverse(timeZoneParts);
    return timeZoneParts;
  }
}
