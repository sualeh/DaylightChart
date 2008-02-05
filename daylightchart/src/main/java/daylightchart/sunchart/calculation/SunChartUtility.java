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
package daylightchart.sunchart.calculation;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.pointlocation6709.Angle;
import org.sunposition.calculation.ExtendedSunPositionAlgorithm;
import org.sunposition.calculation.SunPositionAlgorithmFactory;
import org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides;

import daylightchart.location.Location;
import daylightchart.location.parser.LocationParser;

/**
 * Calculator for sunrise and sunset times for a year.
 * 
 * @author Sualeh Fatehi
 */
public final class SunChartUtility
{

  private static final Logger LOGGER = Logger.getLogger(SunChartUtility.class
    .getName());

  private static final ExtendedSunPositionAlgorithm sunAlgorithm = SunPositionAlgorithmFactory
    .getExtendedSunPositionAlgorithmInstance();

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
  public static SunChartYearData createSunChartYear(final Location location,
                                                    final int year)
  {
    final SunChartYearData sunChartYear = new SunChartYearData(location, year);

    final List<SunPosition> sunPositions = new ArrayList<SunPosition>();
    for (final LocalDateTime dateTime: getYearsDates(year))
    {
      sunAlgorithm.setDate(dateTime.getYear(),
                           dateTime.getMonthOfYear(),
                           dateTime.getDayOfMonth());
      final double hour = dateTime.getHourOfDay() + dateTime.getMinuteOfHour()
                          / 60D;
      final SolarEphemerides solarEphemerides = sunAlgorithm
        .calcSolarEphemerides(hour);
      final SunPosition sunPosition = new SunPosition(dateTime,
                                                      solarEphemerides);
      sunPositions.add(sunPosition);
    }
    sunChartYear.setSunPositions(sunPositions);

    return sunChartYear;
  }

  /**
   * Shows the calculations.
   * 
   * @param args
   *        Location
   * @throws Exception
   *         On an exception
   */
  public static void main(final String[] args)
    throws Exception
  {
    final String locationString = args[0];
    final Location location = LocationParser.parseLocation(locationString);
    final File file = SunChartUtility.writeCalculationsToFile(location);
    System.out.println("Calculations written to " + file.getAbsolutePath());
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

  /**
   * Generate a year's worth of dates
   * 
   * @return All the dates for the year
   */
  private static List<LocalDateTime> getYearsDates(final int year)
  {
    final List<LocalDateTime> dates = new ArrayList<LocalDateTime>();
    final Equinox equinox = new Equinox(year);
    for (int month = 1; month <= 12; month++)
    {
      LocalDate date;
      switch (month)
      {
        case 3:
          date = equinox.getVernalEquinox().toLocalDate();
          break;
        case 6:
          date = equinox.getSummerSolstice().toLocalDate();
          break;
        case 9:
          date = equinox.getAutumnalEquinox().toLocalDate();
          break;
        case 12:
          date = equinox.getWinterSolstice().toLocalDate();
          break;
        default:
          date = new LocalDate(year, month, 15);
          break;
      }
      dates.add(new LocalDateTime(date.getYear(), date.getMonthOfYear(), date
        .getDayOfMonth(), 12, 0, 0));
    }
    return dates;
  }

  @SuppressWarnings("boxing")
  private static void writeCalculations(final Writer writer,
                                        final Location location)
  {
    if (writer == null || location == null)
    {
      return;
    }

    final DecimalFormat format = new DecimalFormat("00.000");
    format.setMaximumFractionDigits(3);

    final int year = Calendar.getInstance().get(Calendar.YEAR);
    final SunChartYearData sunChartYear = createSunChartYear(location, year);

    final PrintWriter printWriter = new PrintWriter(writer, true);
    // Header
    printWriter.printf("Location\t%s%nDate\t%s%n%n", location, year);
    // Data rows
    printWriter.println("Date\tAltitude\tAzimuth");
    final List<SunPosition> sunPositions = sunChartYear.getSunPositions();
    for (final SunPosition sunPosition: sunPositions)
    {
      printWriter.printf("%s\t%s\t%s%n", sunPosition.getDateTime()
        .toLocalDate(), Angle.fromDegrees(sunPosition.getAltitude()), Angle
        .fromDegrees(sunPosition.getAzimuth()));
    }
  }

  private SunChartUtility()
  {
    // Prevent instantiation
  }

}
