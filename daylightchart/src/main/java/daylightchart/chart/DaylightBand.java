package daylightchart.chart;


import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.joda.time.LocalDateTime;

/**
 * One daylight band, consisting of a sunrise series and a sunset
 * series.
 * 
 * @author sfatehi
 */
public class DaylightBand
{

  private final String name;
  private final TimeSeries sunriseSeries;
  private final TimeSeries sunsetSeries;

  /**
   * Create a new daylight band.
   * 
   * @param name
   *        Name of the band.
   */
  public DaylightBand(String name)
  {
    this.name = name;
    sunriseSeries = new TimeSeries("Sunrise " + name);
    sunsetSeries = new TimeSeries("Sunset " + name);
  }

  /**
   * Add a sunrise and sunset point to the band.
   * 
   * @param sunrise
   *        Sunrise time
   * @param sunset
   *        Sunset time
   */
  public void add(final LocalDateTime sunrise, final LocalDateTime sunset)
  {
    sunriseSeries.add(day(sunrise), time(sunrise));
    sunsetSeries.add(day(sunset), time(sunset));
  }

  /**
   * Get the band as a collection.
   * 
   * @return Time series collection
   */
  public TimeSeriesCollection getTimeSeriesCollection()
  {
    final TimeSeriesCollection band = new TimeSeriesCollection();
    band.addSeries(sunriseSeries);
    band.addSeries(sunsetSeries);
    return band;
  }

  /**
   * A utility method for creating a value based on a date.
   * 
   * @param dateTime
   *        Date time
   */
  private Day day(final LocalDateTime dateTime)
  {
    return new Day(dateTime.getDayOfMonth(),
                   dateTime.getMonthOfYear(),
                   dateTime.getYear());
  }

  /**
   * A utility method for creating a value based on a time.
   * 
   * @param dateTime
   *        Date time
   */
  private long time(final LocalDateTime dateTime)
  {
    final Minute m = new Minute(dateTime.getMinuteOfHour(), dateTime
      .getHourOfDay(), 1, 1, 1970);
    return m.getFirstMillisecond();
  }

}
