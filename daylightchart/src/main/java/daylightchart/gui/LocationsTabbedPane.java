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


import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;

import sf.util.ui.Actions;
import sf.util.ui.CloseTabIcon;
import sf.util.ui.ExtensionFileFilter;

import daylightchart.chart.DaylightChart;
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
   * @param location
   *        Location
   */
  public void addLocationTab(final Location location)
  {
    final Options options = UserPreferences.getOptions();
    final DaylightChart chart = new DaylightChart(location, Calendar
      .getInstance().get(Calendar.YEAR), options);
    options.getChartOptions().updateChart(chart);

    final ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setName(location.toString());
    chartPanel.setPreferredSize(new Dimension(700, 525));

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

  /**
   * Saves the selected chart.
   */
  public void saveSelectedChart()
  {
    final ChartPanel chartPanel = getSelectedChart();
    final List<FileFilter> fileFilters = new ArrayList<FileFilter>();
    fileFilters
      .add(new ExtensionFileFilter("Portable Network Graphics (*.png)", ".png")); //$NON-NLS-1$ //$NON-NLS-2$
    fileFilters.add(new ExtensionFileFilter("JPEG (*.jpg)", ".jpg")); //$NON-NLS-1$ //$NON-NLS-2$
    final File selectedFile = Actions
      .showSaveDialog(chartPanel, Messages
        .getString("DaylightChartGui.Menu.File.SaveChart"), //$NON-NLS-1$
                      fileFilters,
                      new File(UserPreferences.getDataFileDirectory(),
                               chartPanel.getName()),
                      Messages
                        .getString("DaylightChartGui.Message.Confirm.FileOverwrite")); //$NON-NLS-1$
    if (selectedFile != null)
    {
      try
      {
        final String extension = ExtensionFileFilter.getExtension(selectedFile);
        if (extension.equals(".png")) //$NON-NLS-1$
        {
          ChartUtilities.saveChartAsPNG(selectedFile,
                                        chartPanel.getChart(),
                                        chartPanel.getWidth(),
                                        chartPanel.getHeight());
        }
        else if (extension.equals(".jpg")) //$NON-NLS-1$
        {
          ChartUtilities.saveChartAsJPEG(selectedFile,
                                         chartPanel.getChart(),
                                         chartPanel.getWidth(),
                                         chartPanel.getHeight());
        }

        // Save last selected directory
        UserPreferences.setDataFileDirectory(selectedFile.getParentFile());
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, Messages
          .getString("DaylightChartGui.Message.Error.CannotSaveFile"), e); //$NON-NLS-1$
        JOptionPane.showMessageDialog(chartPanel, Messages
          .getString("DaylightChartGui.Message.Error.CannotSaveFile") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                                                  + selectedFile, Messages
          .getString("DaylightChartGui.Message.Error.CannotSaveFile"), //$NON-NLS-1$
                                      JOptionPane.OK_OPTION);
      }
    }
  }

  private ChartPanel getSelectedChart()
  {
    return (ChartPanel) getSelectedComponent();
  }

}
