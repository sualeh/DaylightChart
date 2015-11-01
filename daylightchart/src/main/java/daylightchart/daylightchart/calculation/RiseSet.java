/*
 *
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2015, Sualeh Fatehi.
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
package daylightchart.daylightchart.calculation;


import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.geoname.data.Location;
import org.geoname.parser.Utility;

/**
 * Sunrise and sunset at a given location, and a given date. RiseSet is
 * calculated as part of the rise/ set year, and then may be split for
 * creating the daylight bands.
 *
 * @author Sualeh Fatehi
 */
public final class RiseSet
  implements Serializable, Comparable<RiseSet>
{

  static final LocalTime JUST_BEFORE_MIDNIGHT = LocalTime.MIDNIGHT
    .minusNanos(1);

  static final LocalTime JUST_AFTER_MIDNIGHT = LocalTime.MIDNIGHT.plusNanos(1);

  private static final long serialVersionUID = 3092668888760029582L;

  private final RiseSetType riseSetType;
  private final Location location;
  private final LocalDate date;
  private final boolean inDaylightSavings;
  private final LocalTime sunrise;
  private final LocalTime sunset;

  RiseSet(final RawRiseSet rawRiseSet)
  {
    location = rawRiseSet.getLocation();
    date = rawRiseSet.getDate();
    inDaylightSavings = rawRiseSet.isInDaylightSavings();

    final double sunriseRaw = rawRiseSet.getSunrise();
    final double sunsetRaw = rawRiseSet.getSunset();

    final boolean hasSunrise = !Double.isInfinite(sunriseRaw);
    final boolean hasSunset = !Double.isInfinite(sunsetRaw);
    if (sunriseRaw == Double.POSITIVE_INFINITY
        && sunsetRaw == Double.POSITIVE_INFINITY)
    {
      riseSetType = RiseSetType.all_daylight;
    }
    else if (sunriseRaw == Double.NEGATIVE_INFINITY
             && sunsetRaw == Double.NEGATIVE_INFINITY)
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
        sunrise = JUST_AFTER_MIDNIGHT;
        sunset = JUST_BEFORE_MIDNIGHT;
        break;
      case partial:
        if (hasSunrise)
        {
          sunrise = toLocalTime(sunriseRaw);
        }
        else
        {
          sunrise = JUST_AFTER_MIDNIGHT;
        }
        if (hasSunrise)
        {
          sunset = toLocalTime(sunsetRaw);
        }
        else
        {
          sunset = JUST_BEFORE_MIDNIGHT;
        }
        break;
      case normal:
      default:
        sunrise = toLocalTime(sunriseRaw);
        sunset = toLocalTime(sunsetRaw);
        break;
    }

  }

  private RiseSet(final Location location,
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
      riseSetType = RiseSetType.split;
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
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final RiseSet o)
  {
    return date.compareTo(o.getDate());
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
    return date.atTime(sunrise);
  }

  /**
   * Sunset time.
   *
   * @return Sunset time
   */
  public LocalDateTime getSunset()
  {
    return date.atTime(sunset);
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
        .printf("%s, %s: (%s) sunrise %s sunset %s",
                location.getDescription(),
                date,
                riseSetType,
                sunrise.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                sunset.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
      return writer.toString();
    }
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
      return new RiseSet(location,
                         date,
                         false,
                         sunrise.minusHours(1),
                         sunset.minusHours(1));
    }
  }

  /**
   * Gets a copy of this rise/ set, with different date times.
   *
   * @param sunrise
   *        Sunrise
   * @param sunset
   *        Sunset
   * @return New rise/ set
   */
  RiseSet withNewRiseSetDate(final LocalDate date)
  {
    return new RiseSet(location, date, inDaylightSavings, sunrise, sunset);
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
    return LocalTime.of(fields[0], fields[1], fields[2]);
  }

}
