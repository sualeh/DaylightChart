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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import daylightchart.gui.LocationsTabbedPane;
import daylightchart.gui.Messages;

import sf.util.ui.GuiAction;

/**
 * Prints the current chart.
 * 
 * @author sfatehi
 */
public final class PrintChartAction
  extends GuiAction
{

  private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Prints the current chart.
   * 
   * @param locationsTabbedPane
   *        Locations tabs.
   */
  public PrintChartAction(final LocationsTabbedPane locationsTabbedPane)
  {
    super(Messages.getString("DaylightChartGui.Menu.File.PrintChart"), //$NON-NLS-1$
          "/icons/print_chart.gif" //$NON-NLS-1$ 
    );
    addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        locationsTabbedPane.printSelectedChart();
      }
    });
  }

}
