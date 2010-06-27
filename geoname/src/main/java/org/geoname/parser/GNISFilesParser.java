/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2010, Sualeh Fatehi.
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.Location;
import org.pointlocation6709.Angle;
import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;

/**
 * Parses data files.
 * 
 * @author Sualeh Fatehi
 */
public final class GNISFilesParser
{

  private static final Logger LOGGER = Logger.getLogger(GNISFilesParser.class
    .getName());

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
      final List<Location> locations = new ArrayList<Location>();
      final BufferedReader reader = new BufferedReader(rdr);

      String line;
      while ((line = reader.readLine()) != null)
      {
        final String[] fields = line.split("\\|");
        if (fields.length != 18 && fields.length != 19)
        {
          continue;
        }

        final Country usa = Countries.lookupCountry("US");

        final String featureClass = fields[2];
        if (featureClass.equals("Populated Place"))
        {
          // City name is in the form: city, state
          final String city = fields[1] + ", " + fields[3];
          final String latitudeString = fields[9];
          final String longitudeString = fields[10];
          final String altitudeString = fields[15];

          final Latitude latitude = new Latitude(Angle.fromDegrees(Double
            .parseDouble(latitudeString)));
          final Longitude longitude = new Longitude(Angle.fromDegrees(Double
            .parseDouble(longitudeString)));
          double altitude;
          try
          {
            altitude = Double.parseDouble(altitudeString);
          }
          catch (NumberFormatException e)
          {
            altitude = 0;
          }
          if (latitude.getDegrees() != 0 && longitude.getDegrees() != 0)
          {
            final PointLocation pointLocation = new PointLocation(latitude,
                                                                  longitude,
                                                                  altitude);
            final String timeZoneId = DefaultTimezones
              .attemptTimeZoneMatch(city, usa, longitude);
            locations.add(new Location(city, usa, timeZoneId, pointLocation));
          }
        }
      }

      LOGGER.log(Level.INFO, "Loaded " + locations.size() + " locations");
      return new ArrayList<Location>(locations);
    }
    catch (final IOException e)
    {
      throw new ParserException("Invalid locations", e);
    }

  }

  private GNISFilesParser()
  {
  }

}
