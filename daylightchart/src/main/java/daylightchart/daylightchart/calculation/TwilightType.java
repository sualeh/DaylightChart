/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2013, Sualeh Fatehi.
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

  /** No twilight */
  NO(-5D / 6D),
  /** Civil twilight */
  CIVIL(-6D),
  /** Nautical twilight */
  NAUTICAL(-12D),
  /** Astronomical twilight */
  ASTRONOMICAL(-18D);

  private final double horizon;

  private TwilightType(final double horizon)
  {
    this.horizon = horizon;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString()
  {
    return name().substring(0, 1).toUpperCase()
           + name().substring(1).toLowerCase() + " twilight";
  }

  double getHorizon()
  {
    return horizon;
  }

}
