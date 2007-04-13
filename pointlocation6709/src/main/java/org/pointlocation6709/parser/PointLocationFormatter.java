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
    String string = format(pointLocation.getLatitude(), 7)
                    + format(pointLocation.getLongitude(), 8);
    if (pointLocation.getAltitude() != 0)
    {
      string = string + pointLocation.getAltitude();
    }
    return string + "/";
  }

  /**
   * Formats to a string, in a parseable way.
   * 
   * @return Formated string.
   */
  private static String format(final Angle angle, final int fieldlength)
  {
    final int angleSign = angle.getRadians() < 0? -1: 1;
    final int quantity = angleSign
                         * (Math.abs(angle.getField(Angle.Field.DEGREES))
                            * 10000
                            + Math.abs(angle.getField(Angle.Field.MINUTES))
                            * 100 + Math.abs(angle
                           .getField(Angle.Field.SECONDS)));
    return padInt(quantity, fieldlength);
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

    paddedString.append(Math.abs(quantity));
    while (paddedString.length() < width - 1)
    {
      paddedString.insert(0, '0');
    }

    if (quantity < 0)
    {
      paddedString.insert(0, "-");
    }
    else
    {
      paddedString.insert(0, "+");
    }

    return new String(paddedString);
  }

  private PointLocationFormatter()
  {
    // Prevent instantiation
  }

}
