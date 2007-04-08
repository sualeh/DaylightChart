/*
 * Copyright (c) 2004-2007 Sualeh Fatehi. All Rights Reserved.
 */
package daylightchart.chart;


import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import daylightchart.astronomical.SunAlgorithm;
import daylightchart.astronomical.SunAlgorithmFactory;
import daylightchart.location.Location;

/**
 * Calculator for sunrise and sunset times for a year.
 * 
 * @author Sualeh Fatehi
 */
public class RiseSetFactory
{

  /**
   * Calculator for sunrise and sunset times for a year.
   * 
   * @param location
   *        Location
   * @param year
   *        Year
   * @return Full years sunset and sunrise times for a location
   */
  public static RiseSetYear createRiseSetYear(final Location location,
                                              final int year)
  {
    final SunAlgorithm sunAlgorithm = SunAlgorithmFactory.getInstance();

    final RiseSetYear riseSetYear = new RiseSetYear(location, year);
    final List<LocalDate> yearsDates = getYearsDates(year);
    final TimeZone tz = location.getTimeZone();
    final boolean useDaylightTime = tz.useDaylightTime();
    boolean wasDaylightSavings = false;
    for (final LocalDate date: yearsDates)
    {
      final boolean inDaylightSavings = tz.inDaylightTime(date
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
      final Hour[] riseSet = calcRiseSet(sunAlgorithm, location, date);
      final Hour sunrise = riseSet[0];
      sunrise.setInDaylightSavings(useDaylightTime && inDaylightSavings);
      final Hour sunset = riseSet[1];
      sunset.setInDaylightSavings(useDaylightTime && inDaylightSavings);
      //
      riseSetYear.addRiseSet(new RiseSet(location, date, sunrise, sunset));
    }
    return riseSetYear;
  }

  private static Hour[] calcRiseSet(final SunAlgorithm sunAlgorithm,
                                    final Location location,
                                    final LocalDate date)
  {
    sunAlgorithm.setLatitude(location.getCoordinates().getLatitude()
      .getDegrees());
    sunAlgorithm.setLongitude(location.getCoordinates().getLongitude()
      .getDegrees());
    sunAlgorithm
      .setTimeZoneOffset(location.getTimeZone().getRawOffset() / 1000.0 / 60.0 / 60.0);
    sunAlgorithm.setYear(date.getYear());
    sunAlgorithm.setMonth(date.getMonthOfYear());
    sunAlgorithm.setDay(date.getDayOfMonth());

    final double[] riseSet = sunAlgorithm
      .calcRiseSet(SunAlgorithm.SUNRISE_SUNSET);

    return new Hour[] {
        new Hour(riseSet[0]), new Hour(riseSet[1])
    };
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

  private RiseSetFactory()
  {

  }

}
