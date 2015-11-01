/*
 *
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
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

import daylightchart.gui.LocationsTabbedPane;
import daylightchart.gui.Messages;
import daylightchart.gui.util.GuiAction;

/**
 * Prints the current chart.
 *
 * @author sfatehi
 */
public final class PrintChartAction
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
      locationsTabbedPane.printSelectedChart();
    }
  }

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
    setShortcutKey(KeyStroke.getKeyStroke("control P"));
    addActionListener(new GuiActionListener(locationsTabbedPane));
  }

}
