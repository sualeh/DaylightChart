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
package daylightchart.chart;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.joda.time.LocalDateTime;

import daylightchart.gui.options.ChartOptions;
import daylightchart.gui.options.ChartOptionsListener;
import daylightchart.locationparser.LocationFormatter;

/**
 * Produces a chart of daylight times for any location.
 * 
 * @author Sualeh Fatehi
 */
public class DaylightChart
  extends JFreeChart
  implements ChartOptionsListener
{

  private static final long serialVersionUID = 1223227216177061127L;

  private static final Color daylightColor = new Color(0xFF, 0xFF, 0x40, 190);
  private static final Color nightColor = new Color(75, 11, 91, 190);
  private static final Stroke outlineStroke = new BasicStroke(0.2f);

  private final RiseSetYear riseSetData;

  /**
   * Instantiate the chart for a given location, and given year.
   * 
   * @param location
   */
  public DaylightChart(final Location location)
  {
    this(location, Calendar.getInstance().get(Calendar.YEAR));
  }

  /**
   * Instantiate the chart for a given location, and given year.
   * 
   * @param location
   * @param year
   */
  public DaylightChart(final Location location, final int year)
  {
    super(new XYPlot());
    // Calculate rise and set timings for the whole year
    riseSetData = RiseSetFactory.createRiseSetYear(location, year);
    createChart();
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.gui.options.ChartOptionsListener#afterSettingChartOptions(ChartOptions)
   */
  public void afterSettingChartOptions(final ChartOptions chartOptions)
  {
    // Fix title and subtitles
    setTitle(riseSetData.getLocation().toString());
    final TextTitle title = getTitle();
    Font subtitleFont = title.getFont();
    subtitleFont = subtitleFont.deriveFont(Font.PLAIN);
    final TextTitle subtitle = (TextTitle) getSubtitle(0);
    subtitle.setFont(subtitleFont);
    subtitle.setPaint(title.getPaint());
    // Fix font on the domain marker
    final Collection<IntervalMarker> domainMarkers = getXYPlot()
      .getDomainMarkers(Layer.BACKGROUND);
    if (domainMarkers != null && domainMarkers.size() > 0)
    {
      final List<IntervalMarker> domainMarkersList = new ArrayList<IntervalMarker>();
      domainMarkersList.addAll(domainMarkers);
      final IntervalMarker dstMarker = domainMarkersList.get(0);
      final Font labelFont = chartOptions.getPlotOptions()
        .getDomainAxisOptions().getLabelFont();
      dstMarker.setLabelFont(labelFont);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.gui.options.ChartOptionsListener#beforeSettingChartOptions(ChartOptions)
   */
  public void beforeSettingChartOptions(final ChartOptions chartOptions)
  {
    // No-op
  }

  /**
   * Creates the daylight chart.
   */
  private void createChart()
  {
    final XYPlot plot = getXYPlot();

    plot.setBackgroundPaint(nightColor);
    plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));

    createMonthsAxis(plot);
    createHoursAxis(plot);

    // Create a marker region for daylight savings time
    if (riseSetData.usesDaylightTime())
    {
      createDSTMarker(plot);
    }

    plot.setDataset(0, createTimeSeries());
    plot.setRenderer(0, createDifferenceRenderer());

    riseSetData.setUsesDaylightTime(false);
    plot.setDataset(1, createTimeSeries());
    plot.setRenderer(1, createOutlineRenderer());

    createTitles();

    setBackgroundPaint(Color.WHITE);
  }

  private XYItemRenderer createDifferenceRenderer()
  {
    XYItemRenderer renderer;
    renderer = new XYDifferenceRenderer(daylightColor, daylightColor, false);
    renderer.setStroke(outlineStroke);
    renderer.setSeriesPaint(0, Color.WHITE);
    renderer.setSeriesPaint(1, Color.WHITE);
    return renderer;
  }

  private void createDSTMarker(final XYPlot plot)
  {
    final long intervalStart = new Day(riseSetData.getDstStartDate())
      .getFirstMillisecond();
    final long intervalEnd = new Day(riseSetData.getDstEndDate())
      .getFirstMillisecond();
    final IntervalMarker dstMarker = new IntervalMarker(intervalStart,
                                                        intervalEnd,
                                                        nightColor,
                                                        new BasicStroke(0.0f),
                                                        null,
                                                        null,
                                                        0.4f);
    dstMarker.setLabel(Messages.getString("DaylightChart.Label.Marker")); //$NON-NLS-1$
    dstMarker.setLabelPaint(Color.WHITE);
    dstMarker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
    dstMarker.setLabelFont(new Font("SansSerif", Font.BOLD, 12)); //$NON-NLS-1$
    dstMarker.setLabelTextAnchor(TextAnchor.BASELINE_RIGHT);
    //
    plot.addDomainMarker(dstMarker, Layer.BACKGROUND);
  }

  private void createHoursAxis(final XYPlot plot)
  {
    final DateAxis hoursAxis = new DateAxis();
    hoursAxis.setInverted(true);
    hoursAxis.setLowerMargin(0.0);
    hoursAxis.setUpperMargin(0.0);
    // Fix the axis range for all the hours in the day
    hoursAxis.setRange(new Date(70, 0, 1), new Date(70, 0, 2));
    //
    plot.setRangeAxis(hoursAxis);
  }

  private void createMonthsAxis(final XYPlot plot)
  {
    final DateAxis monthsAxis = new DateAxis();
    monthsAxis.setTickMarkPosition(DateTickMarkPosition.START);
    monthsAxis.setLowerMargin(0.02);
    monthsAxis.setUpperMargin(0.02);
    monthsAxis.setDateFormatOverride(new SimpleDateFormat("MMM")); //$NON-NLS-1$
    monthsAxis.setTickUnit(new DateTickUnit(DateTickUnit.MONTH, 1), true, true);
    //
    plot.setDomainAxis(monthsAxis);
    plot.setDomainAxisLocation(AxisLocation.TOP_OR_LEFT);
  }

  private XYItemRenderer createOutlineRenderer()
  {
    XYItemRenderer renderer;
    renderer = new XYLineAndShapeRenderer(true, false);
    renderer.setStroke(outlineStroke);
    renderer.setSeriesPaint(0, Color.WHITE);
    renderer.setSeriesPaint(1, Color.WHITE);
    return renderer;
  }

  /**
   * Creates a dataset for the sunrise and sunset times for the whole
   * year.
   * 
   * @return A dataset for the sunrise and sunset times.
   */
  private TimeSeriesCollection createTimeSeries()
  {
    final TimeSeries sunriseSeries = new TimeSeries(Messages
      .getString("DaylightChart.Label.Sunrise")); //$NON-NLS-1$
    final TimeSeries sunsetSeries = new TimeSeries(Messages
      .getString("DaylightChart.Label.Sunset")); //$NON-NLS-1$
    for (final RiseSet riseSet: riseSetData.getRiseSets())
    {
      final LocalDateTime sunrise = riseSet.getSunrise();
      sunriseSeries.add(day(sunrise), time(sunrise));
      final LocalDateTime sunset = riseSet.getSunset();
      sunsetSeries.add(day(sunset), time(sunset));
    }

    final TimeSeriesCollection timeseries = new TimeSeriesCollection();
    timeseries.addSeries(sunriseSeries);
    timeseries.addSeries(sunsetSeries);
    return timeseries;
  }

  private void createTitles()
  {
    TextTitle title;

    title = new TextTitle(riseSetData.getLocation().toString(),
                          new Font("SansSerif", //$NON-NLS-1$
                                   Font.BOLD,
                                   14));
    setTitle(title);

    clearSubtitles();
    title = new TextTitle(LocationFormatter.printLocationDetails(riseSetData
      .getLocation()), new Font("SansSerif", Font.PLAIN, 12)); //$NON-NLS-1$
    addSubtitle(title);
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
  private Long time(final LocalDateTime dateTime)
  {
    final Minute m = new Minute(dateTime.getMinuteOfHour(), dateTime
      .getHourOfDay(), 1, 1, 1970);
    return new Long(m.getFirstMillisecond());
  }

}
