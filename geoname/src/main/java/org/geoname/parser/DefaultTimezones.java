/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2014, Sualeh Fatehi.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package org.geoname.parser;


import java.io.BufferedReader;
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

import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.USState;
import org.geoname.data.USStates;

import us.fatehi.pointlocation6709.Longitude;

/**
 * Time zone utilities.
 * 
 * @author Sualeh Fatehi
 */
public final class DefaultTimezones
{

  private static final Logger LOGGER = Logger.getLogger(DefaultTimezones.class
    .getName());

  private static final Map<Country, List<String>> defaultTimezones = new HashMap<Country, List<String>>();
  private static final Map<String, List<String>> allTimezoneIds = new HashMap<String, List<String>>();
  private static final Set<TimeZoneDisplay> allTimezones = new TreeSet<TimeZoneDisplay>();

  /**
   * Loads default time zones from the internal database. These are
   * default time zone ids for every time zone for each country.
   */
  static
  {
    BufferedReader reader = null;
    try
    {

      final List<Country> allCountries = Countries.getAllCountries();
      for (final Country country: allCountries)
      {
        defaultTimezones.put(country, new ArrayList<String>());
      }

      reader = new BufferedReader(new InputStreamReader(DefaultTimezones.class
        .getClassLoader().getResourceAsStream("default.timezones.data")));

      String line;
      while ((line = reader.readLine()) != null)
      {

        line = line.trim();
        if (line.startsWith("#"))
        {
          continue;
        }

        final String[] fields = line.split(",");

        final boolean invalidNumberOfFields = fields.length != 2;
        final boolean invalidHasNulls = fields[0] == null || fields[1] == null;
        final boolean invalidLengths = fields[0].length() != 2
                                       || fields[1].length() == 0;
        if (invalidNumberOfFields || invalidHasNulls || invalidLengths)
        {
          throw new IllegalArgumentException("Invalid default timezone record: "
                                             + line);
        }
        final String iso3166CountryCode2 = fields[0];
        final String timezoneId = fields[1];

        // Add default timezone for the country
        final ZoneId defaultZoneId = ZoneId.of(timezoneId);
        final Country country = Countries
          .lookupIso3166CountryCode2(iso3166CountryCode2);
        final List<String> defaultTimezonesForCountry = defaultTimezones
          .get(country);
        defaultTimezonesForCountry.add(defaultZoneId.getId());
      }
    }
    catch (final IOException e)
    {
      throw new IllegalStateException("Cannot read data from internal database",
                                      e);
    }
    finally
    {
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (final IOException e)
        {
          throw new IllegalStateException("Cannot read data from internal database",
                                          e);
        }
      }
    }
  }

  static
  {
    final Set<String> allTimeZoneIds = ZoneId.getAvailableZoneIds();
    for (final String timeZoneId: allTimeZoneIds)
    {
      if (timeZoneId.startsWith("Etc/"))
      {
        continue;
      }
      allTimezones.add(new TimeZoneDisplay(ZoneId.of(timeZoneId)));
      final List<String> timeZoneIdParts = splitTimeZoneId(timeZoneId);
      if (timeZoneIdParts.size() > 0)
      {
        allTimezoneIds.put(timeZoneId, timeZoneIdParts);
      }
    }
  }

  /**
   * Looks up a country from the provided string - whether a country
   * code or a country name. *
   * 
   * @param city
   *        City
   * @param country
   *        Country
   * @param longitude
   *        Longitude
   * @return Country ojbect, or null
   */
  public static String attemptTimeZoneMatch(final String city,
                                            final Country country,
                                            final Longitude longitude)
  {

    if (longitude == null)
    {
      return null;
    }

    // Timezone offset in hours
    double tzOffsetHours;
    tzOffsetHours = longitude.getDegrees() / 15D;
    tzOffsetHours = roundToNearestFraction(tzOffsetHours, 0.5D);

    // More than one timezone found
    String timeZoneId = null;
    // 1. Try a match by city name, in case there is a special
    // time zone for the city
    timeZoneId = findBestTimeZoneId(city, country);
    // 2. Check the country's default time zone
    if (timeZoneId == null)
    {
      final List<String> defaultTimezonesForCountry = defaultTimezones
        .get(country);
      if (defaultTimezonesForCountry == null
          || defaultTimezonesForCountry.size() == 0)
      {
        return createGMTTimeZoneId(tzOffsetHours);
      }
      else if (defaultTimezonesForCountry.size() == 1)
      {
        return defaultTimezonesForCountry.toArray(new String[1])[0];
      }
      // 3. Try a match by longitude, if no good default for
      // the country is found
      double leastDifference = Double.MAX_VALUE;
      for (final String defaultTimeZoneId: defaultTimezonesForCountry)
      {
        final double difference = Math
          .abs(getStandardTimeZoneOffsetHours(defaultTimeZoneId)
               - tzOffsetHours);
        if (difference < leastDifference)
        {
          leastDifference = difference;
          timeZoneId = defaultTimeZoneId;
        }
      }
    }

    LOGGER.log(Level.INFO, "Time zone id for \"" + city + ", " + country
                           + "\" is \"" + timeZoneId + "\"");

    return timeZoneId;

  }

  /**
   * Create a STANDARD GMT-based timezone id.
   * 
   * @param longitude
   *        Longitude
   * @return Time zone id string
   */
  public static String createGMTTimeZoneId(final Longitude longitude)
  {
    if (longitude == null)
    {
      return ZoneId.systemDefault().getId();
    }

    final double tzOffsetHours = longitude.getDegrees() / 15D;
    String timeZoneId = "GMT";
    if (tzOffsetHours < 0)
    {
      timeZoneId = timeZoneId + "-";
    }
    else
    {
      timeZoneId = timeZoneId + "+";
    }

    final int[] hourFields = Utility.sexagesimalSplit(Math.abs(tzOffsetHours));

    final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(2);

    timeZoneId = timeZoneId + numberFormat.format(hourFields[0]) + ":"
                 + numberFormat.format(hourFields[1]);
    return timeZoneId;
  }

  /**
   * Gets all time zones.
   * 
   * @return All time zones
   */
  public static List<TimeZoneDisplay> getAllTimeZonesForDisplay()
  {
    final ArrayList<TimeZoneDisplay> allTimeZonesList = new ArrayList<TimeZoneDisplay>(allTimezones);
    Collections.sort(allTimeZonesList);
    return allTimeZonesList;
  }

  /**
   * Calculates the STANDARD time zone offset, in hours.
   * 
   * @param timeZoneId
   *        Time zone id
   * @return Time zone offset, in hours
   */
  public static double getStandardTimeZoneOffsetHours(final String timeZoneId)
  {
    if (timeZoneId == null)
    {
      return 0D;
    }
    final ZoneId zoneId = ZoneId.of(timeZoneId);
    return zoneId.getRules().getStandardOffset(Instant.now()).getTotalSeconds()
           / (60D * 60D);
  }

  /**
   * Utility to round a number to the nearest half.
   * 
   * @param number
   *        Number to round
   * @param fraction
   *        Fraction to round to
   * @return Rounded numbers
   */
  public static double roundToNearestFraction(final double number,
                                              final double fraction)
  {
    return new BigDecimal(number / fraction)
      .round(new MathContext(1, RoundingMode.HALF_UP)).doubleValue()
           * fraction;
  }

  /**
   * Gets the map of default time zones.
   * 
   * @return Default time zones map
   */
  static Map<Country, List<String>> getMap()
  {
    return new HashMap<Country, List<String>>(defaultTimezones);
  }

  /**
   * Create a STANDARD GMT-based timezone id.
   * 
   * @param tzOffsetHours
   *        Time zone offset, in hours
   * @return Time zone id string
   */
  private static String createGMTTimeZoneId(final double tzOffsetHours)
  {
    String timeZoneId = "GMT";
    if (tzOffsetHours < 0)
    {
      timeZoneId = timeZoneId + "-";
    }
    else
    {
      timeZoneId = timeZoneId + "+";
    }

    final int[] hourFields = Utility.sexagesimalSplit(Math.abs(tzOffsetHours));

    final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(2);

    timeZoneId = timeZoneId + numberFormat.format(hourFields[0]) + ":"
                 + numberFormat.format(hourFields[1]);
    return timeZoneId;
  }

  /**
   * Gets best possible timezone ID for a location. Returns a null if no
   * good match is found.
   * 
   * @param city
   *        City.
   * @param strCountry
   *        Country.
   * @return String, best possible time zone id.
   */
  private static String findBestTimeZoneId(final String city,
                                           final Country country)
  {

    if (city == null || city.length() == 0)
    {
      return null;
    }
    if (country == null)
    {
      return null;
    }

    final List<String> locationParts = new ArrayList<String>();
    for (final String locationPart: city.split(","))
    {
      locationParts.add(locationPart.trim().replaceAll(" |-", "_"));
    }
    if (country.getCode().equals("US") && locationParts.size() >= 2)
    {
      final String stateString = locationParts.get(locationParts.size() - 1);
      final USState state = USStates.lookupUSState(stateString);
      if (state != null)
      {
        // Replace state code with full state name
        locationParts.remove(stateString);
        locationParts.add(state.getName());
      }
    }
    locationParts.add(country.getName());

    String bestTimeZoneId = null;

    for (final Entry<String, List<String>> entry: allTimezoneIds.entrySet())
    {
      final String timeZoneId = entry.getKey();
      final List<String> timeZoneParts = entry.getValue();
      final String locationPart1 = locationParts.get(0)
        .toLowerCase(Locale.ENGLISH);
      final String timeZonePart1 = timeZoneParts.get(0)
        .toLowerCase(Locale.ENGLISH);
      if (locationPart1.equals(timeZonePart1))
      {
        if (timeZoneParts.size() > 1)
        {
          final String locationPart2 = locationParts.get(1)
            .toLowerCase(Locale.ENGLISH);
          final String timeZonePart2 = timeZoneParts.get(1)
            .toLowerCase(Locale.ENGLISH);
          if (locationPart2.equals(timeZonePart2))
          {
            bestTimeZoneId = timeZoneId;
            break;
          }
        }
        else
        {
          bestTimeZoneId = timeZoneId;
          break;
        }
      }
    }

    return bestTimeZoneId;

  }

  private static List<String> splitTimeZoneId(final String timeZoneId)
  {
    final List<String> timeZoneParts = new ArrayList<String>();
    timeZoneParts.addAll(Arrays.asList(timeZoneId.split("/")));
    // If the first part is not a country, it is a continent, so
    // remove it
    final String firstPart = timeZoneParts.get(0);
    final Country country = Countries.lookupCountry(firstPart);
    if (country == null)
    {
      timeZoneParts.remove(0); // Remove the continent
    }
    Collections.reverse(timeZoneParts);
    return timeZoneParts;
  }

}
