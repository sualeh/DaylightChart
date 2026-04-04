/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.actions;

import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.Messages;
import daylightchart.gui.util.Actions;
import daylightchart.gui.util.ExtensionFileFilter;
import daylightchart.gui.util.GuiAction;
import daylightchart.gui.util.SelectedFile;
import daylightchart.options.persistence.LocationFileType;
import daylightchart.options.service.UserPreferencesService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/** Saves locations into a file. */
public final class SaveLocationsFileAction extends GuiAction {

  private static final class GuiActionListener implements ActionListener {
    private final DaylightChartGui mainWindow;

    private GuiActionListener(final DaylightChartGui mainWindow) {
      this.mainWindow = mainWindow;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent actionevent) {
      final List<ExtensionFileFilter<LocationFileType>> fileFilters =
          new ArrayList<ExtensionFileFilter<LocationFileType>>();
      fileFilters.add(new ExtensionFileFilter<LocationFileType>(LocationFileType.data));
      final SelectedFile<LocationFileType> selectedFile =
          Actions.showSaveDialog(
              mainWindow,
              Messages.getString("DaylightChartGui.Menu.File.SaveLocations"),
              fileFilters,
              new File(
                  UserPreferencesService.preferences().getWorkingDirectory().toString(),
                  "locations.data"),
              Messages.getString("DaylightChartGui.Message.Confirm.FileOverwrite")); // $NON-NLS-1$
      if (selectedFile.isSelected()) {
        try {
          UserPreferencesService.preferences()
              .saveLocations(selectedFile, mainWindow.getLocations());
        } catch (final Exception e) {
          LOGGER.log(
              Level.WARNING,
              Messages.getString("DaylightChartGui.Message.Error.CannotSaveFile"), // $NON-NLS-1$
              e);
          LOGGER.log(
              Level.WARNING,
              Messages.getString("DaylightChartGui.Message.Error.CannotSaveFile"), // $NON-NLS-1$
              e);
          JOptionPane.showMessageDialog(
              mainWindow,
              Messages.getString("DaylightChartGui.Message.Error.CannotSaveFile") // $NON-NLS-1$
                  + "\n" //$NON-NLS-1$
                  + selectedFile,
              Messages.getString("DaylightChartGui.Message.Error.CannotSaveFile"), // $NON-NLS-1$
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  @Serial private static final long serialVersionUID = 1173685118494564955L;

  private static final Logger LOGGER = Logger.getLogger(SaveLocationsFileAction.class.getName());

  /**
   * Saves locations into a file.
   *
   * @param mainWindow Main window.
   */
  public SaveLocationsFileAction(final DaylightChartGui mainWindow) {

    super(
        Messages.getString("DaylightChartGui.Menu.File.SaveLocations"), // $NON-NLS-1$
        "/icons/save_locations.gif" //$NON-NLS-1$
        );
    setShortcutKey(KeyStroke.getKeyStroke("control alt S"));
    addActionListener(new GuiActionListener(mainWindow));
  }
}
