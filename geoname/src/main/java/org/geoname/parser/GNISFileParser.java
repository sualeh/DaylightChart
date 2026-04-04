/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.parser;

import java.util.Map;
import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.Location;
import org.geoname.data.Subdivision;
import org.geoname.data.Subdivisions;
import org.geoname.parser.resources.ResourceRef;

/** Parses data files. */
public final class GNISFileParser extends BaseDelimitedLocationsFileParser {

  private static final Country USA = Countries.lookupCountry("US");

  public GNISFileParser(final ResourceRef resourceRef) throws ParserException {
    super(resourceRef, ',');
  }

  @Override
  protected Location parseLocation(final Map<String, String> locationDataMap) {
    if (locationDataMap == null) {
      return null;
    }

    final String featureClass = locationDataMap.get("Class");
    if (featureClass == null || !"Populated Place".equals(featureClass)) {
      return null;
    }
    try {
      final String city = locationDataMap.get("Feature Name");
      final String stateCode = locationDataMap.get("State");
      final Subdivision subdivision = Subdivisions.lookupSubdivisionName(stateCode);

      final Location location =
          getLocation(
              locationDataMap, city, subdivision, USA, "Latitude", "Longitude", "Elevation");
      return location;
    } catch (final ParserException e) {
      return null;
    }
  }
}
