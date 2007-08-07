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
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.sunposition.calculation.SunPositionAlgorithm;
import org.sunposition.calculation.SunPositionAlgorithmFactory;

import daylightchart.chart.RiseSet.RiseSetType;
import daylightchart.location.Location;
import daylightchart.location.parser.DefaultTimezones;

/**
 * Calculator for sunrise and sunset times for a year.
 * 
 * @author Sualeh Fatehi
 */
public final class RiseSetUtility
{

  private static final Logger LOGGER = Logger.getLogger(RiseSetUtility.class
    .getName());

  private static final SunPositionAlgorithm sunAlgorithm = SunPositionAlgorithmFactory
    .getInstance();

  /**
   * Creates daylight bands for plotting.
   * @param riseSetData TODO
   * @param daylightSavingsMode
   *        The daylight savings mode
   * 
   * @return List of daylight bands
   */
  public static List<DaylightBand> createDaylightBands(List<RiseSet> riseSetData,
                                                       final DaylightSavingsMode daylightSavingsMode)
  {
    final List<DaylightBand> bands = new ArrayList<DaylightBand>();

    DaylightBand baseBand = null;
    DaylightBand wrapBand = null;

    for (final RiseSet riseSet: riseSetData)
    {
      final RiseSet[] riseSets = RiseSet.splitAtMidnight(riseSet);
      if (riseSets.length == 2)
      {
        // Create a new wrap band if necessary
        if (wrapBand == null)
        {
          wrapBand = new DaylightBand(daylightSavingsMode + ", #"
                                      + bands.size());
          bands.add(wrapBand);
        }

        if (baseBand == null)
        {
          baseBand = new DaylightBand(daylightSavingsMode + ", #"
                                      + bands.size());
          bands.add(baseBand);
        }

        // Split the daylight hours across two series
        baseBand.add(riseSets[0]);
        wrapBand.add(riseSets[1]);
      }
      else if (riseSets.length == 1)
      {
        // End the wrap band, if necessary
        if (wrapBand != null)
        {
          wrapBand = null;
        }

        // Create a new band if we are entering a period of
        // all-night-time from a period where there was daylight
        if (baseBand == null
            && riseSet.getRiseSetType() != RiseSetType.all_nighttime)
        {
          baseBand = new DaylightBand(daylightSavingsMode + ", #"
                                      + bands.size());
          bands.add(baseBand);
        }
        else
        // Close the band if we are entering a period of all-night-time
        if (baseBand != null
            && riseSet.getRiseSetType() == RiseSetType.all_nighttime)
        {
          baseBand = null;
        }

        // Add sunset and sunrise as usual
        if (baseBand != null)
        {
          baseBand.add(riseSet);
        }
      }
    }

    return bands;

  }

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

    final RiseSetYear riseSetYear = new RiseSetYear(location, year);
    for (final LocalDate date: getYearsDates(year))
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

      RiseSet riseSet = calculateRiseSet(location,
                                         date,
                                         useDaylightTime,
                                         inDaylightSavings,
                                         SunPositionAlgorithm.SUNRISE_SUNSET);
      riseSetYear.addRiseSet(riseSet);

      RiseSet twilights = calculateRiseSet(location,
                                           date,
                                           useDaylightTime,
                                           inDaylightSavings,
                                           SunPositionAlgorithm.CIVIL_TWILIGHT);
      riseSetYear.addTwilight(twilights);

    }

    return riseSetYear;

  }

  private static RiseSet calculateRiseSet(final Location location,
                                          final LocalDate date,
                                          final boolean useDaylightTime,
                                          final boolean inDaylightSavings,
                                          double horizon)
  {
    if (location != null)
    {
      sunAlgorithm.setLocation(location.getDescription(), location
        .getPointLocation().getLatitude().getDegrees(), location
        .getPointLocation().getLongitude().getDegrees());
      sunAlgorithm.setTimeZoneOffset(DefaultTimezones
        .getStandardTimeZoneOffsetHours(location.getTimeZoneId()));
    }
    sunAlgorithm.setDate(date.getYear(), date.getMonthOfYear(), date
      .getDayOfMonth());
    final double[] sunriseSunset = sunAlgorithm.calcRiseSet(horizon);

    {
      ByteArrayOutputStream outputStream = null;
      try
      {
        outputStream = new ByteArrayOutputStream();
        new PrintStream(outputStream, true)
          .printf("%s, %s: sunrise %2.3f sunset %2.3f%n",
                  location,
                  date,
                  sunriseSunset[0],
                  sunriseSunset[1]);
        outputStream.close();
        LOGGER.log(Level.FINE, outputStream.toString());
      }
      catch (IOException e)
      {
        // Ignore
      }
    }

    if (useDaylightTime && inDaylightSavings)
    {
      if (!Double.isInfinite(sunriseSunset[0]))
      {
        sunriseSunset[0] = sunriseSunset[0] + 1;
      }
      if (!Double.isInfinite(sunriseSunset[1]))
      {
        sunriseSunset[1] = sunriseSunset[1] + 1;
      }
    }

    return new RiseSet(location,
                       date,
                       (useDaylightTime && inDaylightSavings),
                       sunriseSunset[0],
                       sunriseSunset[1]);

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

  private RiseSetUtility()
  {

  }

}
