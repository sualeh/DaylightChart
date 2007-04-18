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
package daylightchart.test;


import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;
import org.junit.Test;

import daylightchart.astronomical.SunAlgorithm;
import daylightchart.astronomical.SunAlgorithmFactory;
import daylightchart.location.Location;
import daylightchart.location.parser.LocationParser;
import daylightchart.location.parser.ParserException;

/**
 * Location tests.
 */
public class TestSunAlgorithm
{

  private static final double ONE_MINUTE = 1 / 60D;
  private static final double DELTA = 0.5 * ONE_MINUTE;

  private final SunAlgorithm mSunAlgorithm = SunAlgorithmFactory.getInstance();

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

    final String strLoc = "Aberdeen;GB;Europe/London;+5710-00204/";
    final Location location = LocationParser.parseLocation(strLoc);
    final double riseset[] = calcRiseSet(new LocalDate(2001, 12, 2), location);

    assertEquals(8 + 24 * ONE_MINUTE, riseset[SunAlgorithm.RISE], DELTA);
    assertEquals(12 + 3 + 31 * ONE_MINUTE, riseset[SunAlgorithm.SET], DELTA);

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

    final String strLoc = "Bakersfield, CA;US;America/Los_Angeles;+3523-11901/";
    final Location location = LocationParser.parseLocation(strLoc);
    final double riseset[] = calcRiseSet(new LocalDate(2003, 6, 24), location);

    assertEquals(4 + 42 * ONE_MINUTE, riseset[SunAlgorithm.RISE], DELTA);
    assertEquals(12 + 7 + 15 * ONE_MINUTE, riseset[SunAlgorithm.SET], DELTA);

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

    final String strLoc = "Geneva;Switzerland;Europe/Zurich;+4612+00609/";
    final Location location = LocationParser.parseLocation(strLoc);
    final double riseset[] = calcRiseSet(new LocalDate(2001, 11, 28), location);

    assertEquals(7 + 54 * ONE_MINUTE, riseset[SunAlgorithm.RISE], DELTA);
    assertEquals(12 + 4 + 53 * ONE_MINUTE, riseset[SunAlgorithm.SET], DELTA);

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

    final String strLoc = "Nairobi;Kenya;Africa/Nairobi;-0117+03649/";
    final Location location = LocationParser.parseLocation(strLoc);
    final double riseset[] = calcRiseSet(new LocalDate(2003, 6, 24), location);

    assertEquals(6 + 33 * ONE_MINUTE, riseset[SunAlgorithm.RISE], DELTA);
    assertEquals(12 + 6 + 36 * ONE_MINUTE, riseset[SunAlgorithm.SET], DELTA);

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

    final String strLoc = "Sydney;Australia;Australia/Sydney;-3352+15113/";
    final Location location = LocationParser.parseLocation(strLoc);
    final double riseset[] = calcRiseSet(new LocalDate(2001, 12, 2), location);

    assertEquals(4 + 37 * ONE_MINUTE, riseset[SunAlgorithm.RISE], DELTA);
    assertEquals(18 + 52 * ONE_MINUTE, riseset[SunAlgorithm.SET], DELTA);

  }

  private double[] calcRiseSet(final LocalDate date, final Location location)
  {
    mSunAlgorithm.setLatitude(location.getPointLocation().getLatitude()
      .getDegrees());
    mSunAlgorithm.setLongitude(location.getPointLocation().getLongitude()
      .getDegrees());
    mSunAlgorithm.setTimeZoneOffset(location.getTimeZone().getRawOffset()
                                    / (1000.0 * 60.0 * 60.0));
    mSunAlgorithm.setYear(date.getYear());
    mSunAlgorithm.setMonth(date.getMonthOfYear());
    mSunAlgorithm.setDay(date.getDayOfMonth());

    return mSunAlgorithm.calcRiseSet(SunAlgorithm.SUNRISE_SUNSET);
  }

}
