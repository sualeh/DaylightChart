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
import daylightchart.gui.OptionsDialog;
import daylightchart.gui.util.GuiAction;
import daylightchart.options.Options;
import daylightchart.options.service.UserPreferencesService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import javax.swing.KeyStroke;

/** Shows options. */
public final class OptionsAction extends GuiAction {

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
      Options options = UserPreferencesService.preferences().loadOptions();
      options = OptionsDialog.showOptionsDialog(mainWindow, options);
      UserPreferencesService.preferences().saveOptions(options);
      mainWindow.sortLocations();
    }
  }

  @Serial private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Shows Help-About.
   *
   * @param mainWindow Main window.
   */
  public OptionsAction(final DaylightChartGui mainWindow) {
    super(
        Messages.getString("DaylightChartGui.Menu.Options.Options"), // $NON-NLS-1$
        "/icons/options.gif" //$NON-NLS-1$
        );
    setShortcutKey(KeyStroke.getKeyStroke("control alt O"));
    addActionListener(new GuiActionListener(mainWindow));
  }
}
