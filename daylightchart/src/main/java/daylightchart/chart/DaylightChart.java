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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
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

import daylightchart.location.Location;
import daylightchart.options.Options;
import daylightchart.options.chart.ChartOptions;
import daylightchart.options.chart.ChartOptionsListener;

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
  private static final Font chartFont = new Font("Helvetica", Font.PLAIN, 12);

  private final RiseSetYear riseSetData;

  /**
   * Create an empty chart, just to get the default chart options.
   */
  public DaylightChart()
  {
    this(null, Calendar.getInstance().get(Calendar.YEAR), new Options());
    setTitle("");
  }

  /**
   * Instantiate the chart for a given location, and given year.
   * 
   * @param location
   *        Location
   * @param year
   *        Year
   * @param options
   *        Options
   */
  public DaylightChart(final Location location,
                       final int year,
                       final Options options)
  {
    super(new XYPlot());

    TimeZoneOption timeZoneOption = TimeZoneOption.USE_TIME_ZONE;
    ChartOrientation chartOrientation = ChartOrientation.standard;
    if (options != null)
    {
      timeZoneOption = options.getTimeZoneOption();
      chartOrientation = options.getChartOrientation();
    }
    // Calculate rise and set timings for the whole year
    riseSetData = RiseSetFactory.createRiseSetYear(location,
                                                   year,
                                                   timeZoneOption);
    createChart(chartOrientation);
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.chart.ChartOptionsListener#afterSettingChartOptions(ChartOptions)
   */
  public void afterSettingChartOptions(final ChartOptions chartOptions)
  {
    // Fix title and subtitles
    if (chartOptions.getTitleOptions().getShowTitle())
    {
      setTitle(riseSetData.getLocation().toString());
      final TextTitle title = getTitle();
      Font subtitleFont = title.getFont();
      subtitleFont = subtitleFont.deriveFont(Font.PLAIN);
      final TextTitle subtitle = (TextTitle) getSubtitle(0);
      subtitle.setFont(subtitleFont);
      subtitle.setPaint(title.getPaint());
    }
    else
    {
      setTitle((TextTitle) null);
      clearSubtitles();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.chart.ChartOptionsListener#beforeSettingChartOptions(ChartOptions)
   */
  public void beforeSettingChartOptions(@SuppressWarnings("unused")
  final ChartOptions chartOptions)
  {
    // No-op
  }

  private void adjustForChartOrientation(final ChartOrientation chartOrientation)
  {
    if (chartOrientation == null)
    {
      return;
    }

    final XYPlot plot = getXYPlot();
    final ValueAxis hoursAxis = plot.getRangeAxis();
    final ValueAxis monthsAxis = plot.getDomainAxis();

    switch (chartOrientation)
    {
      case standard:
        hoursAxis.setInverted(true);
        plot.setDomainAxisLocation(AxisLocation.TOP_OR_LEFT);
        break;
      case conventional:
        break;
      case vertical:
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        monthsAxis.setInverted(true);
        break;
      default:
        break;
    }
  }

  /**
   * Creates the daylight chart.
   */
  private void createChart(final ChartOrientation chartOrientation)
  {

    setBackgroundPaint(Color.white);

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

    List<TimeSeries> timeSeries;

    timeSeries = createTimeSeries();
    int numberOfBands = timeSeries.size() / 2;
    for (int band = 0; band < (numberOfBands - 1); band++)
    {
      TimeSeriesCollection seriesBand = new TimeSeriesCollection();
      seriesBand.addSeries(timeSeries.get(band * 2));
      seriesBand.addSeries(timeSeries.get(band * 2 + 1));

      plot.setDataset(band, seriesBand);
      plot.setRenderer(band, createDifferenceRenderer());
    }

    // Create outline plot
    riseSetData.setUsesDaylightTime(false);
    final int datasetNumber = plot.getSeriesCount();
    timeSeries = createTimeSeries();
    TimeSeriesCollection seriesBand = new TimeSeriesCollection();
    seriesBand.addSeries(timeSeries.get(0));
    seriesBand.addSeries(timeSeries.get(1));
    plot.setDataset(datasetNumber, seriesBand);
    plot.setRenderer(datasetNumber, createOutlineRenderer());

    adjustForChartOrientation(chartOrientation);

    createTitles();
  }

  private XYItemRenderer createDifferenceRenderer()
  {
    XYItemRenderer renderer;
    renderer = new XYDifferenceRenderer(daylightColor, daylightColor, false);
    renderer.setBaseStroke(new BasicStroke(0.2f));
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
    dstMarker.setLabelPaint(Color.WHITE);
    dstMarker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
    dstMarker.setLabelTextAnchor(TextAnchor.BASELINE_RIGHT);
    //
    plot.addDomainMarker(dstMarker, Layer.BACKGROUND);
  }

  @SuppressWarnings("deprecation")
  private void createHoursAxis(final XYPlot plot)
  {
    final DateAxis axis = new DateAxis();
    axis.setLowerMargin(0.0f);
    axis.setUpperMargin(0.0f);
    axis.setTickLabelFont(chartFont.deriveFont(Font.PLAIN, 12));
    // Fix the axis range for all the hours in the day
    axis.setRange(new Date(70, 0, 1), new Date(70, 0, 2));
    //
    plot.setRangeAxis(axis);
  }

  private void createMonthsAxis(final XYPlot plot)
  {
    final DateAxis axis = new DateAxis();
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setLowerMargin(0.0f);
    axis.setUpperMargin(0.0f);
    axis.setTickLabelFont(chartFont.deriveFont(Font.PLAIN, 12));
    axis.setDateFormatOverride(new SimpleDateFormat("MMM"));
    axis.setVerticalTickLabels(true);
    axis.setTickUnit(new DateTickUnit(DateTickUnit.MONTH, 1), true, true);
    //
    plot.setDomainAxis(axis);
  }

  private XYItemRenderer createOutlineRenderer()
  {
    XYItemRenderer renderer;
    renderer = new XYLineAndShapeRenderer(true, false);
    renderer.setBaseStroke(new BasicStroke(0.6f));
    renderer.setSeriesPaint(0, Color.WHITE);
    renderer.setSeriesPaint(1, Color.WHITE);
    return renderer;
  }

  /**
   * Creates a data-set for the sunrise and sunset times for the whole
   * year.
   * 
   * @return A data-set for the sunrise and sunset times.
   */
  private List<TimeSeries> createTimeSeries()
  {
    final TimeSeries sunriseSeries = new TimeSeries("Sunrise");
    final TimeSeries sunsetSeries = new TimeSeries("Sunset");
    TimeSeries sunriseWrapSeries = null;
    TimeSeries sunsetWrapSeries = null;

    final List<TimeSeries> timeseries = new ArrayList<TimeSeries>();
    timeseries.add(sunriseSeries);
    timeseries.add(sunsetSeries);

    for (final RiseSet riseSet: riseSetData.getRiseSets())
    {
      final LocalDateTime sunrise = riseSet.getSunrise();
      final LocalDateTime sunset = riseSet.getSunset();
      if (sunset.getHourOfDay() < 12)
      {
        if (sunriseWrapSeries == null && sunsetWrapSeries == null)
        {
          sunriseWrapSeries = new TimeSeries("Sunrise (Wrap)");
          sunsetWrapSeries = new TimeSeries("Sunset (Wrap)");
        }

        final LocalDateTime beforeMidnight = new LocalDateTime(sunrise
                                                                 .getYear(),
                                                               sunrise
                                                                 .getMonthOfYear(),
                                                               sunrise
                                                                 .getDayOfMonth(),
                                                               23,
                                                               59,
                                                               59);
        final LocalDateTime afterMidnight = new LocalDateTime(sunrise.getYear(),
                                                              sunrise
                                                                .getMonthOfYear(),
                                                              sunrise
                                                                .getDayOfMonth(),
                                                              0,
                                                              1,
                                                              1);

        // Split the daylight hours across two series
        sunriseSeries.add(day(sunrise), time(sunrise));
        sunsetSeries.add(day(beforeMidnight), time(beforeMidnight));

        sunriseWrapSeries.add(day(afterMidnight), time(afterMidnight));
        sunsetWrapSeries.add(day(sunset), time(sunset));
      }
      else
      {
        // End the wrap series, if necessary
        if (sunriseWrapSeries != null && sunsetWrapSeries != null)
        {
          timeseries.add(sunriseWrapSeries);
          timeseries.add(sunsetWrapSeries);

          sunriseWrapSeries = null;
          sunsetWrapSeries = null;
        }

        // Add sunset and sunrise as usual
        sunriseSeries.add(day(sunrise), time(sunrise));
        sunsetSeries.add(day(sunset), time(sunset));
      }
    }

    return timeseries;
  }

  private void createTitles()
  {
    TextTitle title;

    final Location location = riseSetData.getLocation();
    if (location != null)
    {
      title = new TextTitle(location.toString(), chartFont
        .deriveFont(Font.BOLD, 18));
      setTitle(title);

      clearSubtitles();
      title = new TextTitle(location.getDetails(), chartFont
        .deriveFont(Font.PLAIN, 18));
      addSubtitle(title);
    }
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
