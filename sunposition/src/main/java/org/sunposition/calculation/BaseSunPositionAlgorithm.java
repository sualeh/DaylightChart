/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2010, Sualeh Fatehi.
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

/**
 * @author Sualeh Fatehi
 */
abstract class BaseSunPositionAlgorithm
  implements SunPositionAlgorithm
{

  /** Day, 1 to 31. */
  protected int day;
  /** Month, 1 to 12. */
  protected int month;
  /** Four digit year. */
  protected int year;

  /** Location name. */
  protected String locationName;
  /** Latitude in degrees, North positive. */
  protected double latitude;
  /** Longitude in degrees, East positive. */
  protected double longitude;

  /** Timezone offset from GMT, in hours. */
  protected double timezoneOffset;

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

    double riseset[];
    try
    {
      riseset = calcRiseSet(SUNRISE_SUNSET);
    }
    catch (final RuntimeException e)
    {
      riseset = new double[] {
          Double.NaN, Double.NaN
      };
    }

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    new PrintStream(outputStream, true)
      .printf("%s %5.2f, %5.2f; date=%4d-%2d-%2d; time zone=%5.2f%n sunrise %s%n sunset %s",
              locationName,
              latitude,
              longitude,
              year,
              month,
              day,
              timezoneOffset,
              toLocalTime(riseset[RISE]),
              toLocalTime(riseset[SET]));
    return outputStream.toString();
  }

  /**
   * Cosine of an angle expressed in degrees.
   * 
   * @param degrees
   *        Degrees.
   * @return Cosine of the angle.
   */
  protected double cosD(final double degrees)
  {
    return Math.cos(Math.toRadians(degrees));
  }

  /**
   * Sine of an angle expressed in degrees.
   * 
   * @param degrees
   *        Degrees.
   * @return Sine of the angle.
   */
  protected double sinD(final double degrees)
  {
    return Math.sin(Math.toRadians(degrees));
  }

  /**
   * Tangent of an angle expressed in degrees.
   * 
   * @param degrees
   *        Degrees.
   * @return Tangent of the angle.
   */
  protected double tanD(final double degrees)
  {
    return Math.tan(Math.toRadians(degrees));
  }

  private String toLocalTime(final double hour)
  {
    double dayHour = hour % 24D;
    if (dayHour < 0)
    {
      dayHour = dayHour + 24;
    }
    // Calculate absolute integer units
    final int timeHour = (int) Math.floor(Math.abs(dayHour));
    final int timeMinutes = (int) Math
      .round((Math.abs(dayHour) - dayHour) * 60D);
    return (timeHour < 10? "0": "") + timeHour + ":"
           + (timeMinutes < 10? "0": "") + timeMinutes;
  }

}
