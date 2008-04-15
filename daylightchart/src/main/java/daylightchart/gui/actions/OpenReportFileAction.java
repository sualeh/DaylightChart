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
package daylightchart.gui.actions;


import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import sf.util.ui.Actions;
import sf.util.ui.ExtensionFileFilter;
import sf.util.ui.GuiAction;
import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.Messages;
import daylightchart.options.UserPreferences;

/**
 * Opens a report file.
 * 
 * @author sfatehi
 */
public final class OpenReportFileAction
  extends GuiAction
{

  private static final class GuiActionListener
    implements ActionListener
  {
    private final DaylightChartGui mainWindow;

    private GuiActionListener(final DaylightChartGui mainWindow)
    {
      this.mainWindow = mainWindow;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(@SuppressWarnings("unused")
    final ActionEvent actionevent)
    {
      final List<FileFilter> fileFilters = new ArrayList<FileFilter>();
      fileFilters.add(new ExtensionFileFilter("Report design files", ".jrxml"));
      final File selectedFile = Actions
        .showOpenDialog(mainWindow,
                        Messages
                          .getString("DaylightChartGui.Menu.File.LoadReport"),
                        fileFilters,
                        new File(UserPreferences.getOptions()
                          .getWorkingDirectory(), "DaylightChartReport.jrxml"),
                        Messages
                          .getString("DaylightChartGui.Message.Error.CannotOpenFile"));

      mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      try
      {
        boolean reportLoaded = UserPreferences.importReport(selectedFile);
        if (!reportLoaded)
        {
          if (selectedFile != null)
          {
            LOGGER.log(Level.WARNING, Messages
              .getString("DaylightChartGui.Message.Error.CannotOpenFile")); //$NON-NLS-1$
            JOptionPane
              .showMessageDialog(mainWindow,
                                 selectedFile
                                     + "\n" //$NON-NLS-1$
                                     + Messages
                                       .getString("DaylightChartGui.Message.Error.CannotOpenFile")); //$NON-NLS-1$
          }
        }
      }
      catch (final RuntimeException e)
      {
        // We catch exceptions, because otherwise the cursor may get
        // stuck in busy mode
        LOGGER.log(Level.WARNING, "Could not load report");
      }

      mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  private static final long serialVersionUID = -177214864870181893L;

  private static final Logger LOGGER = Logger
    .getLogger(OpenReportFileAction.class.getName());

  /**
   * Shows an open dialog to open a locations file.
   * 
   * @param mainWindow
   *        Main Daylight Chart window
   */
  public OpenReportFileAction(final DaylightChartGui mainWindow)
  {

    super(Messages.getString("DaylightChartGui.Menu.File.LoadReport"),//$NON-NLS-1$
          "/icons/import.gif" //$NON-NLS-1$
    );
    addActionListener(new GuiActionListener(mainWindow));
  }

}
