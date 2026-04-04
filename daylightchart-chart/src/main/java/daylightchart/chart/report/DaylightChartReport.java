/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.report;

import daylightchart.chart.DaylightChart;
import daylightchart.chart.data.RiseSetUtility;
import daylightchart.chart.data.RiseSetYearData;
import daylightchart.chart.options.ChartOptions;
import daylightchart.chart.options.ChartOptionsService;
import daylightchart.options.Options;
import daylightchart.options.persistence.FileType;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoname.data.Location;
import org.jfree.chart.ChartUtils;

/** The Daylight Chart report that can be written to a file. */
public class DaylightChartReport {

  private static final Logger LOGGER = Logger.getLogger(DaylightChartReport.class.getName());

  private static final ChartOptionsService CHART_OPTIONS_SERVICE = new ChartOptionsService();

  private final Location location;
  private final DaylightChart chart;

  /**
   * Constructor.
   *
   * @param location Location for the report.
   * @param options Report options.
   * @param chartOptions Chart options.
   */
  public DaylightChartReport(
      final Location location, final Options options, final ChartOptions chartOptions) {
    this.location = location;
    final Options effectiveOptions = options == null ? new Options() : options;
    final ChartOptions effectiveChartOptions =
        chartOptions == null ? CHART_OPTIONS_SERVICE.createDefaultChartOptions() : chartOptions;

    final int year = Year.now().getValue();
    final RiseSetYearData riseSetData =
        RiseSetUtility.createRiseSetYear(location, year, effectiveOptions);
    chart = new DaylightChart(riseSetData, effectiveOptions, effectiveChartOptions);
  }

  /**
   * Daylight Chart chart.
   *
   * @return Chart
   */
  public DaylightChart getChart() {
    return chart;
  }

  /**
   * The location for the chart.
   *
   * @return Location
   */
  public Location getLocation() {
    return location;
  }

  /**
   * Filename for the generated report.
   *
   * @param chartFileType Type of chart file.
   * @return Report file name
   */
  public String getReportFileName(final FileType chartFileType) {
    final String timeStamp =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MMM_dd_hh_mm_ss"));
    String locationDescription = "";
    try {
      locationDescription = new String(location.getDescription().getBytes("ASCII"), "ASCII");
      locationDescription = locationDescription.replace('?', '~');
      locationDescription = locationDescription.replace(", ", "_");
      locationDescription = locationDescription.replace(' ', '_');
    } catch (final UnsupportedEncodingException e) {
      locationDescription = "";
    }
    return locationDescription + "_" + timeStamp + chartFileType.getFileExtension();
  }

  /**
   * Write the Daylight Chart report to a file.
   *
   * @param file File to write to.
   * @param chartFileType Type of chart file.
   */
  public void write(final Path file, final ChartFileType chartFileType) {
    if (chartFileType == null) {
      LOGGER.warning("Cannot write report file, since no chart file type was specified");
      return;
    }
    if (file == null) {
      LOGGER.warning("Cannot write report file, since no file was specified");
      return;
    }

    try {
      final File chartFile = file.toAbsolutePath().toFile();
      switch (chartFileType) {
        case png -> ChartUtils.saveChartAsPNG(chartFile, chart, 842, 595);
        case jpg -> ChartUtils.saveChartAsJPEG(chartFile, chart, 842, 595);
        default -> throw new IllegalArgumentException("Unknown chart file type");
      }
    } catch (final IOException e) {
      LOGGER.log(Level.WARNING, "Error generating a report of type " + chartFileType, e);
    }
  }
}
