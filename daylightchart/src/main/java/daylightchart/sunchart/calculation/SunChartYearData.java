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
package daylightchart.sunchart.calculation;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.geoname.data.Location;

/**
 * A full year's sunrise and sunset times for a location.
 * 
 * @author Sualeh Fatehi
 */
public final class SunChartYearData
  implements Serializable
{

  private static final long serialVersionUID = -7055404819725658424L;

  private final Location location;
  private final int year;
  private boolean usesDaylightTime;
  private final List<SunPositions> sunPositionsList;

  SunChartYearData(final Location location, final int year)
  {
    this.location = location;
    this.year = year;
    sunPositionsList = new ArrayList<SunPositions>();
  }

  /**
   * Location.
   * 
   * @return Location.
   */
  public Location getLocation()
  {
    return location;
  }

  /**
   * @return the sunPositions
   */
  public List<SunPositions> getSunPositionsList()
  {
    return sunPositionsList;
  }

  /**
   * Gets the year.
   * 
   * @return Year.
   */
  public int getYear()
  {
    return year;
  }

  /**
   * Whether the location uses DST rules.
   * 
   * @return Whether the location uses DST rules.
   */
  public boolean usesDaylightTime()
  {
    return usesDaylightTime;
  }

  /**
   * @param sunPositions
   *        the sunPositions to set
   */
  void add(final SunPositions sunPositions)
  {
    sunPositionsList.add(sunPositions);
  }

  void setUsesDaylightTime(final boolean usesDaylightTime)
  {
    this.usesDaylightTime = usesDaylightTime;
  }

}
