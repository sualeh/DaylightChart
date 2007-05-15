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


import java.awt.Component;
import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;

import daylightchart.location.Location;
import daylightchart.location.parser.LocationFormatter;
import daylightchart.location.parser.LocationsLoader;
import daylightchart.options.UserPreferences;

/**
 * GUI helper methods.
 * 
 * @author sfatehi
 */
public class Actions
{

  private static final Logger LOGGER = Logger
    .getLogger(Actions.class.getName());

  /**
   * Loads locations from a file.
   * 
   * @param mainWindow
   *        Main GUI window.
   */
  public static void doOpenLocationsFileAction(final DaylightChartGui mainWindow)
  {

    final JFileChooser fileDialog = new JFileChooser();
    fileDialog.setDialogTitle(Messages
      .getString("DaylightChartGui.Menu.File.LoadLocations")); //$NON-NLS-1$
    fileDialog.setSelectedFile(new File(UserPreferences.getDataFileDirectory(),
                                        "locations.data"));
    fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);
    final int dialogReturnValue = fileDialog.showDialog(mainWindow, Messages
      .getString("DaylightChartGui.Menu.Open")); //$NON-NLS-1$

    if (dialogReturnValue != JFileChooser.APPROVE_OPTION)
    {
      return;
    }

    final File selectedFile = fileDialog.getSelectedFile();
    if (selectedFile == null || !selectedFile.exists() ||
        !selectedFile.canRead())
    {
      JOptionPane
        .showMessageDialog(mainWindow,
                           selectedFile +
                               "\n" //$NON-NLS-1$
                               +
                               Messages
                                 .getString("DaylightChartGui.Error.DidNotReadFile")); //$NON-NLS-1$
      return;
    }

    mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    try
    {
      final List<Location> locationsList = LocationsLoader.load(selectedFile);
      if (locationsList == null || locationsList.size() == 0)
      {
        LOGGER.log(Level.WARNING, Messages
          .getString("DaylightChartGui.Error.ReadFile")); //$NON-NLS-1$
        JOptionPane
          .showMessageDialog(mainWindow,
                             selectedFile +
                                 "\n" //$NON-NLS-1$
                                 +
                                 Messages
                                   .getString("DaylightChartGui.Error.DidNotReadFile")); //$NON-NLS-1$
      }
      else
      {
        mainWindow.setLocations(locationsList);
        UserPreferences.setDataFileDirectory(selectedFile.getParentFile());
      }
    }
    catch (final RuntimeException e)
    {
      // We catch exceptions, because otherwise the cursor may get stuck
      // in busy mode
      LOGGER.log(Level.WARNING, "Could not load locations");
    }

    mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

  }

  /**
   * Saves the chart to a file.
   * 
   * @param chartPanel
   *        Chart panel.
   */
  public static void doPrintChartAction(final ChartPanel chartPanel)
  {
    chartPanel.createChartPrintJob();
  }

  /**
   * Saves the chart to a file.
   * 
   * @param chartPanel
   *        Chart panel.
   */
  public static void doSaveChartAction(final ChartPanel chartPanel)
  {

    final List<FileFilter> fileFilters = new ArrayList<FileFilter>();
    fileFilters
      .add(new ExtensionFileFilter("Portable Network Graphics (*.png)", ".png"));
    fileFilters.add(new ExtensionFileFilter("JPEG (*.jpg)", ".jpg"));
    final File selectedFile = showSaveDialog(Messages
      .getString("DaylightChartGui.Menu.File.SaveChart"), //$NON-NLS-1$
                                             "chart",
                                             fileFilters,
                                             chartPanel);
    if (selectedFile != null)
    {
      try
      {
        final String extension = ExtensionFileFilter.getExtension(selectedFile);
        if (extension.equals(".png"))
        {
          ChartUtilities.saveChartAsPNG(selectedFile,
                                        chartPanel.getChart(),
                                        chartPanel.getWidth(),
                                        chartPanel.getHeight());
        }
        else if (extension.equals(".jpg"))
        {
          ChartUtilities.saveChartAsJPEG(selectedFile,
                                         chartPanel.getChart(),
                                         chartPanel.getWidth(),
                                         chartPanel.getHeight());
        }
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, Messages
          .getString("DaylightChartGui.Error.SaveChart"), e); //$NON-NLS-1$
        JOptionPane.showMessageDialog(chartPanel, Messages
          .getString("DaylightChartGui.Error.CannotSaveFile") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                                                  + selectedFile, Messages
          .getString("DaylightChartGui.Menu.File.SaveChart"), //$NON-NLS-1$
                                      JOptionPane.OK_OPTION);
      }
    }
  }

  /**
   * Saves the locations to a file.
   * 
   * @param mainWindow
   *        Main GUI window.
   */
  public static void doSaveLocationsFileAction(final DaylightChartGui mainWindow)
  {

    final List<FileFilter> fileFilters = new ArrayList<FileFilter>();
    fileFilters.add(new ExtensionFileFilter("Data files", ".data"));
    fileFilters.add(new ExtensionFileFilter("Text files", ".txt"));
    final File selectedFile = showSaveDialog(Messages
      .getString("DaylightChartGui.Menu.File.SaveLocations"), //$NON-NLS-1$
                                             "locations.data",
                                             fileFilters,
                                             mainWindow);
    if (selectedFile != null)
    {
      try
      {
        LocationFormatter.formatLocations(mainWindow.getLocations(),
                                          selectedFile);
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.WARNING, Messages
          .getString("DaylightChartGui.Error.SaveFile"), e); //$NON-NLS-1$
        JOptionPane.showMessageDialog(mainWindow, Messages
          .getString("DaylightChartGui.Error.CannotSaveFile") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                                                  + selectedFile, Messages
          .getString("DaylightChartGui.Menu.File.SaveFile"), //$NON-NLS-1$
                                      JOptionPane.OK_OPTION);
      }
    }
  }

  /**
   * Shows the save dialog.
   * 
   * @param dialogTitle
   *        Dialog title.
   * @param suggestedFilename
   *        Suggested file name.
   * @param parent
   *        Main GUI window.
   * @return Selected file.
   */
  private static File showSaveDialog(final String dialogTitle,
                                     final String suggestedFilename,
                                     final List<FileFilter> fileFilters,
                                     final Component parent)
  {
    // Create the save dialog
    final JFileChooser fileDialog = new JFileChooser();
    fileDialog.setDialogTitle(dialogTitle);
    fileDialog.setSelectedFile(new File(UserPreferences.getDataFileDirectory(),
                                        suggestedFilename));
    fileDialog.setAcceptAllFileFilterUsed(false);
    for (final FileFilter fileFilter: fileFilters)
    {
      fileDialog.addChoosableFileFilter(fileFilter);
    }
    fileDialog.showSaveDialog(parent);

    File selectedFile = fileDialog.getSelectedFile();
    if (selectedFile != null)
    {
      // Add extension, if it is not provided
      final FileFilter fileFilter = fileDialog.getFileFilter();
      if (fileFilter instanceof ExtensionFileFilter)
      {
        final ExtensionFileFilter extFileFilter = (ExtensionFileFilter) fileFilter;
        final String selectedExtension = extFileFilter.getExtension();
        if (!ExtensionFileFilter.getExtension(selectedFile)
          .equals(selectedExtension))
        {
          selectedFile = new File(selectedFile.getAbsoluteFile() +
                                  selectedExtension);
        }
      }

      // Save last selected directory
      UserPreferences.setDataFileDirectory(selectedFile.getParentFile());

      if (selectedFile.exists())
      {
        final int confirm = JOptionPane
          .showConfirmDialog(parent,
                             selectedFile +
                                 "\n" //$NON-NLS-1$ 
                                 +
                                 Messages
                                   .getString("DaylightChartGui.ConfirmOverwrite"), //$NON-NLS-1$
                             dialogTitle,
                             JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
        {
          selectedFile = null;
        }
      }
    }
    return selectedFile;
  }

  private Actions()
  {
    // Prevent instantiation
  }

}
