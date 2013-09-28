/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2013, Sualeh Fatehi.
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

import net.sf.jasperreports.engine.JasperReport;
import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.Messages;
import daylightchart.gui.util.Actions;
import daylightchart.gui.util.ExtensionFileFilter;
import daylightchart.gui.util.GuiAction;
import daylightchart.gui.util.SelectedFile;
import daylightchart.options.ReportDataFile;
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
    public void actionPerformed(final ActionEvent actionevent)
    {
      final List<ExtensionFileFilter<ReportDesignFileType>> fileFilters = new ArrayList<ExtensionFileFilter<ReportDesignFileType>>();
      fileFilters
        .add(new ExtensionFileFilter<ReportDesignFileType>(ReportDesignFileType.report_design));
      final SelectedFile<ReportDesignFileType> selectedFile = Actions
        .showOpenDialog(mainWindow,
                        Messages
                          .getString("DaylightChartGui.Menu.File.LoadReport"),
                        fileFilters,
                        new File(UserPreferences.optionsFile().getData()
                          .getWorkingDirectory(), "DaylightChartReport.jasper"),
                        Messages
                          .getString("DaylightChartGui.Message.Error.CannotOpenFile"));

      if (selectedFile.isSelected())
      {
        mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try
        {
          boolean reportLoaded = importReport(selectedFile.getFile());
          if (!reportLoaded)
          {
            LOGGER.log(Level.WARNING, Messages
              .getString("DaylightChartGui.Message.Error.CannotOpenFile")); //$NON-NLS-1$
            JOptionPane.showMessageDialog(mainWindow, Messages
              .getString("DaylightChartGui.Message.Error.CannotOpenFile") //$NON-NLS-1$
                                                      + "\n" //$NON-NLS-1$
                                                      + selectedFile, Messages
              .getString("DaylightChartGui.Message.Error.CannotOpenFile"), //$NON-NLS-1$
                                          JOptionPane.ERROR_MESSAGE);
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

    /**
     * Import a report file.
     * 
     * @param reportFile
     *        Report file
     * @return Whether the file could be imported
     */
    private boolean importReport(final File reportFile)
    {
      boolean imported = false;
      ReportDataFile importedReportFile = new ReportDataFile(reportFile,
                                                             ReportDesignFileType.report_design);
      importedReportFile.load();
      JasperReport report = importedReportFile.getData();
      if (report != null)
      {
        UserPreferences.reportFile().save(report);
        imported = true;
      }
      return imported;
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
