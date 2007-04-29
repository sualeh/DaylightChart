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


import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import daylightchart.UserPreferences;
import daylightchart.location.Location;
import daylightchart.location.parser.LocationFormatter;
import daylightchart.location.parser.LocationsLoader;

/**
 * GUI helper methods.
 * 
 * @author sfatehi
 */
public class GuiHelper
{

  private static final Logger LOGGER = Logger.getLogger(GuiHelper.class
    .getName());

  /**
   * Loads locations from a file.
   * 
   * @param mainWindow
   *        Main GUI window.
   */
  public static void loadLocations(final DaylightChartGui mainWindow)
  {

    final JFileChooser fileDialog = new JFileChooser();
    fileDialog.setDialogTitle(Messages
      .getString("DaylightChartGui.Menu.File.LoadLocations")); //$NON-NLS-1$
    fileDialog.setSelectedFile(new File(new UserPreferences()
      .getDataFileDirectory(), "locations.data"));
    fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);
    fileDialog.showDialog(mainWindow, Messages
      .getString("DaylightChartGui.Menu.Open")); //$NON-NLS-1$
    final File selectedFile = fileDialog.getSelectedFile();
    if (selectedFile == null)
    {
      return;
    }

    if (!selectedFile.exists() || !selectedFile.canRead())
    {
      JOptionPane
        .showMessageDialog(mainWindow,
                           selectedFile
                               + "\n" //$NON-NLS-1$
                               + Messages
                                 .getString("DaylightChartGui.Error.CannotReadFile")); //$NON-NLS-1$
      return;
    }

    mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    try
    {
      final List<Location> locationsList = LocationsLoader.load(selectedFile);
      if (locationsList == null)
      {
        LOGGER.log(Level.WARNING, Messages
          .getString("DaylightChartGui.Error.ReadFile")); //$NON-NLS-1$
        JOptionPane
          .showMessageDialog(mainWindow,
                             selectedFile
                                 + "\n" //$NON-NLS-1$
                                 + Messages
                                   .getString("DaylightChartGui.Error.CannotReadFile")); //$NON-NLS-1$
        return;
      }
      else
      {
        mainWindow.setLocations(locationsList);
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not load locations");
    }

    new UserPreferences().setDataFileDirectory(selectedFile.getParentFile());

    mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

  }

  /**
   * Saves the locations to a file.
   * 
   * @param mainWindow
   *        Main GUI window.
   */
  public static void saveLocations(final DaylightChartGui mainWindow)
  {
    final File selectedFile = showSaveDialog(Messages
                                               .getString("DaylightChartGui.Menu.File.SaveLocations"), "locations.data", mainWindow); //$NON-NLS-1$ //$NON-NLS-2$
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
   * @param mainWindow
   *        Main GUI window.
   * @return Selected file.
   */
  public static File showSaveDialog(final String dialogTitle,
                                    final String suggestedFilename,
                                    final DaylightChartGui mainWindow)
  {
    final JFileChooser fileDialog = new JFileChooser();
    fileDialog.setDialogTitle(dialogTitle);
    fileDialog.setSelectedFile(new File(new UserPreferences()
      .getDataFileDirectory(), suggestedFilename));
    fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
    fileDialog.showDialog(mainWindow, Messages
      .getString("DaylightChartGui.Menu.File.SaveFile")); //$NON-NLS-1$
    File selectedFile = fileDialog.getSelectedFile();
    if (selectedFile != null)
    {
      if (selectedFile.exists())
      {
        final int confirm = JOptionPane
          .showConfirmDialog(mainWindow,
                             selectedFile
                                 + "\n" //$NON-NLS-1$ 
                                 + Messages
                                   .getString("DaylightChartGui.ConfirmOverwrite"), //$NON-NLS-1$
                             Messages
                               .getString("DaylightChartGui.Menu.File.SaveFile"), //$NON-NLS-1$
                             JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
        {
          selectedFile = null;
        }
      }
      else
      {
        try
        {
          selectedFile.createNewFile();
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, Messages
            .getString("DaylightChartGui.Error.SaveFile"), e); //$NON-NLS-1$
          selectedFile = null;
        }
      }
      if (selectedFile == null || !selectedFile.canWrite())
      {
        JOptionPane.showMessageDialog(mainWindow, Messages.getString(Messages
          .getString("DaylightChartGui.Error.CannotSaveFile"))); //$NON-NLS-1$
      }
      if (selectedFile != null)
      {
        selectedFile.delete();
      }
    }
    if (selectedFile != null)
    {
      // Save last selected directory
      new UserPreferences().setDataFileDirectory(selectedFile.getParentFile());
    }

    return selectedFile;
  }

  private GuiHelper()
  {
    // Prevent instantiation
  }

}
