/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2009, Sualeh Fatehi.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.geoname.data.Location;
import org.geoname.parser.GNISFilesParser;
import org.geoname.parser.GNSCountryFilesParser;
import org.geoname.parser.ParserException;
import org.geoname.parser.UnicodeReader;
import org.junit.Test;

public class TestGeoNameFiles
{

  @Test
  public void GNSCountries()
    throws ParserException, IOException
  {
    parseGNSCountryFile("in.zip", 25091);
    parseGNSCountryFile("lo.zip", 4606);
  }

  @Test
  public void GNISUSStates()
    throws ParserException, IOException
  {
    parseGNISUSStates("MA_Features_20081120.zip", 226);
    parseGNISUSStates("HI_Features_20081120.zip", 540);
  }

  private void parseGNISUSStates(final String filename, final int numLocations)
    throws ParserException, IOException
  {
    List<Location> locations = new ArrayList<Location>();
    String dataFilename = "";

    final InputStream dataStream = this.getClass().getClassLoader()
      .getResourceAsStream(filename);
    ZipInputStream zis = new ZipInputStream(dataStream);
    ZipEntry ze;
    if ((ze = zis.getNextEntry()) != null)
    {
      dataFilename = ze.getName();
      System.out.println(dataFilename);
      BufferedReader reader = new BufferedReader(new UnicodeReader(zis, "UTF-8"));
      locations = GNISFilesParser.parseLocations(reader);

      zis.closeEntry();
    }
    zis.close();

    assertEquals(String.format("Number of locations in file %s:%s",
                               filename,
                               dataFilename), numLocations, locations.size());
  }

  private void parseGNSCountryFile(final String filename, final int numLocations)
    throws ParserException, IOException
  {
    List<Location> locations = new ArrayList<Location>();
    String dataFilename = "";

    final InputStream dataStream = this.getClass().getClassLoader()
      .getResourceAsStream(filename);
    ZipInputStream zis = new ZipInputStream(dataStream);
    ZipEntry ze;
    if ((ze = zis.getNextEntry()) != null)
    {
      dataFilename = ze.getName();
      System.out.println(dataFilename);
      BufferedReader reader = new BufferedReader(new UnicodeReader(zis, "UTF-8"));
      locations = GNSCountryFilesParser.parseLocations(reader);

      zis.closeEntry();
    }
    zis.close();

    assertEquals(String.format("Number of locations in file %s:%s",
                               filename,
                               dataFilename), numLocations, locations.size());
  }
}
