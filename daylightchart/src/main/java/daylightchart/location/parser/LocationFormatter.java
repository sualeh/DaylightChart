/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.pointlocation6709.parser.FormatterException;
import org.pointlocation6709.parser.PointLocationFormatType;
import org.pointlocation6709.parser.PointLocationFormatter;

import daylightchart.location.Country;
import daylightchart.location.Location;

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
   * @throws daylightchart.location.parser.FormatterException
   */
  public static String formatLocation(final Location location)
    throws daylightchart.location.parser.FormatterException
  {
    String coordinatesString;
    try
    {
      coordinatesString = PointLocationFormatter.formatIso6709(location
        .getPointLocation(), PointLocationFormatType.MEDIUM);
    }
    catch (final FormatterException e)
    {
      throw new daylightchart.location.parser.FormatterException(e);
    }
    final String tzId = location.getTimeZoneId();

    final String city = location.getCity();
    final Country country = location.getCountry();

    final StringBuffer representation = new StringBuffer().append(city)
      .append(";").append(country.getIso3166Code2()).append(";").append(tzId)
      .append(";").append(coordinatesString);
    return new String(representation);
  }

  /**
   * Formats a list of locations to a string.
   * 
   * @param locations
   *        Locations to format
   * @return String
   * @throws daylightchart.location.parser.FormatterException
   */
  public static String formatLocations(final List<Location> locations)
    throws daylightchart.location.parser.FormatterException
  {
    final StringWriter writer = new StringWriter();
    formatLocations(locations, writer);
    return writer.toString();
  }

  /**
   * Formats a list of locations to a string.
   * 
   * @param locations
   *        Locations to format
   * @param file
   *        File to write to.
   * @throws daylightchart.location.parser.FormatterException
   */
  public static void formatLocations(final List<Location> locations,
                                     final File file)
    throws daylightchart.location.parser.FormatterException
  {
    if (file == null)
    {
      throw new daylightchart.location.parser.FormatterException("No file provided");
    }

    try
    {
      final FileWriter writer = new FileWriter(file);
      formatLocations(locations, writer);
    }
    catch (final IOException e)
    {
      throw new daylightchart.location.parser.FormatterException(e);
    }
  }

  /**
   * Writes location data out to a file. *
   * 
   * @param locations
   *        Locations to write.
   * @param writer
   *        Writer to write to.
   * @throws daylightchart.location.parser.FormatterException
   *         On an exception.
   */
  public static void formatLocations(final List<Location> locations,
                                     final Writer writer)
    throws daylightchart.location.parser.FormatterException
  {
    if (locations == null || writer == null)
    {
      return;
    }

    try
    {
      final String line_separator = System.getProperty("line.separator", "\n");
      for (final Location location: locations)
      {
        writer.write(formatLocation(location) + line_separator);
      }
      writer.flush();
      writer.close();
    }
    catch (final IOException e)
    {
      throw new daylightchart.location.parser.FormatterException(e);
    }
  }

  /**
   * Gets the tooltip for a location.
   * 
   * @param location
   *        Loaction.
   * @return Tooltip
   */
  public static String getToolTip(final Location location)
  {
    String toolTip = "";
    if (location != null)
    {
      toolTip = "<html><b>" + location.toString() + "</b><br>"
                + location.getDetails() + "</html>";
    }
    return toolTip;
  }

  private LocationFormatter()
  {
  }

}
