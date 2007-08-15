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


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.pointlocation6709.Utility;

import daylightchart.location.Location;

/**
 * Sunrise and sunset at a given location, and a given date. RiseSet is
 * calculated as part of the rise/ set year, and then may be split for
 * creating the daylight bands.
 * 
 * @author Sualeh Fatehi
 */
final class RiseSet
  implements Serializable
{

  enum RiseSetType
  {
    /** Normal day. */
    normal,
    /** Partial day - the sun never rises or never sets. */
    partial,
    /** All daylight, the sun never sets. */
    all_daylight,
    /** All night time, the sun never rises. */
    all_nighttime;
  }

  private static final LocalTime JUST_BEFORE_MIDNIGHT = LocalTime.MIDNIGHT
    .minusMillis(1);

  private static final LocalTime JUST_AFTER_MIDNIGHT = LocalTime.MIDNIGHT
    .plusMillis(1);

  private static final long serialVersionUID = 3092668888760029582L;

  /**
   * Splits the given rise/ set at midnight.
   * 
   * @param riseSet
   *        Rise/ set to split
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
          final double sunrise,
          final double sunset)
  {
    this.location = location;
    this.date = date;
    this.inDaylightSavings = inDaylightSavings;

    boolean hasSunrise = !Double.isInfinite(sunrise);
    boolean hasSunset = !Double.isInfinite(sunset);
    if (sunrise == Double.POSITIVE_INFINITY
        && sunset == Double.POSITIVE_INFINITY)
    {
      riseSetType = RiseSetType.all_daylight;
    }
    else if (sunrise == Double.NEGATIVE_INFINITY
             && sunset == Double.NEGATIVE_INFINITY)
    {
      riseSetType = RiseSetType.all_nighttime;
    }
    else if (hasSunrise && !hasSunset || !hasSunrise && hasSunset)
    {
      riseSetType = RiseSetType.partial;
    }
    else
    {
      riseSetType = RiseSetType.normal;
    }

    switch (riseSetType)
    {
      case all_daylight:
      case all_nighttime:
        this.sunrise = JUST_AFTER_MIDNIGHT;
        this.sunset = JUST_BEFORE_MIDNIGHT;
        break;
      case partial:
        if (hasSunrise)
        {
          this.sunrise = toLocalTime(sunrise);
        }
        else
        {
          this.sunrise = JUST_AFTER_MIDNIGHT;
        }
        if (hasSunrise)
        {
          this.sunset = toLocalTime(sunset);
        }
        else
        {
          this.sunset = JUST_BEFORE_MIDNIGHT;
        }
        break;
      case normal:
      default:
        this.sunrise = toLocalTime(sunrise);
        this.sunset = toLocalTime(sunset);
        break;
    }

  }

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
    else if (sunrise.equals(JUST_AFTER_MIDNIGHT)
             && sunset.equals(JUST_BEFORE_MIDNIGHT))
    {
      throw new IllegalArgumentException("Bad rise/ set type provided");
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
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      new PrintStream(outputStream, true)
        .printf("%s, %s: (%s) sunrise %s sunset %s",
                location.getDescription(),
                date,
                riseSetType,
                sunrise.toString("HH:mm:ss"),
                sunset.toString("HH:mm:ss"));
      return outputStream.toString();
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

  /**
   * Gets a copy of of this rise/ set, with times adjusted for daylight
   * saving time.
   * 
   * @param adjustedForDaylightSavings
   *        Whether to adjust the times.
   * @return Adjusted copy
   */
  RiseSet withAdjustmentForDaylightSavings(final boolean adjustedForDaylightSavings)
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
  RiseSet withNewRiseSetTimes(final LocalTime sunrise, final LocalTime sunset)
  {
    return new RiseSet(location, date, inDaylightSavings, sunrise, sunset);
  }

  private LocalTime toLocalTime(final double hour)
  {
    double dayHour = hour % 24D;
    if (dayHour < 0)
    {
      dayHour = dayHour + 24;
    }
    final int[] fields = Utility.sexagesimalSplit(dayHour);
    return new LocalTime(fields[0], fields[1], fields[2]);
  }

}
