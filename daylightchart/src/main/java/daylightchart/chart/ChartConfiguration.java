package daylightchart.chart;


import java.awt.Dimension;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * One place to change certain configuration options.
 * 
 * @author Sualeh Fatehi
 */
public final class ChartConfiguration
{

  private final Dimension chartDimension;
  private final DateFormat monthsFormat;

  /**
   * Constructor.
   */
  public ChartConfiguration()
  {
    chartDimension = new Dimension(770, 570);
    monthsFormat = new SimpleDateFormat("MMM");
  }

  /**
   * Chart dimension.
   * 
   * @return Chart dimensions
   */
  public Dimension getChartDimension()
  {
    return chartDimension;
  }

  /**
   * Date format for months.
   * 
   * @return Date format for months
   */
  public DateFormat getMonthsFormat()
  {
    return monthsFormat;
  }

}
