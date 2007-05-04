package daylightchart.options;


import java.io.Serializable;

import daylightchart.chart.TimeZoneOption;
import daylightchart.location.LocationsSortOrder;
import daylightchart.options.chart.ChartOptions;

public class Options
  implements Serializable
{

  private static final long serialVersionUID = 3195704386171200909L;

  private LocationsSortOrder locationsSortOrder;
  private TimeZoneOption timeZoneOption;
  private ChartOptions chartOptions;

  public Options()
  {
    locationsSortOrder = LocationsSortOrder.BY_NAME;
    timeZoneOption = TimeZoneOption.USE_TIME_ZONE;
    chartOptions = new ChartOptions();
  }

  /**
   * @return the chartOptions
   */
  public final ChartOptions getChartOptions()
  {
    return chartOptions;
  }

  /**
   * @return the locationsSortOrder
   */
  public final LocationsSortOrder getLocationsSortOrder()
  {
    return locationsSortOrder;
  }

  /**
   * @return the timeZoneOption
   */
  public final TimeZoneOption getTimeZoneOption()
  {
    return timeZoneOption;
  }

  /**
   * @param chartOptions
   *        the chartOptions to set
   */
  public final void setChartOptions(final ChartOptions chartOptions)
  {
    if (chartOptions != null)
    {
      this.chartOptions = chartOptions;
    }
  }

  /**
   * @param locationsSortOrder
   *        the locationsSortOrder to set
   */
  public final void setLocationsSortOrder(final LocationsSortOrder locationsSortOrder)
  {
    if (locationsSortOrder != null)
    {
      this.locationsSortOrder = locationsSortOrder;
    }
  }

  /**
   * @param timeZoneOption
   *        the timeZoneOption to set
   */
  public final void setTimeZoneOption(final TimeZoneOption timeZoneOption)
  {
    if (timeZoneOption != null)
    {
      this.timeZoneOption = timeZoneOption;
    }
  }

}
