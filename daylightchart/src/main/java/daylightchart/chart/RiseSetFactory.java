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


import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.pointlocation6709.Utility;

import daylightchart.astronomical.SunAlgorithm;
import daylightchart.astronomical.SunAlgorithmFactory;
import daylightchart.location.Location;
import daylightchart.location.parser.DefaultTimezones;

/**
 * Calculator for sunrise and sunset times for a year.
 * 
 * @author Sualeh Fatehi
 */
public final class RiseSetFactory
{

  /**
   * Calculator for sunrise and sunset times for a year.
   * 
   * @param location
   *        Location
   * @param year
   *        Year
   * @param timeZoneOption
   *        Time zone option
   * @return Full years sunset and sunrise times for a location
   */
  public static RiseSetYear createRiseSetYear(final Location location,
                                              final int year,
                                              final TimeZoneOption timeZoneOption)
  {
    final SunAlgorithm sunAlgorithm = SunAlgorithmFactory.getInstance();

    final RiseSetYear riseSetYear = new RiseSetYear(location, year);
    final List<LocalDate> yearsDates = getYearsDates(year);
    final TimeZone timeZone;
    if (location != null)
    {
      final String timeZoneId;
      if (timeZoneOption != null
          && timeZoneOption == TimeZoneOption.USE_TIME_ZONE)
      {
        timeZoneId = location.getTimeZoneId();
      }
      else
      {
        timeZoneId = DefaultTimezones.createGMTTimeZoneId(location
          .getPointLocation().getLongitude());
      }
      timeZone = TimeZone.getTimeZone(timeZoneId);
    }
    else
    {
      timeZone = TimeZone.getDefault();
    }
    final boolean useDaylightTime = timeZone.useDaylightTime();
    boolean wasDaylightSavings = false;
    for (final LocalDate date: yearsDates)
    {
      final boolean inDaylightSavings = timeZone.inDaylightTime(date
        .toDateTime((LocalTime) null).toGregorianCalendar().getTime());
      if (wasDaylightSavings != inDaylightSavings)
      {
        if (!wasDaylightSavings)
        {
          riseSetYear.setDstStart(date);
        }
        else
        {
          riseSetYear.setDstEnd(date);
        }
      }
      wasDaylightSavings = inDaylightSavings;
      // Calculate sunsrise and sunset
      final double[] sunriseSunset = calcRiseSet(sunAlgorithm, location, date);
      LocalTime sunrise = toLocalTime(sunriseSunset[0]);
      LocalTime sunset = toLocalTime(sunriseSunset[1]);
      if (useDaylightTime && inDaylightSavings)
      {
        sunrise = sunrise.plusHours(1);
        sunset = sunset.plusHours(1);
      }

      RiseSet riseSet;
      if (sunriseSunset[0] == Double.POSITIVE_INFINITY
          && sunriseSunset[1] == Double.POSITIVE_INFINITY)
      {
        riseSet = new RiseSet(location,
                              date,
                              false,
                              new LocalTime(0, 0, 0, 1),
                              new LocalTime(23, 59, 59, 999));
      }
      else if (sunriseSunset[0] == Double.NEGATIVE_INFINITY
               && sunriseSunset[1] == Double.NEGATIVE_INFINITY)
      {
        riseSet = new RiseSet(location,
                              date,
                              false,
                              new LocalTime(12, 0, 0, 0),
                              new LocalTime(12, 0, 0, 0));
      }
      else
      {
        riseSet = new RiseSet(location,
                              date,
                              (useDaylightTime && inDaylightSavings),
                              sunrise,
                              sunset);
      }
      riseSetYear.addRiseSet(riseSet);
    }
    return riseSetYear;
  }

  private static double[] calcRiseSet(final SunAlgorithm sunAlgorithm,
                                      final Location location,
                                      final LocalDate date)
  {
    if (location != null)
    {
      sunAlgorithm.setLatitude(location.getPointLocation().getLatitude()
        .getDegrees());
      sunAlgorithm.setLongitude(location.getPointLocation().getLongitude()
        .getDegrees());
      sunAlgorithm.setTimeZoneOffset(DefaultTimezones
        .getStandardTimeZoneOffsetHours(location.getTimeZoneId()));
    }
    sunAlgorithm.setYear(date.getYear());
    sunAlgorithm.setMonth(date.getMonthOfYear());
    sunAlgorithm.setDay(date.getDayOfMonth());

    final double[] riseSet = sunAlgorithm
      .calcRiseSet(SunAlgorithm.SUNRISE_SUNSET);
    System.out.println(location.getDescription() + ": " + date + " - sunrise "
                       + riseSet[0] + " sunset " + riseSet[1]);

    return riseSet;
  }

  /**
   * Generate a year's worth of dates
   * 
   * @return All the dates for the year
   */
  private static List<LocalDate> getYearsDates(final int year)
  {
    final List<LocalDate> dates = new ArrayList<LocalDate>();
    LocalDate date = new LocalDate(year, 1, 1);
    do
    {
      dates.add(date);
      date = date.plusDays(1);
    } while (!(date.getMonthOfYear() == 1 && date.getDayOfMonth() == 1));
    return dates;
  }

  private static LocalTime toLocalTime(final double hour)
  {
    double dayHour = hour % 24D;
    if (dayHour < 0)
    {
      dayHour = dayHour + hour;
    }
    final int[] fields = Utility.sexagesimalSplit(dayHour);
    return new LocalTime(fields[0], fields[1], fields[2]);
  }

  private RiseSetFactory()
  {

  }

}
