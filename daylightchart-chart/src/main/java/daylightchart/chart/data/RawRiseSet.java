/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.data;

import java.io.PrintWriter;
import java.io.Serial;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.LocalDate;
import org.geoname.data.Location;

/** Sunrise and sunset at a given location, and a given date. */
final class RawRiseSet implements Serializable, Comparable<RawRiseSet> {

  @Serial private static final long serialVersionUID = 3946758175409716163L;

  private final Location location;
  private final LocalDate date;
  private final boolean inDaylightSavings;
  private final double sunrise;
  private final double sunset;

  RawRiseSet(
      final Location location,
      final LocalDate date,
      final boolean inDaylightSavings,
      final double sunrise,
      final double sunset) {
    this.location = location;
    this.date = date;
    this.inDaylightSavings = inDaylightSavings;

    this.sunrise = sunrise;
    this.sunset = sunset;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final RawRiseSet o) {
    return date.compareTo(o.getDate());
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @SuppressWarnings("boxing")
  @Override
  public String toString() {
    if (location == null) {
      return "";
    }
    final StringWriter writer = new StringWriter();
    new PrintWriter(writer, true)
        .printf(
            "%s, %s: sunrise %6.3f sunset %6.4f", location.getDescription(), date, sunrise, sunset);
    return writer.toString();
  }

  /**
   * Date.
   *
   * @return Date
   */
  LocalDate getDate() {
    return date;
  }

  /**
   * Location.
   *
   * @return Location
   */
  Location getLocation() {
    return location;
  }

  /**
   * Sunrise time.
   *
   * @return Sunrise time
   */
  double getSunrise() {
    return sunrise;
  }

  /**
   * Sunset time.
   *
   * @return Sunset time
   */
  double getSunset() {
    return sunset;
  }

  boolean isInDaylightSavings() {
    return inDaylightSavings;
  }
}
