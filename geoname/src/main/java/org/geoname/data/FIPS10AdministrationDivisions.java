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
package org.geoname.data;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.geoname.parser.UnicodeReader;

/**
 * In-memory database of FIPS 10 administration divisions.
 *
 * @author Sualeh Fatehi
 */
public final class FIPS10AdministrationDivisions
{

  private static final Map<String, String> fips10AdministrationDivisionMap = new HashMap<String, String>();

  /**
   * Loads data from internal database.
   */
  static
  {
    try (BufferedReader reader = new BufferedReader(new UnicodeReader(FIPS10AdministrationDivisions.class
                                                                        .getClassLoader()
                                                                        .getResourceAsStream("fips10.data"),
                                                                      "UTF-8")))
    {
      reader
        .lines()
        .map(line -> line.split(";"))
        .filter(fields -> fields.length == 3)
        .filter(fields -> fields[0] != null && fields[1] != null
                          && fields[2] != null)
        .filter(fields -> fields[0].length() == 4)
        .forEach(fields -> fips10AdministrationDivisionMap.put(fields[0],
                                                               fields[2]));
    }
    catch (final IOException e)
    {
      throw new IllegalStateException("Cannot read data from internal database",
                                      e);
    }
  }

  /**
   * Looks up a FIPS-10 administration division name from the FIPS-10
   * administration division name.
   *
   * @param country
   *        Country
   * @param fips10AdministrationDivisionCode
   *        FIPS-10 administration division code
   * @return FIPS-10 administration division name, or null
   */
  public static String lookupFips10AdministrationDivisionName(final Country country,
                                                              final String fips10AdministrationDivisionCode)
  {
    if (country != null && fips10AdministrationDivisionCode != null)
    {
      final String fips10FullAdministrationDivisionCode = country.getCode()
                                                          + fips10AdministrationDivisionCode;
      return fips10AdministrationDivisionMap
        .get(fips10FullAdministrationDivisionCode);
    }
    else
    {
      return null;
    }
  }

}
