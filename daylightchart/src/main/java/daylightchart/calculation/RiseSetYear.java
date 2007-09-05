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
package daylightchart.calculation;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import daylightchart.location.Location;

/**
 * A full year's sunrise and sunset times for a location.
 * 
 * @author Sualeh Fatehi
 */
public final class RiseSetYear
  implements Serializable
{

  private static final long serialVersionUID = -7055404819725658424L;

  private final Location location;
  private final int year;
  private boolean usesDaylightTime;
  private LocalDate dstStart;
  private LocalDate dstEnd;
  private final List<RiseSetTuple> riseSets;
  private final List<RiseSetTuple> twilights;
  private final List<DaylightBand> bands;

  RiseSetYear(final Location location, final int year)
  {
    this.location = location;
    this.year = year;
    riseSets = new ArrayList<RiseSetTuple>();
    twilights = new ArrayList<RiseSetTuple>();
    bands = new ArrayList<DaylightBand>();
  }

  public List<DaylightBand> getBands()
  {
    return bands;
  }

  /**
   * Gets the end of DST.
   * 
   * @return End of DST.
   */
  public Date getDstEndDate()
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
  public Date getDstStartDate()
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
  public Location getLocation()
  {
    return location;
  }

  /**
   * Gets a list of rise/ set timings.
   * 
   * @return List of rise/ set timings.
   */
  public List<RiseSet> getRiseSets(boolean adjustedForDaylightSavings)
  {
    List<RiseSet> copiedRiseSets;
    if (!adjustedForDaylightSavings)
    {
      copiedRiseSets = new ArrayList<RiseSet>();
      for (final RiseSetTuple riseSetTuple: riseSets)
      {
        final RiseSet riseSet = new RiseSet(riseSetTuple);
        copiedRiseSets.add(riseSet
          .withAdjustmentForDaylightSavings(adjustedForDaylightSavings));
      }
    }
    else
    {
      copiedRiseSets = new ArrayList<RiseSet>();
      for (final RiseSetTuple riseSetTuple: riseSets)
      {
        final RiseSet riseSet = new RiseSet(riseSetTuple);
        copiedRiseSets.add(riseSet);
      }
    }
    return Collections.unmodifiableList(copiedRiseSets);
  }

  /**
   * Gets a list of twiligbt timings.
   * 
   * @return List of rise/ set timings.
   */
  public List<RiseSet> getTwilights()
  {
    List<RiseSet> copiedRiseSets;
    copiedRiseSets = new ArrayList<RiseSet>();
    for (final RiseSetTuple riseSetTuple: twilights)
    {
      final RiseSet riseSet = new RiseSet(riseSetTuple);
      copiedRiseSets.add(riseSet);
    }
    return Collections.unmodifiableList(copiedRiseSets);
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

  void addDaylightBands(final List<DaylightBand> bands)
  {
    this.bands.addAll(bands);
  }

  void addRiseSet(final RiseSetTuple riseSet)
  {
    riseSets.add(riseSet);
  }

  void addTwilight(final RiseSetTuple riseSet)
  {
    twilights.add(riseSet);
  }

  void setDstEnd(final LocalDate dstEnd)
  {
    this.dstEnd = dstEnd;
  }

  void setDstStart(final LocalDate dstStart)
  {
    this.dstStart = dstStart;
  }

  void setUsesDaylightTime(final boolean usesDaylightTime)
  {
    this.usesDaylightTime = usesDaylightTime;
  }

}
