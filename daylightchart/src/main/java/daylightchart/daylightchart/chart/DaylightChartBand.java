package daylightchart.daylightchart.chart;


import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.joda.time.LocalDateTime;

import daylightchart.daylightchart.calculation.DaylightBand;
import daylightchart.daylightchart.calculation.DaylightBandType;
import daylightchart.daylightchart.calculation.RiseSet;

/**
 * Adapter for daylight bands to add charting functionality.
 * 
 * @author Sualeh Fatehi
 */
public class DaylightChartBand
{

  private final DaylightBand daylightBand;

  DaylightChartBand(final DaylightBand daylightBand)
  {
    if (daylightBand == null)
    {
      throw new IllegalArgumentException("DaylightBand not provided");
    }
    this.daylightBand = daylightBand;
  }

  /**
   * Gets the chart renderer for the daylight band.
   * 
   * @return Chart renderer
   */
  public XYItemRenderer getRenderer()
  {
    return getRenderer(daylightBand.getDaylightBandType());
  }

  /**
   * Get the band as a collection.
   * 
   * @return Time series collection
   */
  public TimeSeriesCollection getTimeSeriesCollection()
  {
    final String name = daylightBand.getName();
    final TimeSeries sunriseSeries = new TimeSeries("Sunrise " + name);
    final TimeSeries sunsetSeries = new TimeSeries("Sunset " + name);
    for (final RiseSet riseSet: daylightBand.getRiseSets())
    {
      final LocalDateTime sunrise = riseSet.getSunrise();
      final LocalDateTime sunset = riseSet.getSunset();
      sunriseSeries.add(toTimeSeriesDataItem(sunrise));
      sunsetSeries.add(toTimeSeriesDataItem(sunset));
    }

    final TimeSeriesCollection band = new TimeSeriesCollection();
    band.addSeries(sunriseSeries);
    band.addSeries(sunsetSeries);

    return band;
  }

  private XYItemRenderer createDifferenceRenderer(final Color color)
  {
    XYItemRenderer renderer;
    renderer = new XYDifferenceRenderer(color, color, false);
    renderer.setBaseStroke(new BasicStroke(0.1f));
    renderer.setSeriesPaint(0, color);
    renderer.setSeriesPaint(1, color);
    return renderer;
  }

  private XYItemRenderer createOutlineRenderer()
  {
    XYItemRenderer renderer;
    renderer = new XYLineAndShapeRenderer(true, false);
    renderer.setBaseStroke(new BasicStroke(0.8f));
    renderer.setSeriesPaint(0, Color.white);
    renderer.setSeriesPaint(1, Color.white);
    return renderer;
  }

  /**
   * Gets the chart renderer for the daylight band.
   * 
   * @return Chart renderer
   */
  private XYItemRenderer getRenderer(final DaylightBandType daylightBandType)
  {
    XYItemRenderer renderer;
    switch (daylightBandType)
    {
      case with_clock_shift:
        renderer = createDifferenceRenderer(ChartConfiguration.daylightColor);
        break;
      case without_clock_shift:
        renderer = createOutlineRenderer();
        break;
      case twilight:
        renderer = createDifferenceRenderer(ChartConfiguration.twilightColor);
        break;
      default:
        renderer = null;
        break;
    }
    return renderer;
  }

  /**
   * A utility method for creating a value based on a date.
   * 
   * @param date
   *        Date
   */
  private TimeSeriesDataItem toTimeSeriesDataItem(final LocalDateTime dateTime)
  {
    final Day day = new Day(dateTime.getDayOfMonth(),
                            dateTime.getMonthOfYear(),
                            dateTime.getYear());
    final Minute minute = new Minute(dateTime.getMinuteOfHour(), dateTime
      .getHourOfDay(), 1, 1, 1970);
    final long minuteValue = minute.getFirstMillisecond();
    return new TimeSeriesDataItem(day, minuteValue);
  }

}
