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
package daylightchart.options;


import java.io.Serializable;

import daylightchart.chart.TimeZoneOption;
import daylightchart.location.LocationsSortOrder;
import daylightchart.options.chart.ChartOptions;

/**
 * All Daylight Chart options.
 * 
 * @author Sualeh Fatehi
 */
public class Options
  implements Serializable
{

  private static final long serialVersionUID = 3195704386171200909L;

  private LocationsSortOrder locationsSortOrder;
  private TimeZoneOption timeZoneOption;
  private ChartOptions chartOptions;

  /**
   * Default options.
   */
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
