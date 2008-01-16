/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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


import java.awt.Font;
import java.awt.Paint;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.ui.RectangleInsets;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Options for customizing charts.
 * 
 * @author sfatehi
 */
public class AxisOptions
  extends BaseChartOptions
{

  private static final long serialVersionUID = 7658976939630828679L;

  private String label;
  private Font labelFont;
  private RectangleInsets labelInsets;
  private Paint labelPaint;
  //
  private Font tickLabelFont;
  private RectangleInsets tickLabelInsets;
  private Paint tickLabelPaint;
  private boolean tickLabelsVisible;
  private boolean tickMarksVisible;

  /**
   * {@inheritDoc}
   * 
   * @see BaseChartOptions#copyFromChart(org.jfree.chart.JFreeChart)
   */
  @Override
  public void copyFromChart(@SuppressWarnings("unused")
  final JFreeChart chart)
  {
    // We do not know which axis to work from
    throw new NotImplementedException();
  }

  /**
   * @return the label
   */
  public String getLabel()
  {
    return label;
  }

  /**
   * @return the labelFont
   */
  public Font getLabelFont()
  {
    return labelFont;
  }

  /**
   * @return the labelInsets
   */
  public RectangleInsets getLabelInsets()
  {
    return labelInsets;
  }

  /**
   * @return the labelPaint
   */
  public Paint getLabelPaint()
  {
    return labelPaint;
  }

  /**
   * @return the tickLabelFont
   */
  public Font getTickLabelFont()
  {
    return tickLabelFont;
  }

  /**
   * @return the tickLabelInsets
   */
  public RectangleInsets getTickLabelInsets()
  {
    return tickLabelInsets;
  }

  /**
   * @return the tickLabelPaint
   */
  public Paint getTickLabelPaint()
  {
    return tickLabelPaint;
  }

  /**
   * @return the tickLabelsVisible
   */
  public boolean isTickLabelsVisible()
  {
    return tickLabelsVisible;
  }

  /**
   * @return the tickMarksVisible
   */
  public boolean isTickMarksVisible()
  {
    return tickMarksVisible;
  }

  /**
   * @param label
   *        the label to set
   */
  public void setLabel(final String label)
  {
    this.label = label;
  }

  /**
   * @param labelFont
   *        the labelFont to set
   */
  public void setLabelFont(final Font labelFont)
  {
    this.labelFont = labelFont;
  }

  /**
   * @param labelInsets
   *        the labelInsets to set
   */
  public void setLabelInsets(final RectangleInsets labelInsets)
  {
    this.labelInsets = labelInsets;
  }

  /**
   * @param labelPaint
   *        the labelPaint to set
   */
  public void setLabelPaint(final Paint labelPaint)
  {
    this.labelPaint = labelPaint;
  }

  /**
   * @param tickLabelFont
   *        the tickLabelFont to set
   */
  public void setTickLabelFont(final Font tickLabelFont)
  {
    this.tickLabelFont = tickLabelFont;
  }

  /**
   * @param tickLabelInsets
   *        the tickLabelInsets to set
   */
  public void setTickLabelInsets(final RectangleInsets tickLabelInsets)
  {
    this.tickLabelInsets = tickLabelInsets;
  }

  /**
   * @param tickLabelPaint
   *        the tickLabelPaint to set
   */
  public void setTickLabelPaint(final Paint tickLabelPaint)
  {
    this.tickLabelPaint = tickLabelPaint;
  }

  /**
   * @param tickLabelsVisible
   *        the tickLabelsVisible to set
   */
  public void setTickLabelsVisible(final boolean tickLabelsVisible)
  {
    this.tickLabelsVisible = tickLabelsVisible;
  }

  /**
   * @param tickMarksVisible
   *        the tickMarksVisible to set
   */
  public void setTickMarksVisible(final boolean tickMarksVisible)
  {
    this.tickMarksVisible = tickMarksVisible;
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseChartOptions#updateChart(org.jfree.chart.JFreeChart)
   */
  @Override
  public void updateChart(@SuppressWarnings("unused")
  final JFreeChart chart)
  {
    // We do not know which axis to update
    throw new NotImplementedException();
  }

  /**
   * Gets the properties of the specified axis to match the properties
   * defined on this panel.
   * 
   * @param axis
   *        the axis.
   */
  void getAxisProperties(final Axis axis)
  {
    label = axis.getLabel();
    labelFont = axis.getLabelFont();
    labelPaint = axis.getLabelPaint();
    labelInsets = axis.getLabelInsets();
    //
    tickMarksVisible = axis.isTickMarksVisible();
    tickLabelsVisible = axis.isTickLabelsVisible();
    tickLabelFont = axis.getTickLabelFont();
    tickLabelPaint = axis.getTickLabelPaint();
    tickLabelInsets = axis.getTickLabelInsets();
  }

  /**
   * Sets the properties of the specified axis to match the properties
   * defined on this panel.
   * 
   * @param axis
   *        the axis.
   */
  void setAxisProperties(final Axis axis)
  {
    axis.setLabel(label);
    if (labelFont != null)
    {
      axis.setLabelFont(labelFont);
    }
    if (labelPaint != null)
    {
      axis.setLabelPaint(labelPaint);
    }
    if (labelInsets != null)
    {
      axis.setLabelInsets(labelInsets);
    }
    //
    axis.setTickMarksVisible(tickMarksVisible);
    axis.setTickLabelsVisible(tickLabelsVisible);
    if (tickLabelFont != null)
    {
      axis.setTickLabelFont(tickLabelFont);
    }
    if (tickLabelPaint != null)
    {
      axis.setTickLabelPaint(tickLabelPaint);
    }
    if (tickLabelInsets != null)
    {
      axis.setTickLabelInsets(tickLabelInsets);
    }
  }

}
