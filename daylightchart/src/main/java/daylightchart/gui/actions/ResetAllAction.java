/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import sf.util.ui.GuiAction;
import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.Messages;
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
    private final DaylightChartGui daylightChartGui;

    private GuiActionListener(final DaylightChartGui daylightChartGui)
    {
      this.daylightChartGui = daylightChartGui;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(@SuppressWarnings("unused")
    final ActionEvent actionevent)
    {
      // Clear all preferences
      UserPreferences.clear();

      // Dispose this window
      final JFrame mainWindow = daylightChartGui;
      mainWindow.setVisible(false);
      mainWindow.dispose();

      // Open a new window
      new DaylightChartGui().setVisible(true);
    }
  }

  private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Closes current tab.
   * 
   * @param daylightChartGui
   *        Main window
   */
  public ResetAllAction(final DaylightChartGui daylightChartGui)
  {
    super(Messages.getString("DaylightChartGui.Menu.Options.ResetAll"), //$NON-NLS-1$  
          "/icons/reset_all.gif"); //$NON-NLS-1$ 
    setShortcutKey(KeyStroke.getKeyStroke("control shift alt R"));
    addActionListener(new GuiActionListener(daylightChartGui));
  }

}
