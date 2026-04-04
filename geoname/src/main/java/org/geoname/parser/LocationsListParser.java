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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.Location;
import org.geoname.data.Subdivision;
import org.geoname.data.Subdivisions;
import org.geoname.parser.resources.ResourceRef;
import us.fatehi.pointlocation6709.PointLocation;
import us.fatehi.pointlocation6709.parse.PointLocationParser;

/** Parses locations. */
public final class LocationsListParser implements LocationsParser {
  private static final Logger LOGGER = Logger.getLogger(LocationsListParser.class.getName());

  private static final CSVFormat RECORD_FORMAT =
      CSVFormat.DEFAULT
          .builder()
          .setDelimiter(';')
          .setHeader("city", "admin_code", "country_code", "timezone", "coordinates")
          .get();

  private static final CSVFormat FILE_FORMAT =
      CSVFormat.DEFAULT
          .builder()
          .setDelimiter(';')
          .setHeader()
          .setSkipHeaderRecord(true)
          .setCommentMarker('#')
          .setIgnoreEmptyLines(true)
          .get();

  /**
   * Parses a string representation of a location.
   *
   * @param representation String representation of a location
   * @return Location
   * @throws ParserException On a parse exception
   */
  public static Location parseLocation(final String representation) throws ParserException {
    if (representation == null || representation.isEmpty()) {
      throw new ParserException("No location provided");
    }
    try (CSVParser csvParser = RECORD_FORMAT.parse(new StringReader(representation))) {
      final List<CSVRecord> records = csvParser.getRecords();
      if (records.isEmpty()) {
        throw new ParserException("Invalid location format: " + representation);
      }
      return toLocation(records.get(0), representation);
    } catch (final IOException e) {
      throw new ParserException("Invalid location: " + representation, e);
    }
  }

  private static Location toLocation(final CSVRecord record, final String representation)
      throws ParserException {
    try {
      final String city = record.get("city");
      final String admCode = record.get("admin_code").trim();
      final Subdivision subdivision =
          admCode.isEmpty() ? null : Subdivisions.lookupSubdivision(admCode);
      final Country country = Countries.lookupCountry(record.get("country_code"));
      final String timeZoneId = record.get("timezone");
      final PointLocation pointLocation =
          PointLocationParser.parsePointLocation(record.get("coordinates"));
      return new Location(city, subdivision, country, timeZoneId, pointLocation);
    } catch (final us.fatehi.pointlocation6709.parse.ParserException e) {
      throw new ParserException("Invalid location: " + representation, e);
    }
  }

  private final ResourceRef resourceRef;

  public LocationsListParser(final ResourceRef resourceRef) throws ParserException {
    if (resourceRef == null) {
      throw new ParserException("Cannot read locations");
    }
    this.resourceRef = resourceRef;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.geoname.parser.LocationsParser#parseLocations()
   */
  @Override
  public Collection<Location> parseLocations() throws ParserException {
    final List<Location> locations = new ArrayList<>();
    try (InputStream stream = resourceRef.openStream();
        CSVParser csvParser = FILE_FORMAT.parse(new InputStreamReader(stream, "UTF-8"))) {
      final Set<String> seen = new HashSet<>();
      for (final CSVRecord record : csvParser) {
        try {
          final Location location = toLocation(record, record.toString());
          if (seen.add(location.deduplicationKey())) {
            locations.add(location);
          }
        } catch (final ParserException e) {
          LOGGER.log(Level.WARNING, "Skipping invalid location record: " + record, e);
        }
      }
    } catch (final IOException e) {
      throw new ParserException("Invalid locations", e);
    }

    LOGGER.log(Level.INFO, "Loaded " + locations.size() + " locations");
    return locations;
  }
}
