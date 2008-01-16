/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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
package daylightchart.daylightchart.calculation;


/**
 * Twilight type.
 * 
 * @author Sualeh Fatehi
 */
public enum TwilightType
{

  NONE("None", -5D / 6D),
  CIVIL("Civil twilight", -6D),
  NAUTICAL("Nautical twilight", -12D),
  ASTRONOMICAL("Astronomical twilight", -18D);

  private final double horizon;

  private final String description;

  private TwilightType(final String description, final double horizon)
  {
    this.description = description;
    this.horizon = horizon;
  }

  public String getDescription()
  {
    return description;
  }

  @Override
  public String toString()
  {
    return description;
  }

  double getHorizon()
  {
    return horizon;
  }

}
