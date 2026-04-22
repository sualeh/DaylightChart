/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/** Exits an application. */
public final class ExitAction extends GuiAction {

  @Serial private static final long serialVersionUID = 5749903957626188378L;

  /**
   * Exits an application
   *
   * @param frame Main window
   * @param text Text for the action
   */
  public ExitAction(final JFrame frame, final String text) {
    super(
        text, "/icons/exit.gif" // $NON-NLS-1$
        );
    setShortcutKey(KeyStroke.getKeyStroke("control Q"));
    addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent actionevent) {
            frame.dispose();
            System.exit(0);
          }
        });
  }
}
