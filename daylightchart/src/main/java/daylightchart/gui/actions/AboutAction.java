/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.actions;

import daylightchart.Version;
import daylightchart.gui.Messages;
import daylightchart.gui.util.GuiAction;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import javax.swing.JOptionPane;

/** Shows Help-About. */
public final class AboutAction extends GuiAction {

  private static final class GuiActionListener implements ActionListener {
    private final Component parent;

    private GuiActionListener(final Component parent) {
      this.parent = parent;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent actionevent) {
      JOptionPane.showMessageDialog(
          parent, Version.about(), Version.getProductName(), JOptionPane.PLAIN_MESSAGE);
    }
  }

  @Serial private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Shows Help-About.
   *
   * @param parent Main window.
   */
  public AboutAction(final Component parent) {
    super(
        Messages.getString("DaylightChartGui.Menu.Help.About"), // $NON-NLS-1$
        "/icons/help_about.gif" //$NON-NLS-1$
        );
    addActionListener(new GuiActionListener(parent));
  }
}
