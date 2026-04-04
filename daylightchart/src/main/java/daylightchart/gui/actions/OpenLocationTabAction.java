/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.actions;

import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.util.GuiAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import javax.swing.KeyStroke;
import org.geoname.data.Location;

/** Closes current tab. */
public final class OpenLocationTabAction extends GuiAction {

  private final class GuiActionListener implements ActionListener {
    private final DaylightChartGui mainWindow;
    private final Location location;

    private GuiActionListener(final DaylightChartGui mainWindow, final Location location) {
      this.mainWindow = mainWindow;
      this.location = location;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent actionevent) {
      mainWindow.addLocationTab(location);
    }
  }

  @Serial private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Opens a new tab, with the specified location.
   *
   * @param mainWindow Main window
   * @param location Location
   * @param index Index on the menu
   */
  public OpenLocationTabAction(
      final DaylightChartGui mainWindow, final Location location, final int index) {
    super(index + 1 + " " + location.getDescription());
    setShortcutKey(KeyStroke.getKeyStroke("control " + (index + 1)));
    addActionListener(new GuiActionListener(mainWindow, location));
  }
}
