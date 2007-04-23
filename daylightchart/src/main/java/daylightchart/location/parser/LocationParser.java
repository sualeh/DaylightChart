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

import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;
import org.pointlocation6709.Utility;
import org.pointlocation6709.parser.PointLocationParser;

import daylightchart.location.Countries;
import daylightchart.location.Country;
import daylightchart.location.Location;

/**
 * Parses locations.
 * 
 * @author Sualeh Fatehi
 */
public final class LocationParser
{

  /**
   * Calculates a non-DST aware timezone for a given longitude.
   * 
   * @param longitude
   *        Longitude
   * @return Time zone
   */
  public static TimeZone createTimeZoneForLongitude(final Longitude longitude)
  {

    if (longitude == null)
    {
      return null;
    }

    final boolean hoursOnly = true;

    final double tzOffsetHours = longitude.getDegrees() / 15D;
    final int[] fields = Utility.sexagesimalSplit(tzOffsetHours);
    String tzId = "GMT";

    if (fields[0] < 0)
    {
      tzId = tzId + "-";
    }
    else
    {
      tzId = tzId + "+";
    }
    int absHours = Math.abs(fields[0]);
    final int absMinutes = Math.abs(fields[1]);
    if (hoursOnly)
    {
      if (absMinutes >= 30)
      {
        absHours = absHours + 1;
      }
    }

    final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(2);
    tzId = tzId + numberFormat.format(absHours);
    if (!hoursOnly)
    {
      tzId = tzId + ":" + numberFormat.format(absMinutes);
    }

    return TimeZone.getTimeZone(tzId);

  }

  /**
   * Parses a string representation of a location.
   * 
   * @param representation
   *        String representation of a location
   * @return Location
   * @throws ParserException
   *         On a parse exception
   */
  public static Location parseLocation(final String representation)
    throws ParserException
  {
    if (representation == null || representation.length() == 0)
    {
      throw new ParserException("No location provided");
    }

    final String[] fields = representation.split(";");
    if (fields.length != 4)
    {
      throw new ParserException("Invalid location format: " + representation);
    }

    try
    {
      final String city = fields[0];
      final Country country = Countries.lookupCountry(fields[1]);
      final TimeZone timeZone = TimeZone.getTimeZone(fields[2]);
      final PointLocation pointLocation = PointLocationParser
        .parsePointLocation(fields[3]);

      final Location location = new Location(city,
                                             country,
                                             timeZone,
                                             pointLocation);
      return location;
    }
    catch (final org.pointlocation6709.parser.ParserException e)
    {
      throw new ParserException("Invalid location: " + representation, e);
    }

  }

  /**
   * Reads locations from a file.
   * 
   * @param locationsFile
   *        Locations file
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
    catch (final FileNotFoundException e)
    {
      throw new ParserException(e);
    }
  }

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
        line = line.trim();
        if (!line.startsWith("#"))
        {
          final Location location = parseLocation(line);
          locations.add(location);
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

  private LocationParser()
  {
  }

}
