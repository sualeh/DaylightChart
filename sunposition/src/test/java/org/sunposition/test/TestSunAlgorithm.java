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
package org.sunposition.test;


import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.junit.Test;
import org.pointlocation6709.Utility;
import org.pointlocation6709.parser.ParserException;
import org.sunposition.calculation.SunPositionAlgorithm;
import org.sunposition.calculation.SunPositionAlgorithmFactory;

/**
 * SimpleLocation tests.
 */
public class TestSunAlgorithm
{

  private final SunPositionAlgorithm sunPositionAlgorithm = SunPositionAlgorithmFactory
    .getInstance();

  /**
   * Aberdeen
   * <ol>
   * <li>High latitude</li>
   * <li>N/W quadrant of the globe</li>
   * <li>Time zone 0</li>
   * </ol>
   * 
   * @throws ParserException
   */
  @Test
  public void aberdeen()
    throws ParserException
  {
    final SimpleLocation location = new SimpleLocation("Aberdeen;GB;Europe/London;+5710-00204/");
    final LocalDate date = new LocalDate(2001, 12, 2);

    final double riseset[] = calcRiseSet(date, location);

    assertTimeEquals(location.getLocation(),
                     SunPositionAlgorithm.RISE,
                     8,
                     24,
                     riseset);
    assertTimeEquals(location.getLocation(),
                     SunPositionAlgorithm.SET,
                     15,
                     31,
                     riseset);
  }

  /**
   * Bakersfield
   * <ol>
   * <li>Time zone -8</li>
   * </ol>
   * 
   * @throws ParserException
   */
  @Test
  public void bakersfield()
    throws ParserException
  {
    final SimpleLocation location = new SimpleLocation("Bakersfield, CA;US;America/Los_Angeles;+3523-11901/");
    final LocalDate date = new LocalDate(2003, 6, 24);

    final double riseset[] = calcRiseSet(date, location);

    assertTimeEquals(location.getLocation(),
                     SunPositionAlgorithm.RISE,
                     4,
                     42,
                     riseset);
    assertTimeEquals(location.getLocation(),
                     SunPositionAlgorithm.SET,
                     19,
                     15,
                     riseset);
  }

  /**
   * Geneva
   * <ol>
   * <li>N/E quadrant of the globe</li>
   * </ol>
   * 
   * @throws ParserException
   */
  @Test
  public void geneva()
    throws ParserException
  {
    final SimpleLocation location = new SimpleLocation("Geneva;Switzerland;Europe/Zurich;+4612+00609/");
    final LocalDate date = new LocalDate(2001, 11, 28);

    final double riseset[] = calcRiseSet(date, location);

    assertTimeEquals(location.getLocation(),
                     SunPositionAlgorithm.RISE,
                     7,
                     54,
                     riseset);
    assertTimeEquals(location.getLocation(),
                     SunPositionAlgorithm.SET,
                     16,
                     53,
                     riseset);
  }

  /**
   * Luleå
   * <ol>
   * <li>N/E quadrant of the globe</li>
   * </ol>
   * 
   * @throws ParserException
   */
  @Test
  public void luleå()
    throws ParserException
  {
    final SimpleLocation location = new SimpleLocation("Luleå;SE;Europe/Stockholm;+6536+02210/");
    final LocalDate date = new LocalDate(2007, 6, 23);

    final double riseset[] = calcRiseSet(date, location);

    assertTimeEquals(location.getLocation(),
                     SunPositionAlgorithm.RISE,
                     23,
                     58,
                     riseset);
    assertTimeEquals(location.getLocation(),
                     SunPositionAlgorithm.SET,
                     23,
                     7,
                     riseset);
  }

  /**
   * Nairobi
   * <ol>
   * <li>Latitude close to equator</li>
   * </ol>
   * 
   * @throws ParserException
   */
  @Test
  public void nairobi()
    throws ParserException
  {
    final SimpleLocation location = new SimpleLocation("Nairobi;Kenya;Africa/Nairobi;-0117+03649/");
    final LocalDate date = new LocalDate(2003, 6, 24);

    final double riseset[] = calcRiseSet(date, location);

    assertTimeEquals(location.getLocation(),
                     SunPositionAlgorithm.RISE,
                     6,
                     33,
                     riseset);
    assertTimeEquals(location.getLocation(),
                     SunPositionAlgorithm.SET,
                     18,
                     36,
                     riseset);
  }

  /**
   * Sydney
   * <ol>
   * <li>S/W quadrant of the globe</li>
   * <li>Time zone +10</li>
   * </ol>
   * 
   * @throws ParserException
   */
  @Test
  public void sydney()
    throws ParserException
  {
    final SimpleLocation location = new SimpleLocation("Sydney;Australia;Australia/Sydney;-3352+15113/");
    final LocalDate date = new LocalDate(2001, 12, 2);

    final double riseset[] = calcRiseSet(date, location);

    assertTimeEquals(location.getLocation(),
                     SunPositionAlgorithm.RISE,
                     4,
                     37,
                     riseset);
    assertTimeEquals(location.getLocation(),
                     SunPositionAlgorithm.SET,
                     18,
                     52,
                     riseset);
  }

  private void assertTimeEquals(final String location,
                                final int type,
                                final int expectedHour,
                                final int expectedMinute,
                                final double riseset[])
  {
    final int MINUTES_DELTA = 1;

    final double actualHours = riseset[type];
    final String typeString = type == SunPositionAlgorithm.SET? "sunset"
                                                              : "sunrise";

    final LocalTime expectedTime = new LocalTime(expectedHour, expectedMinute);
    final LocalTime actualTime = toLocalTime(actualHours);
    final Minutes minutes = Minutes.minutesBetween(expectedTime, actualTime);

    String message = location + " " + typeString + ": expected " + expectedTime
                     + "; actual " + actualTime;
    System.out.println(message);
    assertTrue(message, Math.abs(minutes.getMinutes()) <= MINUTES_DELTA);
  }

  private double[] calcRiseSet(final LocalDate date,
                               final SimpleLocation location)
  {
    sunPositionAlgorithm.setLocation(location.getLocation(), location
      .getLatitude(), location.getLongitude());
    sunPositionAlgorithm.setTimeZoneOffset(location.getTimeZoneOffset());
    sunPositionAlgorithm.setDate(date.getYear(), date.getMonthOfYear(), date
      .getDayOfMonth());

    System.out.println(sunPositionAlgorithm);

    return sunPositionAlgorithm
      .calcRiseSet(SunPositionAlgorithm.SUNRISE_SUNSET);
  }

  private LocalTime toLocalTime(final double hour)
  {
    double dayHour = hour % 24D;
    if (dayHour < 0)
    {
      dayHour = dayHour + 24D;
    }
    final int[] fields = Utility.sexagesimalSplit(dayHour);
    return new LocalTime(fields[0], fields[1], fields[2]);
  }

}
