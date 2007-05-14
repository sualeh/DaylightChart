/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
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


import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.ui.ExtensionFileFilter;

import daylightchart.chart.DaylightChart;
import daylightchart.location.Location;
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
   * @param location
   *        Location
   */
  public void addLocationTab(final Location location)
  {
    final Options options = UserPreferences.getOptions();
    final DaylightChart chart = new DaylightChart(location, options
      .getTimeZoneOption());
    options.getChartOptions().updateChart(chart);

    final ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setBackground(Color.WHITE);
    chartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    chartPanel.setPreferredSize(new Dimension(700, 495));

    final String tabTitle = location.toString();
    final String toolTip = "<b>" + tabTitle + "</b><br>"
                           + location.getDetails();

    addTab(tabTitle, new CloseTabIcon(), chartPanel, toolTip);
  }

  /**
   * Prints the selected chart.
   */
  public void printSelectedChart()
  {
    getSelectedChart().createChartPrintJob();
  }

  /**
   * Saves the selected chart.
   */
  public void saveSelectedChart()
  {
    try
    {

      ChartPanel chartPanel = getSelectedChart();
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(UserPreferences.getDataFileDirectory());
      fileChooser.setAcceptAllFileFilterUsed(false);      
      fileChooser
        .addChoosableFileFilter(new ExtensionFileFilter("Portable Network Graphics (*.png)",
                                                        ".png"));
      fileChooser
        .addChoosableFileFilter(new ExtensionFileFilter("JPEG (*.jpg)", ".jpg"));

      int option = fileChooser.showSaveDialog(chartPanel);
      if (option == JFileChooser.APPROVE_OPTION)
      {
        fileChooser.getFileFilter();
        String filename = fileChooser.getSelectedFile().getPath();
        if (chartPanel.isEnforceFileExtensions())
        {
          if (!filename.endsWith(".png"))
          {
            filename = filename + ".png";
          }
        }
        ChartUtilities.saveChartAsPNG(new File(filename),
                                      chartPanel.getChart(),
                                      chartPanel.getWidth(),
                                      chartPanel.getHeight());
      }
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, Messages
        .getString("DaylightChartGui.Error.SaveChart"), e); //$NON-NLS-1$
    }
  }

  private ChartPanel getSelectedChart()
  {
    return (ChartPanel) getSelectedComponent();
  }

}
