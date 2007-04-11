package daylightchart.gui.preferences;


import java.io.Serializable;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.plot.XYPlot;

public abstract class Options
  implements Serializable
{

  public final void copyFromChartEditor(final ChartEditor chartEditor)
  {
    // Create new dummy chart, and copy options
    final JFreeChart chart = new JFreeChart(new XYPlot());
    chartEditor.updateChart(chart);
    copyFromChart(chart);
  }

  public final ChartEditor getChartEditor()
  {
    // Create new dummy chart, and copy options
    final JFreeChart chart = new JFreeChart(new XYPlot());
    updateChart(chart);
    return ChartEditorManager.getChartEditor(chart);
  }

  public abstract void copyFromChart(JFreeChart chart);

  public abstract void updateChart(JFreeChart chart);

}
