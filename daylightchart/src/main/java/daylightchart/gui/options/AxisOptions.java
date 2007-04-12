package daylightchart.gui.options;


import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.ui.RectangleInsets;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class AxisOptions
  extends Options
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

  public AxisOptions()
  {
    label = "";
    labelFont = new Font("Default", Font.PLAIN, 12);
    labelInsets = new RectangleInsets(0, 0, 0, 0);
    labelPaint = Color.BLACK;
    //
    tickLabelFont = new Font("Default", Font.PLAIN, 12);
    tickLabelInsets = new RectangleInsets(0, 0, 0, 0);
    tickLabelPaint = Color.BLACK;
    tickLabelsVisible = true;
    tickMarksVisible = true;
  }

  @Override
  public void copyFromChart(final JFreeChart chart)
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

  @Override
  public void updateChart(final JFreeChart chart)
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
    axis.setLabelFont(labelFont);
    axis.setLabelPaint(labelPaint);
    axis.setLabelInsets(labelInsets);
    //
    axis.setTickMarksVisible(tickMarksVisible);
    axis.setTickLabelsVisible(tickLabelsVisible);
    axis.setTickLabelFont(tickLabelFont);
    axis.setTickLabelPaint(tickLabelPaint);
    axis.setTickLabelInsets(tickLabelInsets);
  }

}