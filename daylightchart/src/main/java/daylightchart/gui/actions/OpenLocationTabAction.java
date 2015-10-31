/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2015, Sualeh Fatehi.
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

import org.geoname.data.Location;

import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.util.GuiAction;

/**
 * Closes current tab.
 * 
 * @author sfatehi
 */
public final class OpenLocationTabAction
  extends GuiAction
{

  private final class GuiActionListener
    implements ActionListener
  {
    private final DaylightChartGui mainWindow;
    private final Location location;

    private GuiActionListener(final DaylightChartGui mainWindow,
                              final Location location)
    {
      this.mainWindow = mainWindow;
      this.location = location;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final ActionEvent actionevent)
    {
      mainWindow.addLocationTab(location);
    }
  }

  private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Opens a new tab, with the specified location.
   * 
   * @param mainWindow
   *        Main window
   * @param location
   *        Location
   * @param index
   *        Index on the menu
   */
  public OpenLocationTabAction(final DaylightChartGui mainWindow,
                               final Location location,
                               final int index)
  {
    super((index + 1) + " " + location.getDescription());
    setShortcutKey(KeyStroke.getKeyStroke("control " + (index + 1)));
    addActionListener(new GuiActionListener(mainWindow, location));
  }

}
