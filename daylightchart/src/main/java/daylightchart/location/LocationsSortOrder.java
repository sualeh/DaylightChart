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
package daylightchart.location;


import java.util.Comparator;

import org.pointlocation6709.Latitude;

/**
 * Sort order for locations.
 * 
 * @author Sualeh Fatehi
 */
public enum LocationsSortOrder
  implements Comparator<Location>
{

  BY_NAME("By name")
  {
    public int compare(final Location location1, final Location location2)
    {
      return location1.getDescription().toLowerCase().compareTo(location2
        .getDescription().toLowerCase());
    }
  },
  BY_LATITUDE("By latitude")
  {
    public int compare(final Location location1, final Location location2)
    {
      final Latitude latitude1 = location1.getPointLocation().getLatitude();
      final Latitude latitude2 = location2.getPointLocation().getLatitude();
      return (int) Math.signum(latitude2.getRadians() - latitude1.getRadians());
    }
  };

  private static final long serialVersionUID = 4483200154586111166L;

  private final String description;

  private LocationsSortOrder(final String description)
  {
    this.description = description;
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

}
