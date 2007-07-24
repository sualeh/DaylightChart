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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import daylightchart.location.Location;

/**
 * A full year's sunrise and sunset times for a location.
 * 
 * @author Sualeh Fatehi
 */
final class RiseSetYear
{

  private final Location location;
  private final int year;
  private boolean usesDaylightTime;
  private LocalDate dstStart;
  private LocalDate dstEnd;
  private final List<RiseSet> riseSets;

  RiseSetYear(final Location location, final int year)
  {
    this.location = location;
    this.year = year;
    if (location != null)
    {
      final TimeZone timeZone = TimeZone.getTimeZone(location.getTimeZoneId());
      usesDaylightTime = timeZone.useDaylightTime();
    }
    riseSets = new ArrayList<RiseSet>();
  }

  void addRiseSet(final RiseSet riseSet)
  {
    riseSets.add(riseSet);
  }

  /**
   * Gets the end of DST.
   * 
   * @return End of DST.
   */
  Date getDstEndDate()
  {
    Date dstEndDate = null;
    if (usesDaylightTime && dstEnd != null)
    {
      dstEndDate = dstEnd.toDateTime((LocalTime) null).toGregorianCalendar()
        .getTime();
    }
    return dstEndDate;
  }

  /**
   * Gets the start of DST.
   * 
   * @return Start of DST.
   */
  Date getDstStartDate()
  {
    Date dstStartDate = null;
    if (usesDaylightTime && dstStart != null)
    {
      dstStartDate = dstStart.toDateTime((LocalTime) null)
        .toGregorianCalendar().getTime();
    }
    return dstStartDate;
  }

  /**
   * Location.
   * 
   * @return Location.
   */
  Location getLocation()
  {
    return location;
  }

  /**
   * Gets a list of rise/ set timings.
   * 
   * @return List of rise/ set timings.
   */
  List<RiseSet> getRiseSets(boolean adjustedForDaylightSavings)
  {
    List<RiseSet> copiedRiseSets;
    if (!adjustedForDaylightSavings)
    {
      copiedRiseSets = new ArrayList<RiseSet>();
      for (final RiseSet riseSet: riseSets)
      {
        copiedRiseSets.add(riseSet.withAdjustmentForDaylightSavings(adjustedForDaylightSavings));
      }
    }
    else
    {
      copiedRiseSets = riseSets;
    }
    return Collections.unmodifiableList(copiedRiseSets);
  }

  /**
   * Gets the year.
   * 
   * @return Year.
   */
  int getYear()
  {
    return year;
  }

  void setDstEnd(final LocalDate dstEnd)
  {
    this.dstEnd = dstEnd;
  }

  void setDstStart(final LocalDate dstStart)
  {
    this.dstStart = dstStart;
  }

  /**
   * Whether the location uses DST rules.
   * 
   * @return Whether the location uses DST rules.
   */
  boolean usesDaylightTime()
  {
    return usesDaylightTime;
  }

}
