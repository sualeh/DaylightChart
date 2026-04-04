/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options;

/** Orientation of the chart. */
public enum ChartOrientation {

  /** Standard */
  STANDARD,
  /** Conventional */
  CONVENTIONAL,
  /** Vertical */
  VERTICAL;

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
  }
}
