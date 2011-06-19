/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2011, Sualeh Fatehi.
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

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.jfree.chart.editor.ChartEditor;

import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.Messages;
import daylightchart.gui.util.GuiAction;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;
import daylightchart.options.chart.ChartOptions;

/**
 * Shows Chart options.
 * 
 * @author sfatehi
 */
public final class ChartOptionsAction
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
      final Options options = UserPreferences.optionsFile().getData();
      final ChartOptions chartOptions = options.getChartOptions();

      final ChartEditor chartEditor = chartOptions.getChartEditor();
      final int confirmValue = JOptionPane
        .showConfirmDialog(mainWindow, chartEditor, Messages
          .getString("DaylightChartGui.Menu.Options.ChartOptions"), //$NON-NLS-1$
                           JOptionPane.OK_CANCEL_OPTION,
                           JOptionPane.PLAIN_MESSAGE);
      if (confirmValue == JOptionPane.OK_OPTION)
      {
        // Get chart options from the editor
        chartOptions.copyFromChartEditor(chartEditor);
        // Save preferences
        UserPreferences.optionsFile().save(options);
      }
    }
  }

  private static final long serialVersionUID = 4002590686393404496L;

  /**
   * Shows Help-About.
   * 
   * @param mainWindow
   *        Main window.
   */
  public ChartOptionsAction(final DaylightChartGui mainWindow)
  {
    super(Messages.getString("DaylightChartGui.Menu.Options.ChartOptions"), //$NON-NLS-1$ 
          "/icons/chart_options.gif" //$NON-NLS-1$
    );
    setShortcutKey(KeyStroke.getKeyStroke("control alt C"));
    addActionListener(new GuiActionListener(mainWindow));
  }
}
