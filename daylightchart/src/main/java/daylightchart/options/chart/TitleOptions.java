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
import java.awt.Font;
import java.awt.Paint;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;

/**
 * Options for customizing charts.
 * 
 * @author sfatehi
 */
public class TitleOptions
  extends BaseChartOptions
{

  private static final long serialVersionUID = -6096894681186027546L;

  private Font titleFont;
  private Paint titlePaint;
  private String titleText;

  /**
   * Constructor.
   */
  public TitleOptions()
  {
    titleFont = new Font("Default", Font.PLAIN, 12);
    titlePaint = Color.BLACK;
    titleText = "";
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.chart.Options#copyFromChart(org.jfree.chart.JFreeChart)
   */
  @Override
  public void copyFromChart(final JFreeChart chart)
  {
    final TextTitle title = chart.getTitle();
    titleFont = title.getFont();
    titlePaint = title.getPaint();
    titleText = title.getText();
  }

  public Font getTitleFont()
  {
    return titleFont;
  }

  public Paint getTitlePaint()
  {
    return titlePaint;
  }

  public String getTitleText()
  {
    return titleText;
  }

  public void setTitleFont(final Font titleFont)
  {
    this.titleFont = titleFont;
  }

  public void setTitlePaint(final Paint titlePaint)
  {
    this.titlePaint = titlePaint;
  }

  public void setTitleText(final String titleText)
  {
    this.titleText = titleText;
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.chart.Options#updateChart(org.jfree.chart.JFreeChart)
   */
  @Override
  public void updateChart(final JFreeChart chart)
  {
    final TextTitle title = chart.getTitle();
    if (title != null)
    {
      title.setFont(titleFont);
      title.setPaint(titlePaint);
      title.setText(titleText);
    }
  }

}
