/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.parser;

import java.util.Collection;
import org.geoname.data.Location;

/** Parses locations files. */
public interface LocationsParser {

  /**
   * Parse a list of locations from a source.
   *
   * @return List of locations
   * @throws ParserException On an exception
   */
  Collection<Location> parseLocations() throws ParserException;
}
