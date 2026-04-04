/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.actions;

import daylightchart.gui.LocationsTabbedPane;
import daylightchart.gui.util.GuiAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import javax.swing.KeyStroke;

/** Closes current tab. */
public final class CloseCurrentTabAction extends GuiAction {

  private static final class GuiActionListener implements ActionListener {
    private final LocationsTabbedPane locationsTabbedPane;

    private GuiActionListener(final LocationsTabbedPane locationsTabbedPane) {
      this.locationsTabbedPane = locationsTabbedPane;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent actionevent) {
      if (locationsTabbedPane.getTabCount() > 0) {
        locationsTabbedPane.remove(locationsTabbedPane.getSelectedComponent());
      }
    }
  }

  @Serial private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Closes current tab.
   *
   * @param locationsTabbedPane Tabbed pane
   */
  public CloseCurrentTabAction(final LocationsTabbedPane locationsTabbedPane) {
    super(
        "Close Current Tab", //$NON-NLS-1$
        "/icons/close_tab.gif" //$NON-NLS-1$
        );
    setShortcutKey(KeyStroke.getKeyStroke("control W"));
    addActionListener(new GuiActionListener(locationsTabbedPane));
  }
}
