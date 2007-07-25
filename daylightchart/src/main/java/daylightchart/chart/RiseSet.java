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

  enum RiseSetType
  {
    normal,
    partial,
    all_daylight,
    all_nighttime;
  }

  private static final LocalTime JUST_BEFORE_MIDNIGHT = LocalTime.MIDNIGHT
    .minusMillis(1);

  private static final LocalTime JUST_AFTER_MIDNIGHT = LocalTime.MIDNIGHT
    .plusMillis(1);

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

    if (sunset.getHourOfDay() < 9)
    {
      return new RiseSet[] {
          riseSet.withNewRiseSetTimes(sunrise.toLocalTime(),
                                      JUST_BEFORE_MIDNIGHT),
          riseSet
            .withNewRiseSetTimes(JUST_AFTER_MIDNIGHT, sunset.toLocalTime())
      };
    }
    else if (sunrise.getHourOfDay() > 15)
    {
      return new RiseSet[] {
          riseSet
            .withNewRiseSetTimes(JUST_AFTER_MIDNIGHT, sunset.toLocalTime()),
          riseSet.withNewRiseSetTimes(sunrise.toLocalTime(),
                                      JUST_BEFORE_MIDNIGHT)
      };
    }
    else
    {
      return new RiseSet[] {
        riseSet
      };
    }
  }

  private final RiseSetType riseSetType;
  private final Location location;
  private final LocalDate date;
  private final boolean inDaylightSavings;
  private final LocalTime sunrise;
  private final LocalTime sunset;

  RiseSet(final Location location,
          final LocalDate date,
          final boolean inDaylightSavings,
          final LocalTime sunrise,
          final LocalTime sunset)
  {
    this.location = location;
    this.date = date;
    this.inDaylightSavings = inDaylightSavings;

    if (sunrise.equals(JUST_AFTER_MIDNIGHT)
        && !sunset.equals(JUST_BEFORE_MIDNIGHT)
        || !sunrise.equals(JUST_AFTER_MIDNIGHT)
        && sunset.equals(JUST_BEFORE_MIDNIGHT))
    {
      riseSetType = RiseSetType.partial;
    }
    else
    {
      riseSetType = RiseSetType.normal;
    }
    this.sunrise = sunrise;
    this.sunset = sunset;
  }

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
          final RiseSetType riseSetType)
  {
    this.riseSetType = riseSetType;
    this.location = location;
    this.date = date;
    inDaylightSavings = false;
    switch (riseSetType)
    {
      case all_daylight:
        sunrise = JUST_AFTER_MIDNIGHT;
        sunset = JUST_BEFORE_MIDNIGHT;
        break;
      case all_nighttime:
        sunrise = JUST_AFTER_MIDNIGHT;
        sunset = JUST_BEFORE_MIDNIGHT;
        break;
      default:
        throw new IllegalArgumentException("Bad rise/ set type provided");
    }
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
      return location.getDescription() + ", " + date + " - " + riseSetType
             + ": sunrise " + sunrise + " sunset " + sunset;
    }
  }

  /**
   * Gets a copy of of this rise/ set, with times adjusted for daylight
   * saving time.
   * 
   * @param adjustedForDaylightSavings
   *        Whether to adjust the times.
   * @return Adjusted copy
   */
  public RiseSet withAdjustmentForDaylightSavings(final boolean adjustedForDaylightSavings)
  {
    if (adjustedForDaylightSavings || !isInDaylightSavings())
    {
      return this;
    }
    else
    {
      return new RiseSet(location, date, false, sunrise.minusHours(1), sunset
        .minusHours(1));
    }
  }

  /**
   * Gets a copy of this rise/ set, with different sunrise and sunset
   * times.
   * 
   * @param sunrise
   *        Sunrise
   * @param sunset
   *        Sunset
   * @return New rise/ set
   */
  public RiseSet withNewRiseSetTimes(final LocalTime sunrise,
                                     final LocalTime sunset)
  {
    return new RiseSet(location, date, inDaylightSavings, sunrise, sunset);
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
   * Gets the rise/ set type.
   * 
   * @return Rise/ set type
   */
  RiseSetType getRiseSetType()
  {
    return riseSetType;
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
