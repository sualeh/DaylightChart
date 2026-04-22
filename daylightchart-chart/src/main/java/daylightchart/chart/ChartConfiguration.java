/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/** One place to change certain configuration options. */
public final class ChartConfiguration {

  /** Default dimensions for the chart. */
  public static final Dimension chartDimension = new Dimension(770, 600);

  /** Default month format. */
  public static final DateFormat monthsFormat = new SimpleDateFormat("MMM");

  /** Default daylight color for the chart. */
  public static final Color daylightColor = new Color(0xFF, 0xFF, 0x60, 200);

  /** Default twilight color for the chart. */
  public static final Color twilightColor = new Color(0xFF, 0xFF, 0xFF, 60);

  /** Default night color for the chart. */
  public static final Color nightColor = new Color(74, 21, 81, 190);

  /** Default font for the chart. */
  public static final Font chartFont = new Font("Helvetica", Font.PLAIN, 12);

  private ChartConfiguration() {
    // Prevent instantiation
  }
}
