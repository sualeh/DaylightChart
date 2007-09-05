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
package daylightchart.daylightchart.calculation;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDate;

/**
 * One daylight band, consisting of a sunrise series and a sunset
 * series.
 * 
 * @author sfatehi
 */
public final class DaylightBand
{

  private final DaylightBandType bandType;
  private final int bandNumber;
  private final List<RiseSet> riseSets;

  /**
   * Create a new daylight band.
   * 
   * @param name
   *        Name of the band.
   */
  DaylightBand(final DaylightBandType bandType, final int bandNumber)
  {
    this.bandType = bandType;
    this.bandNumber = bandNumber;
    riseSets = new ArrayList<RiseSet>();
  }

  /**
   * Type of the band.
   * 
   * @return Type
   */
  public DaylightBandType getDaylightBandType()
  {
    return bandType;
  }

  /**
   * Name of the band.
   * 
   * @return Name
   */
  public String getName()
  {
    return bandType + ", #" + bandNumber;
  }

  /**
   * Get all the rise/ sets in the band
   * 
   * @return Rise/ sets list
   */
  public List<RiseSet> getRiseSets()
  {
    return Collections.unmodifiableList(riseSets);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    final String name = getName();
    final LocalDate startDate = riseSets.get(0).getDate();
    final LocalDate endDate = riseSets.get(riseSets.size() - 1).getDate();

    final StringWriter writer = new StringWriter();
    new PrintWriter(writer, true).printf("%s, starting %s ending %s",
                                         name,
                                         startDate,
                                         endDate);
    return writer.toString();
  }

  /**
   * Add a sunrise and sunset point to the band.
   * 
   * @param riseSet
   *        Sunrise and sunset time
   */
  void add(final RiseSet riseSet)
  {
    if (riseSet != null && riseSet.getSunrise().isBefore(riseSet.getSunset())
        && riseSet.getRiseSetType() != RiseSetType.all_nighttime)
    {
      riseSets.add(riseSet);
    }
  }

}
