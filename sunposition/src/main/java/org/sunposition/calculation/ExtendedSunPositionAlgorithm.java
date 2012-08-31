/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2012, Sualeh Fatehi.
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
 * Defines the interface the sunrise algorithms need to use.
 * <p/>
 * The values that define the location of the horizon for an observer at
 * sea level:
 * </p>
 * 
 * <pre>
 *  -0° 50'    Sunrise/ Sunset
 *  -6°        Civil Twilight (default twilight)
 * -12°        Nautical Twilight
 * -18°        Astronomical Twilight
 * </pre>
 * <p>
 * Note that these values are related to the horizon (90° from the
 * azimuth). If the observer is above or below the horizon, the correct
 * adopted true sunrise/sunset altitude in degrees is -(50/60) - 0.0353
 * * sqrt(height in meters);
 * </p>
 * <p>
 * Sunrise is defined as the time when the apparent altitlude of the
 * upper limb of the Sun will be -50 arc minutes below the horizon. This
 * takes into account refraction and solar semi-diameter effects.
 * </p>
 * 
 * @author Sualeh Fatehi
 */
public interface ExtendedSunPositionAlgorithm
  extends SunPositionAlgorithm
{

  /**
   * Solar ephemerides.
   * 
   * @author Sualeh Fatehi
   */
  public interface SolarEphemerides
  {

    /**
     * Altitude.
     * 
     * @return Altitude.
     */
    double getAltitude();

    /**
     * Azimuth.
     * 
     * @return Azimuth.
     */
    double getAzimuth();

    /**
     * Declination.
     * 
     * @return Declination
     */
    double getDeclination();

    /**
     * Equation of Time.
     * 
     * @return Equation of Time.
     */
    double getEquationOfTime();

    /**
     * Hour angle.
     * 
     * @return Hour angle.
     */
    double getHourAngle();

    /**
     * Right Ascension.
     * 
     * @return Right Ascension.
     */
    double getRightAscension();

  }

  /**
   * Calculate the solar ephemerides.
   * 
   * @param hour
   *        Hour of the day.
   * @return Solar ephemerides.
   */
  public ExtendedSunPositionAlgorithm.SolarEphemerides calcSolarEphemerides(final double hour);

}
