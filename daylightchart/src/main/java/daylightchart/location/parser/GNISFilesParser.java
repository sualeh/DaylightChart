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
import java.text.NumberFormat;
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
public final class GNISFilesParser
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

    try
    {
      List<Location> locations = new ArrayList<Location>();
      final BufferedReader reader = new BufferedReader(rdr);

      String line;
      while ((line = reader.readLine()) != null)
      {
        String[] fields = line.split("\t");
        if (fields.length != 17)
        {
          continue;
        }

        Country usa = Countries.lookupCountry("US");

        String featureClass = fields[2];
        if (featureClass.equals("Populated Place"))
        {
          String city = fields[1];
          String county = fields[5];
          String state = fields[3];

          String latitudeString = fields[9];
          String longitudeString = fields[10];
          String elevationString = fields[14];

          double longitudeDegrees = Double.parseDouble(longitudeString);

          Latitude latitude = new Latitude(Angle.fromDegrees(Double
            .parseDouble(latitudeString)));
          Longitude longitude = new Longitude(Angle
            .fromDegrees(longitudeDegrees));
          double elevation = 0;
          if (elevationString.trim().length() > 0)
          {
            Double.parseDouble(elevationString);
          }
          PointLocation pointLocation = new PointLocation(latitude,
                                                          longitude,
                                                          elevation);
          TimeZone timeZone = TimeZone
            .getTimeZone(formatTimeZone(longitudeDegrees));

          locations.add(new Location(city + ", " + county + ", " + state,
                                     usa,
                                     timeZone,
                                     pointLocation));
        }
      }
      reader.close();

      return new ArrayList<Location>(locations);
    }
    catch (final IOException e)
    {
      throw new ParserException("Invalid locations", e);
    }

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

  private static String formatTimeZone(double longitude)
  {

    double tzOffsetHours = longitude / 15D;
    int[] fields = Utility.sexagesimalSplit(tzOffsetHours);
    String tzId = "GMT";

    if (fields[0] < 0)
    {
      tzId = tzId + "-";
    }
    else
    {
      tzId = tzId + "+";
    }
    final int absHours = Math.abs(fields[0]);
    final int absMinutes = Math.abs(fields[1]);

    final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(2);
    tzId = tzId + numberFormat.format(absHours) + ":"
           + numberFormat.format(absMinutes);

    return tzId;

  }

  private GNISFilesParser()
  {
  }

}
