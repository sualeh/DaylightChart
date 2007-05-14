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
package daylightchart.rcpgui;


import org.eclipse.swt.widgets.Display;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.plot.XYPlot;
import org.jfree.experimental.chart.swt.editor.SWTChartEditor;

import daylightchart.options.chart.ChartOptions;

/**
 * Chart editor factory creates a chart for
 * 
 * @author sfatehi
 */
public final class ChartEditorFactory
{

  /**
   * Gets a chart editor that has preset options.
   * 
   * @return Chart editor.
   */
  public static final ChartEditor getXYPlotChartEditorFromOptions(final Display display,
                                                                  final ChartOptions options)
  {
    // This is overkill, but make a fake chart, whose sole purpose is to
    // transfer settings between a chart editor and chart options
    final JFreeChart chart = new JFreeChart(new XYPlot());
    options.updateChart(chart);
    return new SWTChartEditor(display, chart);
  }

  private ChartEditorFactory()
  {
    // Prevent instantiation
  }

}
