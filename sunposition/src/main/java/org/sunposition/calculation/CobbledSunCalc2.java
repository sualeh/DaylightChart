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
 * Computes the times of sunrise and sunset for a specified date and
 * location. Also finds the sun's co-ordinates and ephemeris for a given
 * hour.
 * </p>
 * <p>
 * Algorithms from "Astronomy on the Personal Computer" by Oliver
 * Montenbruck and Thomas Pfleger. Springer Verlag 1994. ISBN
 * 3-540-57700-9.
 * </p>
 * <p>
 * This is a reasonably accurate and very robust procedure for sunrise
 * that will handle unusual cases, such as the one day in the year in
 * arctic latitudes that the sun rises, but does not set.
 * </p>
 * 
 * @author Sualeh Fatehi
 */
final class CobbledSunCalc2
  extends CobbledSunCalc
{

  /**
   * @param horizon
   *        The adopted true altitude of the horizon in degrees. Use one
   *        of the following values. <br>
   *        <ul>
   *        <li>SUNRISE_SUNSET</li>
   *        <li>CIVIL_TWILIGHT</li>
   *        <li>NAUTICAL_TWILIGHT</li>
   *        <li>ASTRONOMICAL_TWILIGHT</li>
   *        </ul>
   * @return Array for sunrise and sunset times. Use RISE and SET as
   *         indices into this array.
   * @see <a
   *      href="http://www.merrymeet.com/minow/sunclock/Sun.java">Sun.java</a>
   * @see <a
   *      href="http://www.btinternet.com/~kburnett/kepler/moonrise.html">Moon
   *      and Sun rise and set for any latitude</a>
   */
  public double[] calcRiseSet(final double horizon)
  {

    double eqtime = getEquationOfTime(12);
    double decl = Math.toRadians(getSolarDeclination(12));

    // Hour angle, in degrees
    final double zenith = Math.toRadians(90 + horizon);
    final double latitudeRad = Math.toRadians(latitude);
    final double ha = Math.toDegrees(Math.acos(Math.cos(zenith)
                                               / (Math.cos(latitudeRad) * Math
                                                 .cos(decl))
                                               - Math.tan(latitudeRad)
                                               * Math.tan(decl)));

    // Sunrise and sunset
    double sunrise = (720D + 4D * (longitude - ha) - eqtime) / 60D;
    double sunset = (720D + 4D * (longitude + ha) - eqtime) / 60D;

    return new double[] {
        sunrise, sunset
    };
  }

}
