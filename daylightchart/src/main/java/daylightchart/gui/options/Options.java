package daylightchart.gui.options;


import java.io.Serializable;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.plot.XYPlot;

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

  public abstract void copyFromChart(JFreeChart chart);

  public final void copyFromChartEditor(final ChartEditor chartEditor)
  {
    chartEditor.updateChart(chart);
    copyFromChart(chart);
  }

  public final ChartEditor getChartEditor()
  {
    updateChart(chart);
    return ChartEditorManager.getChartEditor(chart);
  }

  public abstract void updateChart(JFreeChart chart);

}
