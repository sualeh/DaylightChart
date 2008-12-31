/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2009, Sualeh Fatehi.
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

import sf.util.ui.BareBonesBrowserLaunch;
import sf.util.ui.GuiAction;
import daylightchart.gui.Messages;

/**
 * Shows online help.
 * 
 * @author sfatehi
 */
public final class OnlineHelpAction
  extends GuiAction
{

  private static final class GuiActionListener
    implements ActionListener
  {
    /**
     * {@inheritDoc}
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final ActionEvent actionevent)
    {
      BareBonesBrowserLaunch
        .openURL("http://daylightchart.sourceforge.net/howto.html"); //$NON-NLS-1$
    }
  }

  private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Shows online help.
   */
  public OnlineHelpAction()
  {
    super(Messages.getString("DaylightChartGui.Menu.Help.Online"), //$NON-NLS-1$
          "/icons/help.gif" //$NON-NLS-1$ 
    );
    setShortcutKey(KeyStroke.getKeyStroke("F1"));
    addActionListener(new GuiActionListener());
  }

}
