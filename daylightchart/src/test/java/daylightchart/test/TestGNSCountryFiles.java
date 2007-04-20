/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
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
package daylightchart.test;


import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import daylightchart.location.Location;
import daylightchart.location.parser.GNSCountryFilesParser;
import daylightchart.location.parser.ParserException;

/**
 * Location tests.
 */
public class TestGNSCountryFiles
{

  @Test
  @Ignore
  public void locations()
    throws ParserException
  {
    final InputStream dataStream = this.getClass().getClassLoader()
      .getResourceAsStream("lo.txt");
    final InputStreamReader reader = new InputStreamReader(dataStream);
    List<Location> locations = GNSCountryFilesParser.parseLocations(reader);

    assertEquals(4574, locations.size());
  }

}
