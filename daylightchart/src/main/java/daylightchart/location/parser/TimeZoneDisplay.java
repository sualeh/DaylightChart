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
package daylightchart.location.parser;


import java.io.Serializable;
import java.util.TimeZone;

public final class TimeZoneDisplay
  implements Serializable, Comparable<TimeZoneDisplay>
{

  private static final long serialVersionUID = 3841508979907339562L;

  private final String timeZoneId;
  private final String timeZoneDisplayName;

  public TimeZoneDisplay(final TimeZone timeZone)
  {
    if (timeZone == null)
    {
      throw new IllegalArgumentException("Cannot use null time zone");
    }
    timeZoneId = timeZone.getID();
    timeZoneDisplayName = timeZone.getDisplayName();
  }

  public int compareTo(final TimeZoneDisplay other)
  {
    int compareTo = 0;
    if (compareTo == 0)
    {
      compareTo = timeZoneDisplayName.compareTo(other.timeZoneDisplayName);
    }
    if (compareTo == 0)
    {
      compareTo = timeZoneId.compareTo(other.timeZoneId);
    }
    return compareTo;
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final TimeZoneDisplay other = (TimeZoneDisplay) obj;
    if (timeZoneDisplayName == null)
    {
      if (other.timeZoneDisplayName != null)
      {
        return false;
      }
    }
    else if (!timeZoneDisplayName.equals(other.timeZoneDisplayName))
    {
      return false;
    }
    if (timeZoneId == null)
    {
      if (other.timeZoneId != null)
      {
        return false;
      }
    }
    else if (!timeZoneId.equals(other.timeZoneId))
    {
      return false;
    }
    return true;
  }

  public String getTimeZoneDisplayName()
  {
    return timeZoneDisplayName;
  }

  public String getTimeZoneId()
  {
    return timeZoneId;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + (timeZoneDisplayName == null? 0: timeZoneDisplayName.hashCode());
    result = prime * result + (timeZoneId == null? 0: timeZoneId.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    return timeZoneDisplayName + " (" + timeZoneId + ")";
  }

}
