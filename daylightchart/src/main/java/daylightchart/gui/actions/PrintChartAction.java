/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.actions;

import daylightchart.gui.LocationsTabbedPane;
import daylightchart.gui.Messages;
import daylightchart.gui.util.GuiAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import javax.swing.KeyStroke;

/** Prints the current chart. */
public final class PrintChartAction extends GuiAction {

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
      locationsTabbedPane.printSelectedChart();
    }
  }

  @Serial private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Prints the current chart.
   *
   * @param locationsTabbedPane Locations tabs.
   */
  public PrintChartAction(final LocationsTabbedPane locationsTabbedPane) {
    super(
        Messages.getString("DaylightChartGui.Menu.File.PrintChart"), // $NON-NLS-1$
        "/icons/print_chart.gif" //$NON-NLS-1$
        );
    setShortcutKey(KeyStroke.getKeyStroke("control P"));
    addActionListener(new GuiActionListener(locationsTabbedPane));
  }
}
