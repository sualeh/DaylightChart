/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.timezones;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public final class TimeZoneDisplay implements Serializable, Comparable<TimeZoneDisplay> {

  @Serial private static final long serialVersionUID = 3841508979907339562L;

  private final String timeZoneId;
  private final String timeZoneDisplayName;
  private final String description;

  public TimeZoneDisplay(final ZoneId zoneId) {
    if (zoneId == null) {
      throw new IllegalArgumentException("Cannot use null time zone");
    }
    timeZoneId = zoneId.getId();
    timeZoneDisplayName = zoneId.getDisplayName(TextStyle.FULL, Locale.getDefault());
    description = createDescription();
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final TimeZoneDisplay other) {
    int compareTo = 0;
    if (compareTo == 0) {
      compareTo = timeZoneDisplayName.compareTo(other.timeZoneDisplayName);
    }
    if (compareTo == 0) {
      compareTo = timeZoneId.compareTo(other.timeZoneId);
    }
    return compareTo;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final TimeZoneDisplay other = (TimeZoneDisplay) obj;
    if (timeZoneDisplayName == null) {
      if (other.timeZoneDisplayName != null) {
        return false;
      }
    } else if (!timeZoneDisplayName.equals(other.timeZoneDisplayName)) {
      return false;
    }
    if (timeZoneId == null) {
      if (other.timeZoneId != null) {
        return false;
      }
    } else if (!timeZoneId.equals(other.timeZoneId)) {
      return false;
    }
    return true;
  }

  /**
   * Time zone display name.
   *
   * @return Time zone display name
   */
  public String getTimeZoneDisplayName() {
    return timeZoneDisplayName;
  }

  /**
   * Time zone id.
   *
   * @return Time zone id
   */
  public String getTimeZoneId() {
    return timeZoneId;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (timeZoneDisplayName == null ? 0 : timeZoneDisplayName.hashCode());
    result = prime * result + (timeZoneId == null ? 0 : timeZoneId.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return description;
  }

  private String createDescription() {
    String string = timeZoneDisplayName;
    if (string.contains("(")) {
      string = string.substring(0, string.indexOf('('));
      string = string.trim();
    }
    string = string + " (" + timeZoneId + ")";
    string = StringUtils.abbreviate(string, 50);
    return string;
  }
}
