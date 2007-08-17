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
package daylightchart.gui.actions;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import sf.util.ui.GuiAction;
import daylightchart.Version;
import daylightchart.gui.Messages;

/**
 * Shows Help-About.
 * 
 * @author sfatehi
 */
public final class AboutAction
  extends GuiAction
{

  private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Shows Help-About.
   * 
   * @param parent
   *        Main window.
   */
  public AboutAction(final Component parent)
  {
    super(Messages.getString("DaylightChartGui.Menu.Help.About") //$NON-NLS-1$
    );
    addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        JOptionPane.showMessageDialog(parent, Version.about());
      }
    });
  }
}
