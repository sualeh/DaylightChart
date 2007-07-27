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


import org.joda.time.LocalDate;

/**
 * @author Sualeh Fatehi
 */
final class SimpleSunPositionCalc
  extends BaseSunPositionAlgorithm
{

  /**
   * {@inheritDoc}
   * 
   * @see org.sunposition.calculation.SunPositionAlgorithm#calcRiseSet(double)
   */
  public double[] calcRiseSet(final double horizon)
  {

    final double hour = 24;

    // Equation of time, in minutes
    final double eqtime = getEquationOfTime(hour);

    // Solar declination angle, in radians
    final double decl = getSolarDeclination(hour);

    // Hour angle, in degrees
    final double ha = Math.toDegrees(Math.acos(cosD(90 + horizon)
                                               / (cosD(latitude) * cosD(decl))
                                               - tanD(latitude) * tanD(decl)));

    // Sunrise and sunset
    final double sunrise = (720D + 4D * (longitude - ha) - eqtime) / 60D;
    final double sunset = (720D + 4D * (longitude + ha) - eqtime) / 60D;

    return new double[] {
        sunrise, sunset
    };

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sunposition.calculation.SunPositionAlgorithm#getEquationOfTime(double)
   */
  public double getEquationOfTime(final double hour)
  {
    // Fractional year, in radians
    final double gamma = Math.toRadians(getFractionalYear(hour));

    // Equation of time, in minutes
    final double eqtime = 229.18 * (0.000075 + 0.001868 * Math.cos(gamma)
                                    - 0.032077 * Math.sin(gamma) - 0.014615
                                    * Math.cos(2D * gamma) - 0.040849 * Math
      .sin(2D * gamma));

    return eqtime;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sunposition.calculation.SunPositionAlgorithm#getSolarDeclination(double)
   */
  public double getSolarDeclination(final double hour)
  {
    // Fractional year, in radians
    final double gamma = Math.toRadians(getFractionalYear(hour));

    // Solar declination angle, in radians
    final double decl = 0.006918 - 0.399912 * Math.cos(gamma) + 0.070257
                        * Math.sin(gamma) - 0.006758 * Math.cos(2D * gamma)
                        + 0.000907 * Math.sin(2D * gamma) - 0.002697
                        * Math.cos(3D * gamma) + 0.00148 * Math.sin(3D * gamma);

    return Math.toDegrees(decl);
  }

  /**
   * Get the fractional year, in degrees.
   * 
   * @param hour
   *        Hour.
   * @return Fractional year
   */
  private double getFractionalYear(final double hour)
  {
    final LocalDate date = new LocalDate(year, month, day);
    final double fractionalYear = (date.getDayOfYear() - 1D + (hour - 12D) / 24D) / 365D;
    final double gamma = 360 * fractionalYear;
    return gamma;
  }

}
