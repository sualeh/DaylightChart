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
package daylightchart.location;


import java.io.IOException;
import java.io.Writer;
import java.util.List;

import daylightchart.iso6709.PointLocationFormatter;

/**
 * Formats objects to strings.
 * 
 * @author Sualeh Fatehi
 */
public class LocationFormatter
{

  /**
   * Formats location to a string, in a parseable way.
   * 
   * @param location
   *        Location to format
   * @return Formated string.
   */
  public final static String formatLocation(final Location location)
  {
    final String corordinatesString = PointLocationFormatter
      .formatIso6709PointLocation(location.getCoordinates());
    final String tzId = location.getTimeZone().getID();

    final String city = location.getCity();
    final String country = location.getCountry();

    final StringBuffer representation = new StringBuffer().append(city)
      .append(";").append(country).append(";").append(tzId).append(";")
      .append(corordinatesString);
    return new String(representation);
  }

  /**
   * Writes location data out to a file. *
   * 
   * @param locations
   *        Locations to write.
   * @param writer
   *        Writer to write to.
   * @throws IOException
   *         On an exception.
   */
  public static void formatLocations(final List<Location> locations,
                                     final Writer writer)
    throws IOException
  {
    final String LINE_BREAK = System.getProperty("line.separator", "\n");
    for (final Location location: locations)
    {
      writer.write(formatLocation(location) + LINE_BREAK);
    }
    writer.flush();
    writer.close();
  }

  /**
   * Details for the location.
   * 
   * @param location
   *        Location
   * @return Details for this location.
   */
  public final static String printLocationDetails(final Location location)
  {
    final String details = location.getCoordinates().getLatitude().toString()
                           + " - "
                           + location.getCoordinates().getLongitude()
                             .toString() + ", "
                           + location.getTimeZone().getDisplayName();
    return details;
  }

  private LocationFormatter()
  {
  }

}
