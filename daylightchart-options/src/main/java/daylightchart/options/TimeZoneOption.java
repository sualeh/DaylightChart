/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options;

/** Time option for charting. */
public enum TimeZoneOption {

  /** Use local time for the time - do not correct for time zone, and DST */
  USE_LOCAL_TIME("Use local time"),
  /** Use time zone for the time - correct for time zone, and DST */
  USE_TIME_ZONE("Use time zone");

  private final String description;

  private TimeZoneOption(final String description) {
    this.description = description;
  }

  /**
   * Description.
   *
   * @return Description
   */
  public String getDescription() {
    return description;
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
