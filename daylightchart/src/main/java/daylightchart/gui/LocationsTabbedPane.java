/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package daylightchart.gui;


import java.util.logging.Logger;

import javax.swing.JTabbedPane;

import org.jfree.chart.ChartPanel;

import sf.util.ui.CloseTabIcon;
import daylightchart.daylightchart.chart.ChartConfiguration;
import daylightchart.daylightchart.chart.DaylightChart;
import daylightchart.daylightchart.layout.DaylightChartReport;
import daylightchart.location.Location;
import daylightchart.location.parser.LocationFormatter;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;

/**
 * Tabbed pane for location charts.
 * 
 * @author sfatehi
 */
public class LocationsTabbedPane
  extends JTabbedPane
{

  private static final long serialVersionUID = -2086804705336786590L;

  private static final Logger LOGGER = Logger
    .getLogger(LocationsTabbedPane.class.getName());

  /**
   * Add a new tab for the location.
   * 
   * @param daylightChartReport
   *        Daylight Chart report
   */
  public void addLocationTab(final DaylightChartReport daylightChartReport)
  {
    final Options options = UserPreferences.getOptions();
    final Location location = daylightChartReport.getLocation();
    final DaylightChart chart = daylightChartReport.getChart();
    options.getChartOptions().updateChart(chart);

    final ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setName(location.toString());
    chartPanel.setPreferredSize(ChartConfiguration.chartDimension);

    addTab(location.toString(),
           new CloseTabIcon(),
           chartPanel,
           LocationFormatter.getToolTip(location));
    setSelectedIndex(getTabCount() - 1);
  }

  /**
   * Prints the selected chart.
   */
  public void printSelectedChart()
  {
    getSelectedChart().createChartPrintJob();
  }

  private ChartPanel getSelectedChart()
  {
    return (ChartPanel) getSelectedComponent();
  }

}
