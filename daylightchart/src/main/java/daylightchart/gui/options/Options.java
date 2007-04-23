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
package daylightchart.gui.options;


import java.io.Serializable;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.plot.XYPlot;

/**
 * Options for customizing charts.
 * 
 * @author sfatehi
 */
public abstract class Options
  implements Serializable
{

  private static final JFreeChart chart = createDummyChart();

  private static JFreeChart createDummyChart()
  {
    final JFreeChart chart = new JFreeChart(new XYPlot());
    chart.setTitle("");
    final XYPlot plot = chart.getXYPlot();
    plot.setDomainAxis(new DateAxis());
    plot.setRangeAxis(new DateAxis());
    return chart;
  }

  /**
   * Copies options from the provided chart.
   * 
   * @param chart
   *        Chart to copy options from
   */
  public abstract void copyFromChart(JFreeChart chart);

  /**
   * Copies options from the provided chart editor.
   * 
   * @param chartEditor
   *        Chart editor to copy options from
   */
  public final void copyFromChartEditor(final ChartEditor chartEditor)
  {
    chartEditor.updateChart(chart);
    copyFromChart(chart);
  }

  /**
   * Gets a chart editor that has preset options.
   * 
   * @return Chart editor.
   */
  public final ChartEditor getChartEditor()
  {
    updateChart(chart);
    return ChartEditorManager.getChartEditor(chart);
  }

  /**
   * Updates a chart with these options.
   * 
   * @param chart
   *        Chart to update.
   */
  public abstract void updateChart(JFreeChart chart);

}
