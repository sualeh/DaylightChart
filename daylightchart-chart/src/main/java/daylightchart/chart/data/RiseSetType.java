/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.data;

enum RiseSetType {
  /** Normal day. */
  normal,
  /** Partial day - the sun never rises or never sets. */
  partial,
  /** Partial day due to a split. */
  split,
  /** All daylight, the sun never sets. */
  all_daylight,
  /** All night time, the sun never rises. */
  all_nighttime
}
