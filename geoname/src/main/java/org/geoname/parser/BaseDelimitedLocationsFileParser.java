/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.geoname.data.Country;
import org.geoname.data.Location;
import org.geoname.data.Subdivision;
import org.geoname.parser.resources.ResourceRef;
import org.geoname.timezones.DefaultTimezones;
import us.fatehi.pointlocation6709.Angle;
import us.fatehi.pointlocation6709.Latitude;
import us.fatehi.pointlocation6709.Longitude;
import us.fatehi.pointlocation6709.PointLocation;

abstract class BaseDelimitedLocationsFileParser implements LocationsParser {
  private static final Logger LOGGER =
      Logger.getLogger(BaseDelimitedLocationsFileParser.class.getName());

  private final ResourceRef resourceRef;
  private final char delimiter;

  protected BaseDelimitedLocationsFileParser(final ResourceRef resourceRef, final char delimiter)
      throws ParserException {
    if (resourceRef == null) {
      throw new ParserException("Cannot read locations");
    }
    this.resourceRef = resourceRef;
    this.delimiter = delimiter;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.geoname.parser.LocationsParser#parseLocations()
   */
  @Override
  public final Collection<Location> parseLocations() throws ParserException {
    final List<Location> locations = new ArrayList<>();
    final CSVFormat format =
        CSVFormat.DEFAULT
            .builder()
            .setDelimiter(delimiter)
            .setQuote(null)
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(true)
            .get();
    try (InputStream stream = resourceRef.openStream();
        CSVParser csvParser = format.parse(new InputStreamReader(stream, "UTF-8"))) {
      final Set<String> seen = new HashSet<>();
      for (final CSVRecord record : csvParser) {
        final Map<String, String> locationDataMap = record.toMap();
        final Location location = parseLocation(locationDataMap);
        if (location != null) {
          if (seen.add(location.deduplicationKey())) {
            locations.add(location);
          }
        }
      }
    } catch (final IOException e) {
      throw new ParserException("Invalid locations", e);
    }

    LOGGER.log(Level.INFO, "Loaded " + locations.size() + " locations");
    return locations;
  }

  protected final double getDouble(
      final Map<String, String> locationDataMap, final String key, final double defaultValue) {
    double doubleValue = defaultValue;
    try {
      doubleValue = getDouble(locationDataMap, key);
    } catch (final ParserException e) {
      doubleValue = defaultValue;
    }
    return doubleValue;
  }

  protected final int getInteger(
      final Map<String, String> locationDataMap, final String key, final int defaultValue) {
    int integerValue = defaultValue;
    if (locationDataMap != null && locationDataMap.containsKey(key)) {
      try {
        integerValue = Integer.parseInt(locationDataMap.get(key));
      } catch (final NumberFormatException e) {
        integerValue = defaultValue;
      }
    }
    return integerValue;
  }

  protected final Location getLocation(
      final Map<String, String> locationDataMap,
      final String city,
      final Country country,
      final String latitudeKey,
      final String longitudeKey,
      final String altitudeKey)
      throws ParserException {
    return getLocation(
        locationDataMap, city, null, country, latitudeKey, longitudeKey, altitudeKey);
  }

  protected final Location getLocation(
      final Map<String, String> locationDataMap,
      final String city,
      final Subdivision subdivision,
      final Country country,
      final String latitudeKey,
      final String longitudeKey,
      final String altitudeKey)
      throws ParserException {
    final Latitude latitude =
        new Latitude(Angle.fromDegrees(getDouble(locationDataMap, latitudeKey)));
    final Longitude longitude =
        new Longitude(Angle.fromDegrees(getDouble(locationDataMap, longitudeKey)));
    final double altitude = getDouble(locationDataMap, altitudeKey, 0D);

    final PointLocation pointLocation = new PointLocation(latitude, longitude, altitude, "");

    final String timeZoneId =
        DefaultTimezones.attemptTimeZoneMatch(city, country, pointLocation.getLongitude());

    try {
      return new Location(city, subdivision, country, timeZoneId, pointLocation);
    } catch (final IllegalArgumentException e) {
      throw new ParserException("Could not get location", e);
    }
  }

  protected abstract Location parseLocation(final Map<String, String> locationDataMap);

  private final double getDouble(final Map<String, String> locationDataMap, final String key)
      throws ParserException {
    if (locationDataMap == null || !locationDataMap.containsKey(key)) {
      throw new ParserException("No value for key " + key);
    }

    try {
      return Double.parseDouble(locationDataMap.get(key));
    } catch (final NumberFormatException e) {
      throw new ParserException("Bad value for key " + key, e);
    }
  }
}
