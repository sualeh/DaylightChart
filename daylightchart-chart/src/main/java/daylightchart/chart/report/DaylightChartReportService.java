/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.report;

import daylightchart.chart.options.ChartOptions;
import daylightchart.options.Options;
import org.geoname.data.Location;

/** Service facade for chart report creation and export. */
public class DaylightChartReportService {

  private static final DaylightChartReportService DAYLIGHT_CHART_REPORT_SERVICE =
      new DaylightChartReportService();

  public static DaylightChartReportService reports() {
    return DAYLIGHT_CHART_REPORT_SERVICE;
  }

  public DaylightChartReport createReport(
      final Location location, final Options options, final ChartOptions chartOptions) {
    return new DaylightChartReport(location, options, chartOptions);
  }
}
