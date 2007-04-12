package daylightchart.gui.preferences;


import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.JFreeChart;

public class ChartOptions
  extends Options
{

  private static final long serialVersionUID = -7527051325325384357L;

  private boolean antiAlias;
  private Paint backgroundPaint;
  //
  private final PlotOptions plotOptions;
  private final TitleOptions titleOptions;

  public ChartOptions()
  {
    antiAlias = false;
    backgroundPaint = Color.WHITE;
    //
    plotOptions = new PlotOptions();
    titleOptions = new TitleOptions();
  }

  @Override
  public void copyFromChart(final JFreeChart chart)
  {
    antiAlias = chart.getAntiAlias();
    backgroundPaint = chart.getBackgroundPaint();
    //
    plotOptions.copyFromChart(chart);
    titleOptions.copyFromChart(chart);
  }

  public Paint getBackgroundPaint()
  {
    return backgroundPaint;
  }

  public PlotOptions getPlotOptions()
  {
    return plotOptions;
  }

  public TitleOptions getTitleOptions()
  {
    return titleOptions;
  }

  public boolean isAntiAlias()
  {
    return antiAlias;
  }

  public void setAntiAlias(final boolean antiAlias)
  {
    this.antiAlias = antiAlias;
  }

  public void setBackgroundPaint(final Paint backgroundPaint)
  {
    this.backgroundPaint = backgroundPaint;
  }

  @Override
  public void updateChart(final JFreeChart chart)
  {
    if (chart instanceof ChartOptionsListener)
    {
      ((ChartOptionsListener) chart).beforeSettingChartOptions(this);
    }
    //
    chart.setAntiAlias(antiAlias);
    chart.setBackgroundPaint(backgroundPaint);
    //
    plotOptions.updateChart(chart);
    titleOptions.updateChart(chart);
    //
    if (chart instanceof ChartOptionsListener)
    {
      ((ChartOptionsListener) chart).afterSettingChartOptions(this);
    }
  }

}
