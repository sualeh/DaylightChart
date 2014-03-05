/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2014, Sualeh Fatehi.
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
import daylightchart.gui.OptionsDialog;
import daylightchart.gui.util.GuiAction;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;

/**
 * Shows options.
 * 
 * @author sfatehi
 */
public final class OptionsAction
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
      Options options = UserPreferences.optionsFile().getData();
      options = OptionsDialog.showOptionsDialog(mainWindow, options);
      UserPreferences.optionsFile().save(options);
      mainWindow.sortLocations();
    }
  }

  private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Shows Help-About.
   * 
   * @param mainWindow
   *        Main window.
   */
  public OptionsAction(final DaylightChartGui mainWindow)
  {
    super(Messages.getString("DaylightChartGui.Menu.Options.Options"), //$NON-NLS-1$ 
          "/icons/options.gif" //$NON-NLS-1$
    );
    setShortcutKey(KeyStroke.getKeyStroke("control alt O"));
    addActionListener(new GuiActionListener(mainWindow));
  }

}
