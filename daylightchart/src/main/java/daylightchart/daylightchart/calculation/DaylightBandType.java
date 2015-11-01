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


/**
 * Band type.
 *
 * @author sfatehi
 */
public enum DaylightBandType
{

 /** With clock shift. */
  with_clock_shift("With clock shift", true),
 /** Without clock shift. */
  without_clock_shift("Without clock shift", false),
 /** TwilightType. */
  twilight("TwilightType", true);

  private final boolean adjustedForDaylightSavings;
  private final String description;

  private DaylightBandType(final String description,
                           final boolean adjustedForDaylightSavings)
  {
    this.description = description;
    this.adjustedForDaylightSavings = adjustedForDaylightSavings;
  }

  /**
   * Whether the times should be adjusted for daylight savings time.
   *
   * @return Whether the times should be adjusted for daylight savings
   *         time
   */
  public boolean isAdjustedForDaylightSavings()
  {
    return adjustedForDaylightSavings;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString()
  {
    return description;
  }

}
