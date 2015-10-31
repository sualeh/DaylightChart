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
package daylightchart.sunchart.calculation;


import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.geoname.data.Location;

import us.fatehi.calculation.Equinox;
import us.fatehi.calculation.Equinox.DateTime;
import us.fatehi.calculation.ExtendedSunPositionAlgorithm;
import us.fatehi.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides;
import us.fatehi.calculation.SunPositionAlgorithmFactory;

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
   * @return Full years sunset and sunrise times for a location
   */
  public static SunChartYearData createSunChartYear(final Location location,
                                                    final int year)
  {
    final SunChartYearData sunChartYear = new SunChartYearData(location, year);

    for (final LocalDate date: getYearsDates(year))
    {
      final SunPositions sunPositions = new SunPositions(date);
      sunAlgorithm.setDate(date.getYear(),
                           date.getMonthValue(),
                           date.getDayOfMonth());
      for (int hour = 0; hour < 24; hour++)
      {
        final SolarEphemerides solarEphemerides = sunAlgorithm
          .calcSolarEphemerides(hour);
        final LocalDateTime dateTime = LocalDateTime.of(date.getYear(),
                                                        date.getMonthValue(),
                                                        date.getDayOfMonth(),
                                                        hour,
                                                        0,
                                                        0);
        final SunPosition sunPosition = new SunPosition(dateTime,
                                                        solarEphemerides);
        sunPositions.add(sunPosition);
      }
      sunChartYear.add(sunPositions);
    }
    return sunChartYear;
  }

  /**
   * Writes calculations to a writer.
   * 
   * @param location
   * @param writer
   */
  public static void writeCalculations(final Location location,
                                       final Writer writer)
  {
    if (writer == null || location == null)
    {
      return;
    }

    final DecimalFormat format = new DecimalFormat("+000.000;-000.000");
    format.setMaximumFractionDigits(3);

    final int year = Year.now().getValue();
    final SunChartYearData sunChartYear = createSunChartYear(location, year);

    final PrintWriter printWriter = new PrintWriter(writer, true);
    // Header
    printWriter.printf("Location\t%s%nDate\t%s%n%n", location, year);
    // Data rows
    final List<SunPositions> sunPositionsList = sunChartYear
      .getSunPositionsList();
    for (final SunPositions sunPositions: sunPositionsList)
    {
      printWriter
        .println("Date      \tTime\tAltitude\tAzimuth  \tHour Angle\tEqn of Time  \tDeclination");
      for (final SunPosition sunPosition: sunPositions)
      {
        printWriter.printf("%s\t%s\t%s\t%s\t%s\t%s\t%s%n",
                           sunPosition.getDateTime().toLocalDate(),
                           sunPosition.getDateTime().toLocalTime()
                             .format(DateTimeFormatter.ofPattern("HH:mm")),
                           format.format(sunPosition.getAltitude()),
                           format.format(sunPosition.getAzimuth()),
                           format.format(sunPosition.getHourAngle()),
                           format.format(sunPosition.getEquationOfTime()),
                           format.format(sunPosition.getDeclination()));
      }
      printWriter.println();
    }
  }

  /**
   * Generate a year's worth of dates
   * 
   * @return All the dates for the year
   */
  private static List<LocalDate> getYearsDates(final int year)
  {
    final List<LocalDate> dates = new ArrayList<LocalDate>();
    final Equinox equinox = new Equinox(year);
    for (int month = 1; month <= 12; month++)
    {
      LocalDate date;
      switch (month)
      {
        case 3:
          date = toLocalDate(equinox.getMarchEquinox());
          break;
        case 6:
          date = toLocalDate(equinox.getJuneSolstice());
          break;
        case 9:
          date = toLocalDate(equinox.getSeptemberEquinox());
          break;
        case 12:
          date = toLocalDate(equinox.getDecemberSolstice());
          break;
        default:
          date = LocalDate.of(year, month, 15);
          break;
      }
      dates.add(date);
    }
    return dates;
  }

  private static LocalDate toLocalDate(DateTime equinox3)
  {
    LocalDate date;
    date = LocalDate.of(equinox3.getYear(),
                        equinox3.getMonth(),
                        equinox3.getDay());
    return date;
  }

  private SunChartUtility()
  {
    // Prevent instantiation
  }

}
