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
package daylightchart.chart.calculation;


import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import org.joda.time.LocalDate;

import daylightchart.location.Location;

/**
 * Sunrise and sunset at a given location, and a given date. RiseSet is
 * calculated as part of the rise/ set year, and then may be split for
 * creating the daylight bands.
 * 
 * @author Sualeh Fatehi
 */
final class RiseSetTuple
  implements Serializable
{

  private static final long serialVersionUID = 3092668888760029582L;

  private final Location location;
  private final LocalDate date;
  private final boolean inDaylightSavings;
  private final double sunrise;
  private final double sunset;

  RiseSetTuple(final Location location,
               final LocalDate date,
               final boolean inDaylightSavings,
               final double sunrise,
               final double sunset)
  {
    this.location = location;
    this.date = date;
    this.inDaylightSavings = inDaylightSavings;

    this.sunrise = sunrise;
    this.sunset = sunset;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    if (location == null)
    {
      return "";
    }
    else
    {
      final StringWriter writer = new StringWriter();
      new PrintWriter(writer, true)
        .printf("%s, %s: sunrise %6.3f sunset %6.4f",
                location.getDescription(),
                date,
                sunrise,
                sunset);
      return writer.toString();
    }
  }

  /**
   * Date.
   * 
   * @return Date
   */
  LocalDate getDate()
  {
    return date;
  }

  /**
   * Location.
   * 
   * @return Location
   */
  Location getLocation()
  {
    return location;
  }

  /**
   * Sunrise time.
   * 
   * @return Sunrise time
   */
  double getSunrise()
  {
    return sunrise;
  }

  /**
   * Sunset time.
   * 
   * @return Sunset time
   */
  double getSunset()
  {
    return sunset;
  }

  boolean isInDaylightSavings()
  {
    return inDaylightSavings;
  }

}
