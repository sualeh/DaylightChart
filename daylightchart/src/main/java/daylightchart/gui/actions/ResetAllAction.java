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

import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.Messages;
import daylightchart.gui.util.GuiAction;
import daylightchart.options.UserPreferences;

/**
 * Closes current tab.
 * 
 * @author sfatehi
 */
public final class ResetAllAction
  extends GuiAction
{

  private static final class GuiActionListener
    implements ActionListener
  {
    private final DaylightChartGui mainWindow;

    private GuiActionListener(final DaylightChartGui mainWindow)
    {
      this.mainWindow = mainWindow;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final ActionEvent actionevent)
    {
      // Clear all preferences
      UserPreferences.clear();

      final boolean slimUi = mainWindow.isSlimUi();
      restart(mainWindow, slimUi);
    }

  }

  private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Restarts the program, by closing and opening the main window, and
   * re-reading the preferences.
   * 
   * @param mainWindow
   *        Main window.
   * @param slimUi
   *        Whether the program should start with a minimal GUI.
   */
  public static void restart(final DaylightChartGui mainWindow,
                             final boolean slimUi)
  {
    // Dispose this window
    mainWindow.setVisible(false);
    mainWindow.dispose();

    // Open a new window
    new DaylightChartGui(slimUi).setVisible(true);
  }

  /**
   * Closes current tab.
   * 
   * @param mainWindow
   *        Main window
   */
  public ResetAllAction(final DaylightChartGui mainWindow)
  {
    super(Messages.getString("DaylightChartGui.Menu.Options.ResetAll"), //$NON-NLS-1$  
          "/icons/reset_all.gif"); //$NON-NLS-1$ 
    setShortcutKey(KeyStroke.getKeyStroke("control shift alt R"));
    addActionListener(new GuiActionListener(mainWindow));
  }

}
