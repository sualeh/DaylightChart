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
package daylightchart.chart.options;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleInsets;

/**
 * Options for customizing charts.
 * 
 * @author sfatehi
 */
public class PlotOptions
  extends Options
{

  private static final long serialVersionUID = 6801804849033048049L;

  private Paint backgroundPaint;
  private Paint outlinePaint;
  private transient Stroke outlineStroke;
  private RectangleInsets insets;
  //
  private final AxisOptions domainAxisOptions;
  private final AxisOptions rangeAxisOptions;

  /**
   * Constructor.
   */
  public PlotOptions()
  {
    backgroundPaint = Color.WHITE;
    outlinePaint = Color.WHITE;
    outlineStroke = new BasicStroke();
    insets = new RectangleInsets(0, 0, 0, 0);
    //
    domainAxisOptions = new AxisOptions();
    rangeAxisOptions = new AxisOptions();
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.chart.options.Options#copyFromChart(org.jfree.chart.JFreeChart)
   */
  @Override
  public void copyFromChart(final JFreeChart chart)
  {
    final Plot plot = chart.getPlot();

    backgroundPaint = plot.getBackgroundPaint();
    outlinePaint = plot.getOutlinePaint();
    outlineStroke = plot.getOutlineStroke();
    insets = plot.getInsets();
    //
    // Update axes
    Axis domainAxis = null;
    if (plot instanceof CategoryPlot)
    {
      final CategoryPlot p = (CategoryPlot) plot;
      domainAxis = p.getDomainAxis();
    }
    else if (plot instanceof XYPlot)
    {
      final XYPlot p = (XYPlot) plot;
      domainAxis = p.getDomainAxis();
    }
    if (domainAxis != null)
    {
      domainAxisOptions.getAxisProperties(domainAxis);
    }

    Axis rangeAxis = null;
    if (plot instanceof CategoryPlot)
    {
      final CategoryPlot p = (CategoryPlot) plot;
      rangeAxis = p.getRangeAxis();
    }
    else if (plot instanceof XYPlot)
    {
      final XYPlot p = (XYPlot) plot;
      rangeAxis = p.getRangeAxis();
    }
    if (rangeAxis != null)
    {
      rangeAxisOptions.getAxisProperties(rangeAxis);
    }
  }

  public Paint getBackgroundPaint()
  {
    return backgroundPaint;
  }

  public AxisOptions getDomainAxisOptions()
  {
    return domainAxisOptions;
  }

  public RectangleInsets getInsets()
  {
    return insets;
  }

  public Paint getOutlinePaint()
  {
    return outlinePaint;
  }

  public Stroke getOutlineStroke()
  {
    return outlineStroke;
  }

  public AxisOptions getRangeAxisOptions()
  {
    return rangeAxisOptions;
  }

  public void setBackgroundPaint(final Paint backgroundPaint)
  {
    this.backgroundPaint = backgroundPaint;
  }

  public void setInsets(final RectangleInsets insets)
  {
    this.insets = insets;
  }

  public void setOutlinePaint(final Paint outlinePaint)
  {
    this.outlinePaint = outlinePaint;
  }

  public void setOutlineStroke(final Stroke outlineStroke)
  {
    this.outlineStroke = outlineStroke;
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.chart.options.Options#updateChart(org.jfree.chart.JFreeChart)
   */
  @Override
  public void updateChart(final JFreeChart chart)
  {
    final Plot plot = chart.getPlot();

    plot.setBackgroundPaint(backgroundPaint);
    plot.setOutlinePaint(outlinePaint);
    plot.setOutlineStroke(outlineStroke);
    plot.setInsets(insets);

    // Update axes
    Axis domainAxis = null;
    if (plot instanceof CategoryPlot)
    {
      final CategoryPlot p = (CategoryPlot) plot;
      domainAxis = p.getDomainAxis();
    }
    else if (plot instanceof XYPlot)
    {
      final XYPlot p = (XYPlot) plot;
      domainAxis = p.getDomainAxis();
    }
    if (domainAxis != null)
    {
      domainAxisOptions.setAxisProperties(domainAxis);
    }

    Axis rangeAxis = null;
    if (plot instanceof CategoryPlot)
    {
      final CategoryPlot p = (CategoryPlot) plot;
      rangeAxis = p.getRangeAxis();
    }
    else if (plot instanceof XYPlot)
    {
      final XYPlot p = (XYPlot) plot;
      rangeAxis = p.getRangeAxis();
    }
    if (rangeAxis != null)
    {
      rangeAxisOptions.setAxisProperties(rangeAxis);
    }

  }

}
