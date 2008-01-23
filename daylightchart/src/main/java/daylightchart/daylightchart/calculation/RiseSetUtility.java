/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;
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
  public static RiseSetYearData createRiseSetYear(final Location location,
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

    final TwilightType twilight = options.getTwilightType();
    final RiseSetYearData riseSetYear = new RiseSetYearData(location,
                                                            twilight,
                                                            year);
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
                                                  TwilightType.NONE);
      riseSetYear.addRiseSet(riseSet);

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
   * Writes chart calculations to a file.
   * 
   * @param location
   *        Location
   * @return File that was written
   */
  public static File writeCalculationsToFile(final Location location)
  {
    try
    {
      final File file = new File(location.getDescription() + ".txt");
      final FileWriter writer = new FileWriter(file);
      writeCalculations(writer, location);
      return file;
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Cannot write calculations for location "
                                + location, e);
      return null;
    }
  }

  static void createBands(final RiseSetYearData riseSetYear,
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

    for (int i = 0; i < riseSetData.size(); i++)
    {
      final RiseSet riseSet = riseSetData.get(i);
      RiseSet riseSetYesterday = null;
      if (i > 0)
      {
        riseSetYesterday = riseSetData.get(i - 1);
      }
      RiseSet riseSetTomorrow = null;
      if (i < riseSetData.size() - 1)
      {
        riseSetTomorrow = riseSetData.get(i + 1);
      }

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

        // Add a special "smoothing" value to the wrap band, if
        // necessary
        if (riseSetYesterday != null
            && riseSetYesterday.getRiseSetType() == RiseSetType.all_daylight)
        {
          wrapBand.add(riseSets[1].withNewRiseSetDate(riseSetYesterday
            .getDate()));
        }
      }
      else if (riseSets.length == 1)
      {
        // End the wrap band, if necessary
        if (wrapBand != null)
        {
          // Add a special "smoothing" value to the wrap band, if
          // necessary
          if (riseSetTomorrow != null
              && riseSetTomorrow.getRiseSetType() == RiseSetType.all_daylight
              && wrapBand.size() > 0)
          {
            wrapBand.add(wrapBand.getLastRiseSet()
              .withNewRiseSetDate(riseSetTomorrow.getDate()));
          }
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

    if (riseSet.getRiseSetType() != RiseSetType.partial
        && sunset.getHourOfDay() < 9)
    {
      return new RiseSet[] {
          riseSet.withNewRiseSetTimes(sunrise.toLocalTime(),
                                      RiseSet.JUST_BEFORE_MIDNIGHT),
          riseSet.withNewRiseSetTimes(RiseSet.JUST_AFTER_MIDNIGHT, sunset
            .toLocalTime())
      };
    }
    else if (riseSet.getRiseSetType() != RiseSetType.partial
             && sunrise.getHourOfDay() > 15)
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
                                             final TwilightType twilight)
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

  /**
   * Debug calculations.
   * 
   * @param writer
   *        Writer to write to
   * @param location
   *        Location to debug
   */
  private static void writeCalculations(final Writer writer,
                                        final Location location)
  {
    writeCalculations(writer,
                      location,
                      TwilightType.ASTRONOMICAL,
                      DaylightBandType.twilight);
  }

  /**
   * Debug calculations.
   * 
   * @param writer
   *        Writer to write to
   * @param location
   *        Location to debug
   * @param daylightBandType
   *        Types of band type to write to
   */
  @SuppressWarnings("boxing")
  private static void writeCalculations(final Writer writer,
                                        final Location location,
                                        final TwilightType twilight,
                                        final DaylightBandType... daylightBandType)
  {
    if (writer == null || location == null)
    {
      return;
    }

    final DecimalFormat format = new DecimalFormat("00.000");
    format.setMaximumFractionDigits(3);

    final int year = Calendar.getInstance().get(Calendar.YEAR);
    final Options options = new Options();
    options.setTwilightType(twilight);
    final RiseSetYearData riseSetYear = createRiseSetYear(location,
                                                          year,
                                                          options);

    final PrintWriter printWriter = new PrintWriter(writer, true);
    // Header
    printWriter.printf("Location\t%s%nDate\t%s%n%n", location, year);
    // Bands
    final List<DaylightBand> bands = riseSetYear.getBands();
    for (final Iterator<DaylightBand> iterator = bands.iterator(); iterator
      .hasNext();)
    {
      final DaylightBand band = iterator.next();
      if (!ArrayUtils.contains(daylightBandType, band.getDaylightBandType()))
      {
        iterator.remove();
      }
    }
    printWriter.printf("\t\t\t");
    if (ArrayUtils.contains(daylightBandType, DaylightBandType.twilight))
    {
      printWriter.printf("\t\t");
    }
    for (final DaylightBand band: bands)
    {
      printWriter.printf("Band\t%s\t", band.getName());
    }
    printWriter.println();
    // Data rows
    printWriter.print("Date\tSunrise\tSunset");
    if (ArrayUtils.contains(daylightBandType, DaylightBandType.twilight))
    {
      printWriter.println("\tTwilight Rise\tTwilight Set");
    }
    else
    {
      printWriter.println();
    }
    final List<RawRiseSet> rawRiseSets = riseSetYear.getRawRiseSets();
    final List<RawRiseSet> rawTwilights = riseSetYear.getRawTwilights();
    for (int i = 0; i < rawRiseSets.size(); i++)
    {
      final RawRiseSet rawRiseSet = rawRiseSets.get(i);
      printWriter
        .printf("%s\t%s\t%s", rawRiseSet.getDate(), format.format(rawRiseSet
          .getSunrise()), format.format(rawRiseSet.getSunset()));
      if (ArrayUtils.contains(daylightBandType, DaylightBandType.twilight))
      {
        final RawRiseSet rawTwilight = rawTwilights.get(i);
        printWriter.printf("\t%s\t%s",
                           format.format(rawTwilight.getSunrise()),
                           format.format(rawTwilight.getSunset()));
      }
      for (final DaylightBand band: bands)
      {
        final RiseSet riseSet = band.get(rawRiseSet.getDate());
        if (riseSet == null)
        {
          printWriter.print("\t\t");
        }
        else
        {
          printWriter.printf("\t%s\t%s", riseSet.getSunrise().toLocalTime()
            .toString("HH:mm:ss"), riseSet.getSunset().toLocalTime()
            .toString("HH:mm:ss"));
        }
      }
      printWriter.println();
    }
  }

  private RiseSetUtility()
  {

  }

}
