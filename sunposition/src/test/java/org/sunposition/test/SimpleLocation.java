/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2012, Sualeh Fatehi.
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
package org.sunposition.test;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.TimeZone;

import org.pointlocation6709.PointLocation;
import org.pointlocation6709.parser.ParserException;
import org.pointlocation6709.parser.PointLocationParser;

/**
 * A location object has all the information required to define a
 * location, such as the name of the city and the country, the point
 * location, and the time zone.
 * 
 * @author Sualeh Fatehi
 */
final class SimpleLocation
{

  private final String location;
  private final PointLocation pointLocation;
  private final String timeZoneId;

  SimpleLocation(final String representation)
  {
    final String[] fields = representation.split(";");
    if (fields.length != 4)
    {
      throw new IllegalArgumentException("Invalid location format: "
                                         + representation);
    }

    this.location = fields[0] + ", " + fields[1];
    this.timeZoneId = fields[2];
    try
    {
      this.pointLocation = PointLocationParser.parsePointLocation(fields[3]);
    }
    catch (ParserException e)
    {
      throw new IllegalArgumentException("Invalid point location format: "
                                         + representation);
    }
  }

  String getLocation()
  {
    return location;
  }

  double getLatitude()
  {
    return pointLocation.getLatitude().getDegrees();
  }

  double getLongitude()
  {
    return pointLocation.getLongitude().getDegrees();
  }

  /**
   * Calculates the standard time zone offset, in hours.
   * 
   * @return Time zone offset, in hours
   */
  double getTimeZoneOffset()
  {
    final TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
    return timeZone.getRawOffset() / (60D * 60D * 1000D);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    new PrintStream(outputStream, true).printf("%s, %s (%3.2f), %s",
                                               location,
                                               timeZoneId,
                                               getTimeZoneOffset(),
                                               pointLocation);
    return outputStream.toString();
  }

}
