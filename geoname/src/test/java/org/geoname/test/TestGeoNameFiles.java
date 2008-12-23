/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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
package org.geoname.test;


import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.geoname.Location;
import org.geoname.parser.GNISFilesParser;
import org.geoname.parser.GNSCountryFilesParser;
import org.geoname.parser.ParserException;
import org.junit.Test;

public class TestGeoNameFiles
{

  @Test
  public void countries()
    throws ParserException
  {
    parseGNSCountryFile("lo.txt", 4574);
    parseGNSCountryFile("ar.txt", 5467);
  }

  @Test
  public void GNISUSStates()
    throws ParserException
  {
    parseGNISUSStates("IN_DECI.txt", 3451);
  }

  private void parseGNISUSStates(final String filename, final int numLocations)
    throws ParserException
  {
    final InputStream dataStream = this.getClass().getClassLoader()
      .getResourceAsStream(filename);
    final InputStreamReader reader = new InputStreamReader(dataStream);
    final List<Location> locations = GNISFilesParser.parseLocations(reader);

    assertEquals(numLocations, locations.size());
  }

  private void parseGNSCountryFile(final String filename, final int numLocations)
    throws ParserException
  {
    final InputStream dataStream = this.getClass().getClassLoader()
      .getResourceAsStream(filename);
    final InputStreamReader reader = new InputStreamReader(dataStream);
    final List<Location> locations = GNSCountryFilesParser
      .parseLocations(reader);

    assertEquals(numLocations, locations.size());
  }

}
