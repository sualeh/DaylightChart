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
package daylightchart.options.chart;


import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.JFreeChart;

/**
 * Options for customizing charts.
 * 
 * @author sfatehi
 */
public class ChartOptions
  extends BaseChartOptions
{

  private static final long serialVersionUID = -7527051325325384357L;

  private boolean antiAlias;
  private Paint backgroundPaint;
  //
  private final PlotOptions plotOptions;
  private final TitleOptions titleOptions;

  /**
   * Constructor.
   */
  public ChartOptions()
  {
    antiAlias = false;
    backgroundPaint = Color.WHITE;
    //
    plotOptions = new PlotOptions();
    titleOptions = new TitleOptions();
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.chart.Options#copyFromChart(org.jfree.chart.JFreeChart)
   */
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

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.chart.Options#updateChart(org.jfree.chart.JFreeChart)
   */
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
