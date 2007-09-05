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
package daylightchart.chart.calculation;


import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.joda.time.LocalDateTime;

import daylightchart.chart.calculation.RiseSet.RiseSetType;

/**
 * One daylight band, consisting of a sunrise series and a sunset
 * series.
 * 
 * @author sfatehi
 */
public final class DaylightBand
{

  private final DaylightSavingsMode daylightSavingsMode;
  private final int bandNumber;
  private final List<RiseSet> riseSets;

  /**
   * Create a new daylight band.
   * 
   * @param name
   *        Name of the band.
   */
  DaylightBand(final DaylightSavingsMode daylightSavingsMode,
               final int bandNumber)
  {
    this.daylightSavingsMode = daylightSavingsMode;
    this.bandNumber = bandNumber;
    riseSets = new ArrayList<RiseSet>();
  }

  public XYItemRenderer getRenderer()
  {
    return daylightSavingsMode.getRenderer();
  }

  /**
   * Get the band as a collection.
   * 
   * @return Time series collection
   */
  public TimeSeriesCollection getTimeSeriesCollection()
  {
    final String name = getName();
    final TimeSeries sunriseSeries = new TimeSeries("Sunrise " + name);
    final TimeSeries sunsetSeries = new TimeSeries("Sunset " + name);
    for (final RiseSet riseSet: riseSets)
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

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append(getName()).append("\n");
    for (final RiseSet riseSet: riseSets)
    {
      buffer.append("  ").append(riseSet).append("\n");
    }
    return buffer.toString();
  }

  /**
   * Add a sunrise and sunset point to the band.
   * 
   * @param riseSet
   *        Sunrise and sunset time
   */
  void add(final RiseSet riseSet)
  {
    if (riseSet != null && riseSet.getSunrise().isBefore(riseSet.getSunset())
        && riseSet.getRiseSetType() != RiseSetType.all_nighttime)
    {
      riseSets.add(riseSet);
    }
  }

  private String getName()
  {
    return daylightSavingsMode + ", #" + bandNumber;
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
