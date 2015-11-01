/*
 *
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2015, Sualeh Fatehi.
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


import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geoname.data.Location;

/**
 * A full year's sunrise and sunset times for a location.
 *
 * @author Sualeh Fatehi
 */
public final class RiseSetYearData
  implements Serializable
{

  private static final long serialVersionUID = -7055404819725658424L;

  private final Location location;
  private final int year;
  private boolean usesDaylightTime;
  private LocalDate dstStart;
  private LocalDate dstEnd;
  private final TwilightType twilight;
  private final List<RawRiseSet> riseSets;
  private final List<RawRiseSet> twilights;
  private final List<DaylightBand> bands;

  RiseSetYearData(final Location location,
                  final TwilightType twilight,
                  final int year)
  {
    this.location = location;
    this.year = year;
    this.twilight = twilight;
    riseSets = new ArrayList<RawRiseSet>();
    twilights = new ArrayList<RawRiseSet>();
    bands = new ArrayList<DaylightBand>();
  }

  /**
   * Gets all the daylight bands.
   *
   * @return Daylight bands
   */
  public List<DaylightBand> getBands()
  {
    return new ArrayList<DaylightBand>(bands);
  }

  /**
   * Gets the end of DST.
   *
   * @return End of DST.
   */
  public LocalDate getDstEndDate()
  {
    if (usesDaylightTime && dstEnd != null)
    {
      return dstEnd;
    }
    return null;
  }

  /**
   * Gets the start of DST.
   *
   * @return Start of DST.
   */
  public LocalDate getDstStartDate()
  {
    if (usesDaylightTime && dstStart != null)
    {
      return dstStart;
    }
    return null;
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
   * Sunrise and sunset data for the year.
   *
   * @return Sunrise and sunset data for the year
   */
  public List<RiseSetData> getRiseSetData()
  {
    final List<RiseSetData> riseSetData = new ArrayList<RiseSetData>();
    final List<RiseSet> riseSets = getRiseSets(true);
    final List<RiseSet> twilights = getTwilights();
    for (int i = 0; i < riseSets.size(); i++)
    {
      final RiseSet riseSet = riseSets.get(i);
      final RiseSet twilight = twilights.get(i);
      riseSetData.add(new RiseSetData(riseSet, twilight));
    }
    return riseSetData;
  }

  /**
   * Gets the twilight type.
   *
   * @return TwilightType type.
   */
  public TwilightType getTwilight()
  {
    return twilight;
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

  void addRiseSet(final RawRiseSet riseSet)
  {
    riseSets.add(riseSet);
  }

  void addTwilight(final RawRiseSet riseSet)
  {
    twilights.add(riseSet);
  }

  List<RawRiseSet> getRawRiseSets()
  {
    return Collections.unmodifiableList(riseSets);
  }

  /**
   * Gets a list of twilight timings.
   *
   * @return List of rise/ set timings.
   */
  List<RawRiseSet> getRawTwilights()
  {
    return Collections.unmodifiableList(twilights);
  }

  /**
   * Gets a list of rise/ set timings.
   *
   * @param adjustedForDaylightSavings
   *        Whether the times need to be adjusted for daylight savings
   *        time
   * @return List of rise/ set timings.
   */
  List<RiseSet> getRiseSets(final boolean adjustedForDaylightSavings)
  {
    List<RiseSet> copiedRiseSets;
    if (!adjustedForDaylightSavings)
    {
      copiedRiseSets = new ArrayList<RiseSet>();
      for (final RawRiseSet riseSetTuple: riseSets)
      {
        final RiseSet riseSet = new RiseSet(riseSetTuple);
        copiedRiseSets.add(riseSet
          .withAdjustmentForDaylightSavings(adjustedForDaylightSavings));
      }
    }
    else
    {
      copiedRiseSets = new ArrayList<RiseSet>();
      for (final RawRiseSet riseSetTuple: riseSets)
      {
        final RiseSet riseSet = new RiseSet(riseSetTuple);
        copiedRiseSets.add(riseSet);
      }
    }
    return Collections.unmodifiableList(copiedRiseSets);
  }

  /**
   * Gets a list of twilight timings.
   *
   * @return List of rise/ set timings.
   */
  List<RiseSet> getTwilights()
  {
    List<RiseSet> copiedRiseSets;
    copiedRiseSets = new ArrayList<RiseSet>();
    for (final RawRiseSet riseSetTuple: twilights)
    {
      final RiseSet riseSet = new RiseSet(riseSetTuple);
      copiedRiseSets.add(riseSet);
    }
    return Collections.unmodifiableList(copiedRiseSets);
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
