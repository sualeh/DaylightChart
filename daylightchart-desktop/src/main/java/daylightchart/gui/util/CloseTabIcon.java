/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

/**
 * This class encapsulates an icon that can be added to the selector tab of a JTabbedPane to allow
 * clicks upon it to close its owning tab.
 *
 * <p>Use this code when you add the tab: <code>
 * tabbedPane.addTab(theTitle, new CloseTabIcon(), theComponent, tooltip);</code>
 *
 * <p>Created on 19/06/2006 by Tim Ryan
 */
public class CloseTabIcon implements Icon {

  private final Icon icon;
  private JTabbedPane tabbedPane;
  private Rectangle position;

  /** Creates a new instance of CloseTabIcon. */
  public CloseTabIcon() {
    icon = new ImageIcon(CloseTabIcon.class.getResource("/icons/close_tab.gif"));
  }

  /**
   * {@inheritDoc}
   *
   * @see Icon#getIconHeight()
   */
  @Override
  public int getIconHeight() {
    return icon.getIconHeight();
  }

  /**
   * {@inheritDoc}
   *
   * @see Icon#getIconWidth()
   */
  @Override
  public int getIconWidth() {
    return icon.getIconWidth();
  }

  /**
   * {@inheritDoc}
   *
   * @see Icon#paintIcon(Component, Graphics, int, int)
   */
  @Override
  public void paintIcon(final Component component, final Graphics g, final int x, final int y) {
    // When painting, remember last position painted so we can see if
    // the user clicked on the icon.
    position = new Rectangle(x, y, getIconWidth(), getIconHeight());

    icon.paintIcon(component, g, x, y);

    // Lazily create a link to the owning JTabbedPane and attach a
    // listener to it, so clicks on the selector tab can be intercepted
    // by this code.
    if (tabbedPane == null && component instanceof JTabbedPane pane) {
      tabbedPane = pane;
      tabbedPane.addMouseListener(
          new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent e) {
              // Asking for isConsumed is *very* important, otherwise more
              // than one tab might get closed.
              if (!e.isConsumed() && position != null && position.contains(e.getX(), e.getY())) {
                if (tabbedPane != null) {
                  tabbedPane.remove(tabbedPane.getSelectedComponent());
                }
                e.consume();
              }
            }
          });
    }
  }
}
