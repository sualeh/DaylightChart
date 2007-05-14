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
package daylightchart.location;


import java.io.Serializable;
import java.util.Comparator;

import org.pointlocation6709.Latitude;

/**
 * Sort order for locations.
 * 
 * @author Sualeh Fatehi
 */
public enum LocationsSortOrder
  implements Comparator<Location>, Serializable
{

  /** Sort locations by name. */
  BY_NAME,
  /** Sort locations by latitude. */
  BY_LATITUDE;

  private static final long serialVersionUID = 4483200154586111166L;

  /**
   * {@inheritDoc}
   * 
   * @see java.util.Comparator#compare(java.lang.Object,
   *      java.lang.Object)
   */
  public int compare(final Location location1, final Location location2)
  {
    int comparison = 0;
    switch (this)
    {
      case BY_LATITUDE:
        final Latitude latitude1 = location1.getPointLocation().getLatitude();
        final Latitude latitude2 = location2.getPointLocation().getLatitude();
        comparison = (int) Math.signum(latitude2.getRadians() -
                                       latitude1.getRadians());
        break;
      case BY_NAME:
        comparison = location1.compareTo(location2);
        break;
    }
    return comparison;
  }

}
