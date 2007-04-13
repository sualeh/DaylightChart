/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package org.pointlocation6709.parser;


import org.pointlocation6709.Angle;
import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;

/**
 * Formats point locations to strings.
 * 
 * @author Sualeh Fatehi
 */
public final class PointLocationFormatter
{

  /**
   * Formats a point location as an ISO 6709:1983 string.
   * 
   * @param pointLocation
   *        Point location to format
   * @return Formatted string
   */
  public static String formatIso6709(final PointLocation pointLocation)
  {
    final Latitude latitude = pointLocation.getLatitude();
    final Longitude longitude = pointLocation.getLongitude();
    String string = formatIntegerDegreesString(latitude)
                    + formatSexagesimalMinutesString(latitude)
                    + formatIntegerDegreesString(longitude)
                    + formatSexagesimalMinutesString(longitude);
    final double altitude = pointLocation.getAltitude();
    if (altitude != 0)
    {
      string = string + formatDoubleWithSign(altitude);
    }
    return string + "/";
  }

  /**
   * Formats a point location as an ISO 6709:1983 string, using
   * decimals.
   * 
   * @param pointLocation
   *        Point location to format
   * @return Formatted string
   */
  public static String formatIso6709WithDecimals(final PointLocation pointLocation)
  {
    final Latitude latitude = pointLocation.getLatitude();
    final Longitude longitude = pointLocation.getLongitude();
    String string = formatIntegerDegreesString(latitude)
                    + formatDecimalMinutesString(latitude)
                    + formatIntegerDegreesString(longitude)
                    + formatDecimalMinutesString(longitude);
    final double altitude = pointLocation.getAltitude();
    if (altitude != 0)
    {
      string = string + formatDoubleWithSign(altitude);
    }
    return string + "/";
  }

  private static String formatDecimalMinutesString(final Angle angle)
  {
    final int intDegrees = Math.abs(angle.getField(Angle.Field.DEGREES));
    final double absMinutes = Math.abs(angle.getDegrees()) - intDegrees;

    return String.valueOf(absMinutes);
  }

  private static String formatDoubleWithSign(final double value)
  {
    final String sign = value < 0? "-": "+";
    return sign + Math.abs(value);
  }

  private static String formatIntegerDegreesString(final Angle angle)
  {
    String sign = angle.getRadians() < 0? "-": "+";
    final int intDegrees = Math.abs(angle.getField(Angle.Field.DEGREES));

    final int fieldlength;
    if (angle instanceof Latitude)
    {
      fieldlength = 2;
    }
    else if (angle instanceof Longitude)
    {
      // According to the ISO6709:1983 standard,
      // the 180th meridian is negative
      if (intDegrees == 180)
      {
        sign = "-";
      }
      fieldlength = 3;
    }
    else
    {
      fieldlength = 0;
    }

    final String degrees = padInt(intDegrees, fieldlength);

    return sign + degrees;
  }

  private static String formatSexagesimalMinutesString(final Angle angle)
  {
    final int absMinutes = Math.abs(angle.getField(Angle.Field.MINUTES));
    final int absSeconds = Math.abs(angle.getField(Angle.Field.SECONDS));

    return padInt(absMinutes, 2) + padInt(absSeconds, 2);
  }

  /**
   * Pads a string with a pad character. Includes the sign before the
   * integer.
   * 
   * @param quantity
   *        Integer quantity to pad.
   * @param width
   *        Width of the padding.
   * @return Padded string to length "width", with the sign preceeding
   *         the integer.
   */
  private static String padInt(final int quantity, final int width)
  {
    final StringBuffer paddedString = new StringBuffer();

    paddedString.append(quantity);
    while (paddedString.length() < width)
    {
      paddedString.insert(0, '0');
    }

    return new String(paddedString);
  }

  private PointLocationFormatter()
  {
    // Prevent instantiation
  }

}
