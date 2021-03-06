/*
 *
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2016, Sualeh Fatehi.
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
package daylightchart.gui.actions;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.KeyStroke;

import daylightchart.gui.LocationsTabbedPane;
import daylightchart.gui.util.GuiAction;

/**
 * Closes current tab.
 *
 * @author sfatehi
 */
public final class CloseCurrentTabAction
  extends GuiAction
{

  private static final class GuiActionListener
    implements ActionListener
  {
    private final LocationsTabbedPane locationsTabbedPane;

    private GuiActionListener(final LocationsTabbedPane locationsTabbedPane)
    {
      this.locationsTabbedPane = locationsTabbedPane;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent actionevent)
    {
      if (locationsTabbedPane.getTabCount() > 0)
      {
        locationsTabbedPane.remove(locationsTabbedPane.getSelectedComponent());
      }
    }
  }

  private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Closes current tab.
   *
   * @param locationsTabbedPane
   *        Tabbed pane
   */
  public CloseCurrentTabAction(final LocationsTabbedPane locationsTabbedPane)
  {
    super("Close Current Tab", //$NON-NLS-1$
          "/icons/close_tab.gif" //$NON-NLS-1$
    );
    setShortcutKey(KeyStroke.getKeyStroke("control W"));
    addActionListener(new GuiActionListener(locationsTabbedPane));
  }
}
