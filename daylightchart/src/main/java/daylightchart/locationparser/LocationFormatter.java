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
package daylightchart.locationparser;


import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.pointlocation6709.parser.PointLocationFormatter;

import daylightchart.chart.Location;

/**
 * Formats objects to strings.
 * 
 * @author Sualeh Fatehi
 */
public final class LocationFormatter
{

  /**
   * Formats location to a string, in a parseable way.
   * 
   * @param location
   *        Location to format
   * @return Formated string.
   */
  public static String formatLocation(final Location location)
  {
    final String corordinatesString = PointLocationFormatter
      .formatIso6709(location.getPointLocation());
    final String tzId = location.getTimeZone().getID();

    final String city = location.getCity();
    final String country = location.getCountry();

    final StringBuffer representation = new StringBuffer().append(city)
      .append(";").append(country).append(";").append(tzId).append(";")
      .append(corordinatesString);
    return new String(representation);
  }

  /**
   * Formats a list of locations to a string.
   * 
   * @param locations
   *        Locations to format
   * @return String
   */
  public static String formatLocations(final List<Location> locations)
  {
    final StringWriter writer = new StringWriter();
    try
    {
      formatLocations(locations, writer);
    }
    catch (final IOException e)
    {
      // Ignore
    }
    return writer.toString();
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
    if (locations == null || writer == null)
    {
      return;
    }
    
    final String line_separator = System.getProperty("line.separator", "\n");
    for (final Location location: locations)
    {
      writer.write(formatLocation(location) + line_separator);
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
  public static String printLocationDetails(final Location location)
  {
    final String details = location.getPointLocation().toString() + ", "
                           + location.getTimeZone().getDisplayName();
    return details;
  }

  private LocationFormatter()
  {
  }

}
