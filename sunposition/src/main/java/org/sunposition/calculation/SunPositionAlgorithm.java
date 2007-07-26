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
package org.sunposition.calculation;


/**
 * <p>
 * Defines the interface the sunrise algorithms need to use. <p/> The
 * values that define the location of the horizon for an observer at sea
 * level:
 * </p>
 * 
 * <pre>
 *  -0° 50'    Sunrise/ Sunset
 *  -6°        Civil Twilight (default twilight)
 * -12°        Nautical Twilight
 * -18°        Astronomical Twilight
 * </pre>
 * 
 * <p>
 * Note that these values are related to the horizon (90° from the
 * azimuth). If the observer is above or below the horizon, the correct
 * adopted true sunrise/sunset altitude in degrees is -(50/60) - 0.0353 *
 * sqrt(height in meters);
 * </p>
 * <p>
 * Sunrise is defined as the time when the apparent altitlude of the
 * upper limb of the Sun will be -50 arc minutes below the horizon. This
 * takes into account refraction and solar semi-diameter effects.
 * </p>
 * 
 * @author Sualeh Fatehi
 */
public interface SunPositionAlgorithm
{

  /*
   * The solar ephemeris calculation returns values in a double[]
   * vector. Use these constants to access elements of the array.
   */
  /** Constant indexing a field in the solar ephemeris result. */
  int DECLINATION = 0;
  /** Constant indexing a field in the solar ephemeris result. */
  int RIGHTASCENSION = 1;
  /** Constant indexing a field in the solar ephemeris result. */
  int HOURANGLE = 2;
  /** Constant indexing a field in the solar ephemeris result. */
  int AZIMUTH = 3;
  /** Constant indexing a field in the solar ephemeris result. */
  int ALTITUDE = 4;
  /** Constant indexing a field in the solar ephemeris result. */
  int EQUATIONOFTIME = 5;

  /*
   * The sunrise algorithm return values in an array. Use these
   * constants to access elements of the array.
   */
  /** Constant indexing a field in the sunrise and sunset result. */
  int RISE = 0;
  /** Constant indexing a field in the sunrise and sunset result. */
  int SET = 1;

  /*
   * ABOVE_HORIZON and BELOW_HORIZON are returned for sun calculations
   * where the astronomical object does not cross the horizon.
   */
  /**
   * Constant for when the astronomical object does not cross the
   * horizon.
   */
  double ABOVE_HORIZON = Double.POSITIVE_INFINITY;

  /**
   * Constant for when the astronomical object does not cross the
   * horizon.
   */
  double BELOW_HORIZON = Double.NEGATIVE_INFINITY;

  /** Sunrise and sunset angle, in degrees */
  double SUNRISE_SUNSET = -(50.0 / 60.0);
  double CIVIL_TWILIGHT = -6.0;
  double NAUTICAL_TWILIGHT = -12.0;
  double ASTRONOMICAL_TWILIGHT = -18.0;
  double TWILIGHT = CIVIL_TWILIGHT;

  /**
   * Compute the time of sunrise and sunset for this date.
   * 
   * @param dblHorizon
   *        The adopted true altitude of the horizon in degrees. Use one
   *        of the following values. <br>
   *        <ul>
   *        <li> SUNRISE_SUNSET </li>
   *        <li> CIVIL_TWILIGHT </li>
   *        <li> NAUTICAL_TWILIGHT </li>
   *        <li> ASTRONOMICAL_TWILIGHT</li>
   *        </ul>
   * @return Array for sunrise and sunset times. Use RISE and SET as
   *         indices into this array.
   */
  double[] calcRiseSet(final double dblHorizon);

  /**
   * Calculate solar ephemeris.
   * 
   * @param hour
   *        Hour past midnight (for the current day).
   * @return Solar ephemerides, as an array. Use one of the following
   *         values to index elements of the array. <br>
   *         <ol>
   *         <li> DECLINATION </li>
   *         <li> RIGHTASCENSION </li>
   *         <li> HOURANGLE </li>
   *         <li> AZIMUTH </li>
   *         <li> ALTITUDE </li>
   *         <li> EQUATIONOFTIME</li>
   *         </ol>
   */
  double[] calcSolarEphemeris(double hour);

  /**
   * Day, 1 to 31.
   * 
   * @param year
   *        Four digit year.
   * @param month
   *        Month, 1 to 12.
   * @param day
   *        Day, 1 to 31.
   */
  void setDate(int year, int month, int day);

  /**
   * Location.
   * 
   * @param latitude
   *        Latitude in degrees, North positive.
   * @param longitude
   *        Longitude in degrees, East positive.
   */
  void setLocation(double latitude, double longitude);

  /**
   * Time zone offset from GMT, in hours.
   * 
   * @param timeZoneOffset
   *        Time zone offset.
   */
  void setTimeZoneOffset(double timeZoneOffset);

}
