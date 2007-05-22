/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
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
package daylightchart.gui;


import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

/**
 * This class encapsulates an icon that can be added to the selector tab
 * of a JTabbedPane to allow clicks upon it to close its owning tab.
 * <p>
 * Use this code when you add the tab:
 * </p>
 * <code>tabbedPane.addTab(theTitle, new CloseTabIcon(), theComponent, tooltip);</code>
 * <p>
 * Created on 19/06/2006 by Tim Ryan
 * </p>
 */
public class CloseTabIcon
  implements Icon
{

  private final Icon icon;
  private JTabbedPane tabbedPane;
  private Rectangle position;

  /**
   * Creates a new instance of CloseTabIcon.
   */
  public CloseTabIcon()
  {
    icon = new ImageIcon(CloseTabIcon.class.getResource("/close_tab.gif"));
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.swing.Icon#getIconHeight()
   */
  public int getIconHeight()
  {
    return icon.getIconHeight();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.swing.Icon#getIconWidth()
   */
  public int getIconWidth()
  {
    return icon.getIconWidth();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.swing.Icon#paintIcon(java.awt.Component,
   *      java.awt.Graphics, int, int)
   */
  public void paintIcon(final Component component,
                        final Graphics g,
                        final int x,
                        final int y)
  {
    // When painting, remember last position painted so we can see if
    // the user clicked on the icon.
    position = new Rectangle(x, y, getIconWidth(), getIconHeight());

    icon.paintIcon(component, g, x, y);

    // Lazily create a link to the owning JTabbedPane and attach a
    // listener to it, so clicks on the selector tab can be intercepted
    // by this code.
    if (tabbedPane == null && component instanceof JTabbedPane)
    {
      tabbedPane = (JTabbedPane) component;
      tabbedPane.addMouseListener(new MouseAdapter()
      {
        @Override
        public void mouseReleased(final MouseEvent e)
        {
          // Asking for isConsumed is *very* important, otherwise more
          // than one tab might get closed.
          if (!e.isConsumed() && position != null
              && position.contains(e.getX(), e.getY()))
          {
            if (tabbedPane != null)
            {
              tabbedPane.remove(tabbedPane.getSelectedComponent());
            }
            e.consume();
          }
        }
      });
    }
  }

}
