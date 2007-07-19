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
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

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

  private enum DaylightSavingsMode
  {
    WITH_CLOCK_SHIFT("With Clock Shift", true),
    WITHOUT_CLOCK_SHIFT("Without Clock Shift", false);

    private final boolean adjustedForDaylightSavings;
    private final String description;

    private DaylightSavingsMode(final String description,
                                final boolean adjustedForDaylightSavings)
    {
      this.description = description;
      this.adjustedForDaylightSavings = adjustedForDaylightSavings;
    }

    String getDescription()
    {
      return description;
    }

    XYItemRenderer getRenderer()
    {
      XYItemRenderer renderer;
      switch (this)
      {
        case WITH_CLOCK_SHIFT:
          renderer = createDifferenceRenderer();
          break;
        case WITHOUT_CLOCK_SHIFT:
          renderer = createOutlineRenderer();
          break;
        default:
          renderer = null;
          break;
      }
      return renderer;
    }

    boolean isAdjustedForDaylightSavings()
    {
      return adjustedForDaylightSavings;
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

    private XYItemRenderer createOutlineRenderer()
    {
      XYItemRenderer renderer;
      renderer = new XYLineAndShapeRenderer(true, false);
      renderer.setBaseStroke(new BasicStroke(0.6f));
      renderer.setSeriesPaint(0, Color.WHITE);
      renderer.setSeriesPaint(1, Color.WHITE);
      return renderer;
    }
  }

  private static final long serialVersionUID = 1223227216177061127L;

  private static final Logger LOGGER = Logger.getLogger(DaylightChart.class
    .getName());

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
   * Creates a data-set for the sunrise and sunset times for the whole
   * year.
   * 
   * @return A data-set for the sunrise and sunset times.
   */
  private void createBandsInPlot(final DaylightSavingsMode daylightSavingsMode,
                                 final XYPlot plot)
  {
    final List<DaylightBand> bands = new ArrayList<DaylightBand>();

    final DaylightBand baseBand = new DaylightBand(daylightSavingsMode
      .getDescription()
                                                   + ", #" + bands.size());
    bands.add(baseBand);

    DaylightBand wrapBand = null;

    for (final RiseSet riseSet: riseSetData.getRiseSets(daylightSavingsMode
      .isAdjustedForDaylightSavings()))
    {
      final RiseSet[] riseSets = RiseSet.splitAtMidnight(riseSet);
      if (riseSets.length == 2)
      {
        // Create a new wrap band if necessary
        if (wrapBand == null)
        {
          wrapBand = new DaylightBand(daylightSavingsMode + ", #"
                                      + bands.size());
          bands.add(wrapBand);
        }
        // Split the daylight hours across two series
        baseBand.add(riseSets[0]);
        wrapBand.add(riseSets[1]);
      }
      else if (riseSets.length == 1)
      {
        // End the wrap band, if necessary
        if (wrapBand != null)
        {
          wrapBand = null;
        }

        // Add sunset and sunrise as usual
        baseBand.add(riseSet);
      }
    }

    for (final DaylightBand band: bands)
    {
      LOGGER.log(Level.FINE, band.toString());
      System.out.println(band);
      final int currentDatasetNumber = plot.getDatasetCount();
      plot.setDataset(currentDatasetNumber, band.getTimeSeriesCollection());
      plot.setRenderer(currentDatasetNumber, daylightSavingsMode.getRenderer());
    }
  }

  /**
   * Creates the daylight chart.
   */
  private void createChart(final ChartOrientation chartOrientation)
  {

    createTitles();

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

    // Create daylight plot, clock-shift taken into account
    createBandsInPlot(DaylightSavingsMode.WITH_CLOCK_SHIFT, plot);
    // Create outline plot, without clock shift
    createBandsInPlot(DaylightSavingsMode.WITHOUT_CLOCK_SHIFT, plot);

    adjustForChartOrientation(chartOrientation);

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

}
