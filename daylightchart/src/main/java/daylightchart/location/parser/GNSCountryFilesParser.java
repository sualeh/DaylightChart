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
package daylightchart.location.parser;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.pointlocation6709.Angle;
import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;
import org.pointlocation6709.Utility;

import daylightchart.location.Countries;
import daylightchart.location.Country;
import daylightchart.location.Location;

/**
 * Parses objects from strings.
 * 
 * @author Sualeh Fatehi
 */
public final class GNSCountryFilesParser
{

  /**
   * Reads locations from a reader.
   * 
   * @param rdr
   *        Reader
   * @return List of locations
   * @throws ParserException
   *         On a parse exception
   */
  public static List<Location> parseLocations(final Reader rdr)
    throws ParserException
  {

    if (rdr == null)
    {
      throw new ParserException("Cannot read locations");
    }

    List<Location> locations;
    try
    {
      locations = new ArrayList<Location>();
      final BufferedReader reader = new BufferedReader(rdr);

      String line;
      while ((line = reader.readLine()) != null)
      {
        String[] fields = line.split("\t");
        if (fields.length != 25)
        {
          continue;
        }

        String featureClassification = fields[9];
        if (featureClassification.equalsIgnoreCase("P"))
        {
          String latitudeString = fields[3];
          String longitudeString = fields[4];
          String fips10CountryCode = fields[12];
          String city = fields[22];

          double longitudeDegrees = Double.parseDouble(longitudeString);

          Country country = Countries
            .lookupFips10CountryCode(fips10CountryCode);
          Latitude latitude = new Latitude(Angle.fromDegrees(Double
            .parseDouble(latitudeString)));
          Longitude longitude = new Longitude(Angle
            .fromDegrees(longitudeDegrees));
          PointLocation pointLocation = new PointLocation(latitude, longitude);

          int[] sexagesimalSplit = Utility.sexagesimalSplit(longitudeDegrees/ 15D);
          TimeZone timeZone = TimeZone.getTimeZone("GMT");

          locations.add(new Location(city, country, timeZone, pointLocation));
        }
      }
      reader.close();
    }
    catch (final IOException e)
    {
      throw new ParserException("Invalid locations", e);
    }

    return new ArrayList<Location>(locations);

  }

  /**
   * Reads locations from a String.
   * 
   * @param locationsString
   *        Locations string
   * @return List of locations
   * @throws ParserException
   *         On a parse exception
   */
  public static List<Location> parseLocations(final String locationsString)
    throws ParserException
  {
    if (locationsString == null)
    {
      throw new ParserException("Cannot read file");
    }
    return parseLocations(new StringReader(locationsString));
  }

  /**
   * Reads locations from a reader.
   * 
   * @param locationsFile
   *        Locations string
   * @return List of locations
   * @throws ParserException
   *         On a parse exception
   */
  public static List<Location> parseLocations(final File locationsFile)
    throws ParserException
  {
    if (locationsFile == null || !locationsFile.exists()
        || !locationsFile.canRead())
    {
      throw new ParserException("Cannot read file");
    }
    try
    {
      return parseLocations(new FileReader(locationsFile));
    }
    catch (FileNotFoundException e)
    {
      throw new ParserException(e);
    }
  }

  private GNSCountryFilesParser()
  {
  }

}
