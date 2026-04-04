/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.data;

/** Band type. */
public enum DaylightBandType {

  /** With clock shift. */
  with_clock_shift("With clock shift", true),
  /** Without clock shift. */
  without_clock_shift("Without clock shift", false),
  /** TwilightType. */
  twilight("TwilightType", true);

  private final boolean adjustedForDaylightSavings;
  private final String description;

  DaylightBandType(final String description, final boolean adjustedForDaylightSavings) {
    this.description = description;
    this.adjustedForDaylightSavings = adjustedForDaylightSavings;
  }

  /**
   * Whether the times should be adjusted for daylight savings time.
   *
   * @return Whether the times should be adjusted for daylight savings time
   */
  public boolean isAdjustedForDaylightSavings() {
    return adjustedForDaylightSavings;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return description;
  }
}
