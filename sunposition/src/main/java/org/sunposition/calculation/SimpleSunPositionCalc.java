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


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.joda.time.LocalDate;

/**
 * @author Sualeh Fatehi
 */
final class SimpleSunPositionCalc
  implements SunPositionAlgorithm
{

  /** Day, 1 to 31. */
  private int day;
  /** Month, 1 to 12. */
  private int month;
  /** Four digit year. */
  private int year;

  /** Location name. */
  private String locationName;
  /** Latitude in degrees, North positive. */
  private double latitude;
  /** Longitude in degrees, East positive. */
  private double longitude;

  /** Timezone offset from GMT, in hours. */
  private double timezoneOffset;

  /**
   * {@inheritDoc}
   * 
   * @see org.sunposition.calculation.SunPositionAlgorithm#calcRiseSet(double)
   */
  public double[] calcRiseSet(final double horizon)
  {

    // Fractional year, in radians
    final LocalDate date = new LocalDate(year, month, day);
    final double hour = 24;
    final double gamma = 2D
                         * Math.PI
                         * ((double) date.getDayOfYear() - 1D + (hour - 12D) / 24D)
                         / 365D;

    // Equation of time, in minutes
    final double eqtime = 229.18 * (0.000075 + 0.001868 * Math.cos(gamma)
                                    - 0.032077 * Math.sin(gamma) - 0.014615
                                    * Math.cos(2D * gamma) - 0.040849 * Math
      .sin(2D * gamma));
    System.out.printf("Equation of time: %4.2f%n", eqtime);

    // Solar declination angle, in radians
    final double decl = 0.006918 - 0.399912 * Math.cos(gamma) + 0.070257
                        * Math.sin(gamma) - 0.006758 * Math.cos(2D * gamma)
                        + 0.000907 * Math.sin(2D * gamma) - 0.002697
                        * Math.cos(3D * gamma) + 0.00148 * Math.sin(3D * gamma);
    System.out.printf("Solar declination: %4.2f%n", Math.toDegrees(decl));

    // Hour angle, in degrees
    final double zenith = Math.toRadians(90 + horizon);
    final double latitudeRad = Math.toRadians(latitude);
    final double ha = Math.toDegrees(Math.acos(Math.cos(zenith)
                                               / (Math.cos(latitudeRad) * Math
                                                 .cos(decl))
                                               - Math.tan(latitudeRad)
                                               * Math.tan(decl)));

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
   * @see org.sunposition.calculation.SunPositionAlgorithm#calcSolarEphemeris(double)
   */
  public double[] calcSolarEphemeris(final double hour)
  {
    throw new RuntimeException("Not implemented");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sunposition.calculation.SunPositionAlgorithm#setDate(int,
   *      int, int)
   */
  public void setDate(final int year, final int month, final int day)
  {
    if (year < 1500 || year > 3000)
    {
      throw new IllegalArgumentException("Out of range: " + year);
    }
    this.year = year;

    if (month < 1 || month > 12)
    {
      throw new IllegalArgumentException("Out of range: " + month);
    }
    this.month = month;

    if (day < 1 || day > 31)
    {
      throw new IllegalArgumentException("Day out of range: " + day);
    }
    this.day = day;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.sunposition.calculation.SunPositionAlgorithm#setLocation(String,
   *      double, double)
   */
  public void setLocation(final String locationName,
                          final double latitude,
                          final double longitude)
  {
    this.locationName = locationName;

    if (Math.abs(latitude) > 90)
    {
      throw new IllegalArgumentException("Out of range: " + latitude);
    }
    this.latitude = latitude;

    if (Math.abs(longitude) > 180)
    {
      throw new IllegalArgumentException("Out of range: " + longitude);
    }
    this.longitude = longitude;
  }

  /**
   * Time zone offset from GMT, in hours.
   * 
   * @param timeZoneOffset
   *        Time zone offset.
   */
  public void setTimeZoneOffset(final double timeZoneOffset)
  {
    if (Math.abs(timeZoneOffset) > 13)
    {
      throw new IllegalArgumentException("Out of range: " + timeZoneOffset);
    }
    timezoneOffset = timeZoneOffset;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @SuppressWarnings("boxing")
  @Override
  public String toString()
  {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    new PrintStream(outputStream, true)
      .printf("%s %5.2f, %5.2f; date=%i-%i-%i;time zone=%5.2f",
              locationName,
              latitude,
              longitude,
              timezoneOffset,
              year,
              month,
              day);
    return outputStream.toString();
  }

}
