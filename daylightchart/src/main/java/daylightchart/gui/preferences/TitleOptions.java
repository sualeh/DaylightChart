package daylightchart.gui.preferences;


import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;

public class TitleOptions
  extends Options
{
  /**
   * 
   */
  private static final long serialVersionUID = -6096894681186027546L;

  private Font titleFont;
  private Paint titlePaint;
  private String titleText;

  public TitleOptions()
  {
    titleFont = new Font("Default", Font.PLAIN, 12);
    titlePaint = Color.BLACK;
    titleText = "";
  }

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
