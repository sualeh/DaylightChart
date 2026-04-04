/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui;

import daylightchart.chart.ChartConfiguration;
import daylightchart.chart.DaylightChart;
import daylightchart.chart.report.DaylightChartReport;
import daylightchart.gui.util.CloseTabIcon;
import java.io.Serial;
import javax.swing.JTabbedPane;
import org.geoname.data.Location;
import org.geoname.parser.LocationFormatter;
import org.jfree.chart.ChartPanel;

/** Tabbed pane for location charts. */
public class LocationsTabbedPane extends JTabbedPane {

  @Serial private static final long serialVersionUID = -2086804705336786590L;

  /**
   * Add a new tab for the location.
   *
   * @param daylightChartReport Daylight Chart report
   */
  public void addLocationTab(final DaylightChartReport daylightChartReport) {
    final Location location = daylightChartReport.getLocation();
    final DaylightChart chart = daylightChartReport.getChart();

    final ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setName(location.toString());
    chartPanel.setPreferredSize(ChartConfiguration.chartDimension);

    addTab(
        location.toString(),
        new CloseTabIcon(),
        chartPanel,
        LocationFormatter.getToolTip(location));
    setSelectedIndex(getTabCount() - 1);
  }

  /** Prints the selected chart. */
  public void printSelectedChart() {
    getSelectedChart().createChartPrintJob();
  }

  private ChartPanel getSelectedChart() {
    return (ChartPanel) getSelectedComponent();
  }
}
