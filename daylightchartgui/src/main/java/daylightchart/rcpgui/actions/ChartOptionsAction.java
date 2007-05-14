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
package daylightchart.rcpgui.actions;


import javax.swing.JOptionPane;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.jfree.chart.editor.ChartEditor;

import daylightchart.chart.DaylightChart;
import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.Messages;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;
import daylightchart.options.chart.ChartEditorFactory;
import daylightchart.options.chart.ChartOptions;

public class ChartOptionsAction
  extends Action
{

  public static final String ID = ChartOptionsAction.class.getName();

  private final IWorkbenchWindow window;

  public ChartOptionsAction(final IWorkbenchWindow window)
  {
    this.window = window;
    setText(Messages.getString("DaylightChartGui.Menu.Options.ChartOptions")); //$NON-NLS-1$
    // The id is used to refer to the action in a menu or toolbar
    setId(ID);
    // Associate the action with a pre-defined command, to allow key
    // bindings.
    setActionDefinitionId(ID);
  }

  @Override
  public void run()
  {
    if (window == null)
    {
      return;
    }

    final Options options = UserPreferences.getOptions();
    final ChartOptions chartOptions = options.getChartOptions();

//    final SWTChartEditor chartEditor = ChartEditorFactory
//      .getXYPlotChartEditorFromOptions(window.getShell().getDisplay(),
//                                       chartOptions);
//    chartEditor.open();
    
    final ChartEditor chartEditor = ChartEditorFactory
    .getXYPlotChartEditorFromOptions(chartOptions);
  final int confirmValue = JOptionPane
    .showConfirmDialog(null, chartEditor, Messages
      .getString("DaylightChartGui.Menu.Options.ChartOptions"), //$NON-NLS-1$
                       JOptionPane.OK_CANCEL_OPTION,
                       JOptionPane.PLAIN_MESSAGE);
  if (confirmValue == JOptionPane.OK_OPTION)
  {
    // Get chart options from the editor
    chartOptions.copyFromChartEditor(chartEditor);
    // Save preferences
    UserPreferences.setOptions(options);
  }

  }
}
