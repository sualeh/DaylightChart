/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.actions;

import daylightchart.chart.options.ChartOptionsService;
import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.Messages;
import daylightchart.gui.util.GuiAction;
import daylightchart.options.service.UserPreferencesService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import javax.swing.KeyStroke;

/** Closes current tab. */
public final class ResetAllAction extends GuiAction {

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
      // Clear all preferences
      ChartOptionsService.chartOptions().clear();
      UserPreferencesService.preferences().clear();
      restart(mainWindow);
    }
  }

  @Serial private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Restarts the program, by closing and opening the main window, and re-reading the preferences.
   *
   * @param mainWindow Main window.
   */
  public static void restart(final DaylightChartGui mainWindow) {
    // Dispose this window
    mainWindow.setVisible(false);
    mainWindow.dispose();

    // Open a new window
    new DaylightChartGui().setVisible(true);
  }

  /**
   * Closes current tab.
   *
   * @param mainWindow Main window
   */
  public ResetAllAction(final DaylightChartGui mainWindow) {
    super(
        Messages.getString("DaylightChartGui.Menu.Options.ResetAll"), // $NON-NLS-1$
        "/icons/reset_all.gif"); //$NON-NLS-1$
    setShortcutKey(KeyStroke.getKeyStroke("control shift alt R"));
    addActionListener(new GuiActionListener(mainWindow));
  }
}
