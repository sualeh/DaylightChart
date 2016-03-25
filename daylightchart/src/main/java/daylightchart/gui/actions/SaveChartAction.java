/*
 *
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2016, Sualeh Fatehi.
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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import daylightchart.daylightchart.layout.ChartFileType;
import daylightchart.daylightchart.layout.DaylightChartReport;
import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.Messages;
import daylightchart.gui.util.Actions;
import daylightchart.gui.util.ExtensionFileFilter;
import daylightchart.gui.util.GuiAction;
import daylightchart.gui.util.SelectedFile;
import daylightchart.options.UserPreferences;

/**
 * Saves locations into a file.
 *
 * @author sfatehi
 */
public final class SaveChartAction
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
    @Override
    public void actionPerformed(final ActionEvent actionevent)
    {
      final DaylightChartReport daylightChartReport = new DaylightChartReport(mainWindow
        .getSelectedLocation(), UserPreferences.optionsFile().getData());
      final List<ExtensionFileFilter<ChartFileType>> fileFilters = new ArrayList<ExtensionFileFilter<ChartFileType>>();
      for (final ChartFileType chartFileType: ChartFileType.values())
      {
        fileFilters.add(new ExtensionFileFilter<ChartFileType>(chartFileType));
      }
      final String reportFilename = daylightChartReport
        .getReportFileName(ChartFileType.png);
      final Path chartFile = Paths.get(
                                       UserPreferences.optionsFile().getData()
                                         .getWorkingDirectory().toString(),
                                       reportFilename);
      final SelectedFile<ChartFileType> selectedFile = Actions
        .showSaveDialog(mainWindow,
                        Messages
                          .getString("DaylightChartGui.Menu.File.SaveChart"),
                        fileFilters,
                        chartFile.toFile(),
                        Messages
                          .getString("DaylightChartGui.Message.Confirm.FileOverwrite")); //$NON-NLS-1$
      if (selectedFile.isSelected())
      {
        try
        {
          daylightChartReport.write(selectedFile.getFile(),
                                    selectedFile.getFileType());
        }
        catch (final Exception e)
        {
          LOGGER
            .log(Level.WARNING,
                 Messages
                   .getString("DaylightChartGui.Message.Error.CannotSaveFile"), //$NON-NLS-1$
                 e);
          JOptionPane
            .showMessageDialog(mainWindow,
                               Messages
                                 .getString("DaylightChartGui.Message.Error.CannotSaveFile") //$NON-NLS-1$
                                           + "\n" //$NON-NLS-1$
                                           + selectedFile,
                               Messages
                                 .getString("DaylightChartGui.Message.Error.CannotSaveFile"), //$NON-NLS-1$
                               JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  private static final long serialVersionUID = 1173685118494564955L;

  private static final Logger LOGGER = Logger
    .getLogger(SaveChartAction.class.getName());

  /**
   * Saves locations into a file.
   *
   * @param mainWindow
   *        Main window.
   */
  public SaveChartAction(final DaylightChartGui mainWindow)
  {

    super(Messages.getString("DaylightChartGui.Menu.File.SaveChart"), //$NON-NLS-1$
          "/icons/save.gif" //$NON-NLS-1$
    );
    setShortcutKey(KeyStroke.getKeyStroke("control S"));
    addActionListener(new GuiActionListener(mainWindow));
  }

}
