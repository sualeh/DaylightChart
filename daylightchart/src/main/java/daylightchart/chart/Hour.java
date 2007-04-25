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
package daylightchart.chart;


import java.io.Serializable;

import org.joda.time.LocalTime;
import org.pointlocation6709.Utility;

/**
 * Represents an hour. An Hour can be converted to a string in standard
 * format, and in 24 hour time format. This object also adjusts the time
 * for daylight savings time.
 * 
 * @author Sualeh Fatehi
 */
public final class Hour
  implements Serializable, Comparable<Hour>
{

  private static final long serialVersionUID = 6973647622518973008L;

  private final LocalTime hour;
  /** Adjusts the output of the time format for daylight savings time. */
  private boolean inDaylightSavings;

  /**
   * Constructor for the hour, given the value of the hour.
   * 
   * @param hour
   *        The value of the hour.
   */
  public Hour(final double hour)
  {
    double dayHour = hour % 24D;
    if (dayHour < 0)
    {
      dayHour = dayHour + hour;
    }
    final int[] fields = Utility.sexagesimalSplit(dayHour);
    this.hour = new LocalTime(fields[0], fields[1], fields[2]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(final Hour otherHour)
  {
    return hour.compareTo(otherHour.hour);
  }

  /**
   * Get local time.
   * 
   * @return Local time
   */
  public final LocalTime getLocalTime()
  {
    LocalTime adjustedHour;
    if (inDaylightSavings)
    {
      adjustedHour = hour.plusHours(1);
    }
    else
    {
      adjustedHour = hour;
    }

    return adjustedHour;
  }

  /**
   * Whether daylight savings time is in effect.
   * 
   * @return Whether in DST.
   */
  public boolean isInDaylightSavings()
  {
    return inDaylightSavings;
  }

  /**
   * Set whether this hour uses daylight savings time.
   * 
   * @param inDaylightSavings
   *        the inDaylightSavings to set
   */
  public void setInDaylightSavings(final boolean inDaylightSavings)
  {
    this.inDaylightSavings = inDaylightSavings;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return getLocalTime().toString();
  }

}
