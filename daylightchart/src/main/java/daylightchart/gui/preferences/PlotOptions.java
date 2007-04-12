package daylightchart.gui.preferences;


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
