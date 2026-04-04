/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.options;

/** Allows a chart to act on chart options being set. */
public interface ChartOptionsListener {

  /**
   * Called after setting chart options.
   *
   * @param chartOptions Chart options
   */
  void afterSettingChartOptions(ChartOptions chartOptions);

  /**
   * Called before setting chart options.
   *
   * @param chartOptions Chart options
   */
  void beforeSettingChartOptions(ChartOptions chartOptions);
}
