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
import org.joda.time.MutableDateTime;

import daylightchart.location.Location;

/**
 * Sunrise and sunset at a given location, and a given date.
 * 
 * @author Sualeh Fatehi
 */
public class RiseSet
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
  public static RiseSet[] splitAtMidnight(final RiseSet riseSet)
  {
    if (riseSet == null)
    {
      return new RiseSet[0];
    }

    final LocalDateTime sunrise = riseSet.getSunrise();
    final LocalDateTime sunset = riseSet.getSunset();
    if (sunset.getHourOfDay() < 12)
    {
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

      // Split the sunrise and sunset times
      return new RiseSet[] {
          new RiseSet(riseSet.getLocation(),
                      riseSet.getDate(),
                      new Hour(sunrise),
                      new Hour(beforeMidnight)),
          new RiseSet(riseSet.getLocation(),
                      riseSet.getDate(),
                      new Hour(afterMidnight),
                      new Hour(sunset))
      };
    }
    else
    {
      return new RiseSet[] {
        riseSet
      };
    }
  }

  private static LocalDateTime toLocalDateTime(final LocalDate date,
                                               final Hour hour)
  {
    final MutableDateTime mutableDate = date.toDateTime((LocalTime) null)
      .toMutableDateTime();
    mutableDate.setHourOfDay(hour.getLocalTime().getHourOfDay());
    mutableDate.setMinuteOfHour(hour.getLocalTime().getMinuteOfHour());
    return mutableDate.toDateTime().toLocalDateTime();
  }

  private final Location location;
  private final LocalDate date;
  private final Hour sunrise;
  private final Hour sunset;

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
  public RiseSet(final Location location,
                 final LocalDate date,
                 final Hour sunrise,
                 final Hour sunset)
  {
    this.location = location;
    this.date = date;
    this.sunrise = sunrise;
    this.sunset = sunset;
  }

  /**
   * Date.
   * 
   * @return Date
   */
  public LocalDate getDate()
  {
    return date;
  }

  /**
   * Location.
   * 
   * @return Location
   */
  public Location getLocation()
  {
    return location;
  }

  /**
   * Sunrise time.
   * 
   * @return Sunrise time
   */
  public LocalDateTime getSunrise()
  {
    return toLocalDateTime(date, sunrise);
  }

  /**
   * Sunset time.
   * 
   * @return Sunset time
   */
  public LocalDateTime getSunset()
  {
    return toLocalDateTime(date, sunset);
  }

  /**
   * Sets whether the daylight time is on.
   * 
   * @param usesDaylightTime
   *        Whether the daylight time is on
   */
  public void setUsesDaylightTime(final boolean usesDaylightTime)
  {
    sunrise.setInDaylightSavings(usesDaylightTime);
    sunset.setInDaylightSavings(usesDaylightTime);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return location.getDescription() + ": " + date + " - sunrise " + sunrise
           + " sunset " + sunset;
  }

}
