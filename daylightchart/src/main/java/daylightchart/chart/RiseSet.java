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
package daylightchart.chart;


import java.io.Serializable;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import daylightchart.location.Location;

/**
 * Sunrise and sunset at a given location, and a given date.
 * 
 * @author Sualeh Fatehi
 */
final class RiseSet
  implements Serializable
{

  private static final long serialVersionUID = 3092668888760029582L;

  /**
   * Splits the given RiseSet at midnight.
   * 
   * @param riseSet
   *        RiseSet to split
   * @return Split RiseSet(s)
   */
  static RiseSet[] splitAtMidnight(final RiseSet riseSet)
  {
    if (riseSet == null)
    {
      return new RiseSet[0];
    }

    final LocalDateTime sunrise = riseSet.getSunrise();
    final LocalDateTime sunset = riseSet.getSunset();

    final LocalDateTime beforeMidnight = new LocalDateTime(sunrise.getYear(),
                                                           sunrise
                                                             .getMonthOfYear(),
                                                           sunrise
                                                             .getDayOfMonth(),
                                                           23,
                                                           59,
                                                           59,
                                                           999);
    final LocalDateTime afterMidnight = new LocalDateTime(sunrise.getYear(),
                                                          sunrise
                                                            .getMonthOfYear(),
                                                          sunrise
                                                            .getDayOfMonth(),
                                                          0,
                                                          0,
                                                          0,
                                                          1);

    if (sunset.getHourOfDay() < 12)
    {
      return new RiseSet[] {
          riseSet.copy(sunrise.toLocalTime(), beforeMidnight.toLocalTime()),
          riseSet.copy(afterMidnight.toLocalTime(), sunset.toLocalTime())
      };
    }
    else if (sunrise.getHourOfDay() > 12)
    {
      return new RiseSet[] {
          riseSet.copy(afterMidnight.toLocalTime(), sunset.toLocalTime()),
          riseSet.copy(sunrise.toLocalTime(), beforeMidnight.toLocalTime())
      };
    }
    else
    {
      return new RiseSet[] {
        riseSet
      };
    }
  }

  private final Location location;
  private final LocalDate date;
  private final boolean inDaylightSavings;
  private final LocalTime sunrise;
  private final LocalTime sunset;

  /**
   * Sunrise and sunset at a given location, and a given date.
   * 
   * @param location
   *        Location
   * @param date
   *        Date
   * @param sunrise
   *        Sunrise time
   * @param sunset
   *        Sunset time
   */
  RiseSet(final Location location,
          final LocalDate date,
          final boolean inDaylightSavings,
          final LocalTime sunrise,
          final LocalTime sunset)
  {
    this.location = location;
    this.date = date;
    this.inDaylightSavings = inDaylightSavings;
    this.sunrise = sunrise;
    this.sunset = sunset;
  }

  public RiseSet copy(final boolean adjustedForDaylightSavings)
  {
    if (adjustedForDaylightSavings || !isInDaylightSavings())
    {
      return this;
    }
    else
    {
      return new RiseSet(this.location, this.date, false, this.sunrise
        .minusHours(1), this.sunset.minusHours(1));
    }
  }

  public RiseSet copy(final LocalTime sunrise, final LocalTime sunset)
  {
    return new RiseSet(location, date, inDaylightSavings, sunrise, sunset);
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
      return location.getDescription() + ": " + date + " - sunrise " + sunrise
             + " sunset " + sunset;
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
  LocalDateTime getSunrise()
  {
    return date.toDateTime(sunrise).toLocalDateTime();
  }

  /**
   * Sunset time.
   * 
   * @return Sunset time
   */
  LocalDateTime getSunset()
  {
    return date.toDateTime(sunset).toLocalDateTime();
  }

  /**
   * Whether this rise/ set pair is using daylight savings time.
   * 
   * @return is in daylight savings time
   */
  boolean isInDaylightSavings()
  {
    return inDaylightSavings;
  }

}
