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
package org.pointlocation6709;


/**
 * Represents a latitude in degrees or radians.
 * 
 * @author Sualeh Fatehi
 */
public final class Latitude
  extends Angle
{

  private static final long serialVersionUID = -1048509855080052523L;

  /**
   * Copy constructor. Copies the value of a provided angle.
   * 
   * @param angle
   *        Angle to copy the value from.
   */
  public Latitude(final Angle angle)
  {
    super(angle);
    validateDegreesRange(90);
  }

  @Override
  protected String getDirection()
  {
    if (getRadians() < 0)
    {
      return "S";
    }
    else
    {
      return "N";
    }
  }

}
