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

import javax.swing.KeyStroke;

import daylightchart.gui.LocationsTabbedPane;
import daylightchart.gui.Messages;

import sf.util.ui.GuiAction;

/**
 * Saves the current chart.
 * 
 * @author sfatehi
 */
public final class SaveChartAction
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
    public void actionPerformed(@SuppressWarnings("unused")
    final ActionEvent actionevent)
    {
      locationsTabbedPane.saveSelectedChart();
    }
  }

  private static final long serialVersionUID = 5749903957626188378L;

  /**
   * Saves the current chart.
   * 
   * @param locationsTabbedPane
   *        Locations tabs.
   */
  public SaveChartAction(final LocationsTabbedPane locationsTabbedPane)
  {
    super(Messages.getString("DaylightChartGui.Menu.File.SaveChart"), //$NON-NLS-1$
          "/icons/save_chart.gif" //$NON-NLS-1$ 
    );
    setShortcutKey(KeyStroke.getKeyStroke("control S"));
    addActionListener(new GuiActionListener(locationsTabbedPane));
  }

}
