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
package org.geoname.test;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.geoname.data.Location;
import org.geoname.parser.GNISFileParser;
import org.geoname.parser.GNSCountryFileParser;
import org.geoname.parser.ParserException;
import org.junit.Test;

public class TestGeoNameFiles
{

  @Test
  public void GNISUSStates()
    throws ParserException, IOException
  {
    final String date = "20100607";
    parseGNISUSStates("MA", date, 2428);
    parseGNISUSStates("HI", date, 541);
  }

  @Test
  public void GNSCountries()
    throws ParserException, IOException
  {
    parseGNSCountryFile("uz.zip", 3757);
    parseGNSCountryFile("lo.zip", 4969);
  }

  private void parseGNISUSStates(final String state,
                                 final String date,
                                 final int numLocations)
    throws ParserException, IOException
  {
    final String filename = state + "_Features_" + date + ".zip";
    List<Location> locations = new ArrayList<Location>();

    final InputStream dataStream = this.getClass().getClassLoader()
      .getResourceAsStream(filename);
    ZipInputStream zis = new ZipInputStream(dataStream);
    ZipEntry ze;
    if ((ze = zis.getNextEntry()) != null)
    {
      System.out.println(ze);
      final GNISFileParser parser = new GNISFileParser(zis);
      locations = parser.parseLocations();
    }
    zis.close();

    assertEquals(String.format("Number of locations in file %s:%s",
                               filename,
                               ze), numLocations, locations.size());
  }

  private void parseGNSCountryFile(final String filename, final int numLocations)
    throws ParserException, IOException
  {
    List<Location> locations = new ArrayList<Location>();

    final InputStream dataStream = this.getClass().getClassLoader()
      .getResourceAsStream(filename);
    ZipInputStream zis = new ZipInputStream(dataStream);
    ZipEntry ze;
    if ((ze = zis.getNextEntry()) != null)
    {
      System.out.println(ze);
      final GNSCountryFileParser parser = new GNSCountryFileParser(zis);
      locations = parser.parseLocations();
    }
    zis.close();

    assertEquals(String.format("Number of locations in file %s:%s",
                               filename,
                               ze), numLocations, locations.size());
  }
}
