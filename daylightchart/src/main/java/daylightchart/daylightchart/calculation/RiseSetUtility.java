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
package daylightchart.daylightchart.calculation;


import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.sunposition.calculation.SunPositionAlgorithm;
import org.sunposition.calculation.SunPositionAlgorithmFactory;

import daylightchart.daylightchart.chart.TimeZoneOption;
import daylightchart.location.Location;
import daylightchart.location.parser.DefaultTimezones;
import daylightchart.options.Options;

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
   * Calculator for sunrise and sunset times for a year.
   * 
   * @param location
   *        Location
   * @param year
   *        Year
   * @param options
   *        Options
   * @return Full years sunset and sunrise times for a location
   */
  public static RiseSetYear createRiseSetYear(final Location location,
                                              final int year,
                                              final Options options)
  {
    final TimeZoneOption timeZoneOption = options.getTimeZoneOption();
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
    final boolean useDaylightTime = timeZone.useDaylightTime()
                                    && timeZoneOption != TimeZoneOption.USE_LOCAL_TIME;
    boolean wasDaylightSavings = false;

    final RiseSetYear riseSetYear = new RiseSetYear(location, year);
    riseSetYear.setUsesDaylightTime(useDaylightTime);
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

      final RawRiseSet riseSet = calculateRiseSet(location,
                                                  date,
                                                  useDaylightTime,
                                                  inDaylightSavings,
                                                  Twilight.none);
      riseSetYear.addRiseSet(riseSet);

      final Twilight twilight = options.getTwilight();
      if (twilight != null)
      {
        final RawRiseSet twilights = calculateRiseSet(location,
                                                      date,
                                                      useDaylightTime,
                                                      inDaylightSavings,
                                                      twilight);
        riseSetYear.addTwilight(twilights);
      }

    }

    // Create band for twilight, clock-shift taken into account
    createBands(riseSetYear, DaylightBandType.twilight);
    // Create band, clock-shift taken into account
    createBands(riseSetYear, DaylightBandType.with_clock_shift);
    // Create band, without clock shift
    createBands(riseSetYear, DaylightBandType.without_clock_shift);

    return riseSetYear;

  }

  /**
   * Debug calculations.
   * 
   * @param location
   *        Location to debug
   */
  public static void writeCalculations(final Writer writer,
                                       final Location location)
  {
    if (writer == null || location == null)
    {
      return;
    }

    final int year = Calendar.getInstance().get(Calendar.YEAR);
    final RiseSetYear riseSetYear = RiseSetUtility
      .createRiseSetYear(location, year, new Options());

    final PrintWriter printWriter = new PrintWriter(writer, true);
    // Header
    printWriter.printf("Location\t%s%nDate\t%s%n%n", location, year);
    // Bands
    printWriter.printf("\t\t\t\t\t");
    final List<DaylightBand> bands = riseSetYear.getBands();
    for (final DaylightBand band: bands)
    {
      printWriter.printf("Band\t%s\t", band.getName());
    }
    printWriter.println();
    // Data rows
    printWriter.println("Date\tSunrise\tSunset\tTwilight Rise\tTwilight Set");
    final List<RawRiseSet> rawRiseSets = riseSetYear.getRawRiseSets();
    final List<RawRiseSet> rawTwilights = riseSetYear.getRawTwilights();
    for (int i = 0; i < rawRiseSets.size(); i++)
    {
      final RawRiseSet rawRiseSet = rawRiseSets.get(i);
      final RawRiseSet rawTwilight = rawTwilights.get(i);
      printWriter.printf("%s\t%6.3f\t%6.3f\t%6.3f\t%6.3f",
                         rawRiseSet.getDate(),
                         rawRiseSet.getSunrise(),
                         rawRiseSet.getSunset(),
                         rawTwilight.getSunrise(),
                         rawTwilight.getSunset());
      for (final DaylightBand band: bands)
      {
        final RiseSet riseSet = band.get(rawRiseSet.getDate());
        if (riseSet == null)
        {
          printWriter.print("\t\t");
        }
        else
        {
          printWriter.printf("\t%s\t%s",
                             riseSet.getSunrise().toLocalTime(),
                             riseSet.getSunset().toLocalTime());
        }
      }
      printWriter.println();
    }
  }

  static void createBands(final RiseSetYear riseSetYear,
                          final DaylightBandType daylightSavingsMode)
  {
    final List<DaylightBand> bands;
    if (daylightSavingsMode == DaylightBandType.twilight)
    {
      bands = RiseSetUtility.createDaylightBands(riseSetYear.getTwilights(),
                                                 daylightSavingsMode);
    }
    else
    {
      bands = RiseSetUtility
        .createDaylightBands(riseSetYear.getRiseSets(daylightSavingsMode
          .isAdjustedForDaylightSavings()), daylightSavingsMode);
    }

    riseSetYear.addDaylightBands(bands);
  }

  /**
   * Creates daylight bands for plotting.
   * 
   * @param riseSetData
   *        Rise/ set data for the year
   * @param daylightSavingsMode
   *        The daylight savings mode
   * @return List of daylight bands
   */
  static List<DaylightBand> createDaylightBands(final List<RiseSet> riseSetData,
                                                final DaylightBandType daylightSavingsMode)
  {
    final List<DaylightBand> bands = new ArrayList<DaylightBand>();

    DaylightBand baseBand = null;
    DaylightBand wrapBand = null;

    for (final RiseSet riseSet: riseSetData)
    {
      final RiseSet[] riseSets = splitAtMidnight(riseSet);
      if (riseSets.length == 2)
      {
        // Create a new wrap band if necessary
        if (wrapBand == null)
        {
          wrapBand = new DaylightBand(daylightSavingsMode, bands.size());
          bands.add(wrapBand);
        }

        if (baseBand == null)
        {
          baseBand = new DaylightBand(daylightSavingsMode, bands.size());
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
          baseBand = new DaylightBand(daylightSavingsMode, bands.size());
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
                                      RiseSet.JUST_BEFORE_MIDNIGHT),
          riseSet.withNewRiseSetTimes(RiseSet.JUST_AFTER_MIDNIGHT, sunset
            .toLocalTime())
      };
    }
    else if (sunrise.getHourOfDay() > 15)
    {
      return new RiseSet[] {
          riseSet.withNewRiseSetTimes(RiseSet.JUST_AFTER_MIDNIGHT, sunset
            .toLocalTime()),
          riseSet.withNewRiseSetTimes(sunrise.toLocalTime(),
                                      RiseSet.JUST_BEFORE_MIDNIGHT)
      };
    }
    else
    {
      return new RiseSet[] {
        riseSet
      };
    }
  }

  @SuppressWarnings("boxing")
  private static RawRiseSet calculateRiseSet(final Location location,
                                             final LocalDate date,
                                             final boolean useDaylightTime,
                                             final boolean inDaylightSavings,
                                             final Twilight twilight)
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
    final double[] sunriseSunset = sunAlgorithm.calcRiseSet(twilight
      .getHorizon());

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

    final RawRiseSet riseSet = new RawRiseSet(location,
                                              date,
                                              (useDaylightTime && inDaylightSavings),
                                              sunriseSunset[0],
                                              sunriseSunset[1]);
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

  private RiseSetUtility()
  {

  }
}
