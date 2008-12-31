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
package org.geoname.parser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.geoname.Country;
import org.geoname.Location;
import org.pointlocation6709.PointLocation;
import org.pointlocation6709.parser.PointLocationParser;


/**
 * Parses locations.
 * 
 * @author Sualeh Fatehi
 */
public final class LocationParser
{

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
      final String timeZoneId = fields[2];
      final PointLocation pointLocation = PointLocationParser
        .parsePointLocation(fields[3]);

      final Location location = new Location(city,
                                             country,
                                             timeZoneId,
                                             pointLocation);
      return location;
    }
    catch (final org.pointlocation6709.parser.ParserException e)
    {
      throw new ParserException("Invalid location: " + representation, e);
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
    }
    catch (final IOException e)
    {
      throw new ParserException("Invalid locations", e);
    }

    return new ArrayList<Location>(locations);

  }

  private LocationParser()
  {
  }

}
