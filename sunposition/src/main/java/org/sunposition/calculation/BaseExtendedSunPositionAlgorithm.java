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
 * Computes the times of sunrise and sunset for a specified date and
 * location. Also finds the sun's co-ordinates and ephemerides for a
 * given hour.
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
abstract class BaseExtendedSunPositionAlgorithm
  extends BaseSunPositionAlgorithm
  implements ExtendedSunPositionAlgorithm
{

  static class BaseSolarEphemerides
    implements ExtendedSunPositionAlgorithm.SolarEphemerides
  {

    private double declination;
    private double rightAscension;
    private double hourAngle;
    private double azimuth;
    private double altitude;
    private double equationOfTime;

    /**
     * {@inheritDoc}
     * 
     * @see org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides#getAltitude()
     */
    public double getAltitude()
    {
      return altitude;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides#getAzimuth()
     */
    public double getAzimuth()
    {
      return azimuth;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides#getDeclination()
     */
    public double getDeclination()
    {
      return declination;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides#getEquationOfTime()
     */
    public double getEquationOfTime()
    {
      return equationOfTime;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides#getHourAngle()
     */
    public double getHourAngle()
    {
      return hourAngle;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides#getRightAscension()
     */
    public double getRightAscension()
    {
      return rightAscension;
    }

    void setAltitude(final double altitude)
    {
      this.altitude = altitude;
    }

    void setAzimuth(final double azimuth)
    {
      this.azimuth = azimuth;
    }

    void setDeclination(final double declination)
    {
      this.declination = declination;
    }

    void setEquationOfTime(final double equationOfTime)
    {
      this.equationOfTime = equationOfTime;
    }

    void setHourAngle(final double hourAngle)
    {
      this.hourAngle = hourAngle;
    }

    void setRightAscension(final double rightAscension)
    {
      this.rightAscension = rightAscension;
    }

  }

}
