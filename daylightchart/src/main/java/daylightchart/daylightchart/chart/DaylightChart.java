/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2010, Sualeh Fatehi.
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
package daylightchart.daylightchart.chart;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoname.data.Location;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.time.Day;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import daylightchart.daylightchart.calculation.DaylightBand;
import daylightchart.daylightchart.calculation.RiseSetUtility;
import daylightchart.daylightchart.calculation.RiseSetYearData;
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

  private static final Logger LOGGER = Logger.getLogger(DaylightChart.class
    .getName());

  private final RiseSetYearData riseSetData;

  /**
   * Create an empty chart, just to get the default chart options.
   */
  public DaylightChart()
  {
    this(RiseSetUtility.createRiseSetYear(null, Calendar.getInstance()
      .get(Calendar.YEAR), new Options()), new Options());
    setTitle("");
  }

  /**
   * Instantiate the chart for a given location, and given year.
   * 
   * @param riseSetData
   *        Rise and set data for the year
   * @param options
   *        Options
   */
  public DaylightChart(final RiseSetYearData riseSetData, final Options options)
  {
    super(new XYPlot());
    this.riseSetData = riseSetData;
    createChart(options);
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.chart.ChartOptionsListener#afterSettingChartOptions(ChartOptions)
   */
  public void afterSettingChartOptions(final ChartOptions chartOptions)
  {
    Font titleFont;
    final TextTitle title = getTitle();
    if (title != null)
    {
      titleFont = title.getFont();
    }
    else
    {
      titleFont = ChartConfiguration.chartFont;
    }
    createTitles(chartOptions, titleFont.deriveFont(Font.BOLD, 18));
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.chart.ChartOptionsListener#beforeSettingChartOptions(ChartOptions)
   */
  public void beforeSettingChartOptions(final ChartOptions chartOptions)
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
      case STANDARD:
        hoursAxis.setInverted(true);
        plot.setDomainAxisLocation(AxisLocation.TOP_OR_LEFT);
        break;
      case CONVENTIONAL:
        break;
      case VERTICAL:
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        monthsAxis.setInverted(true);
        break;
      default:
        break;
    }
  }

  /**
   * Creates bands for the sunrise and sunset times for the whole year.
   */
  private void createBandsInPlot(final XYPlot plot)
  {
    final List<DaylightBand> bands = riseSetData.getBands();
    for (final DaylightBand band: bands)
    {
      final DaylightChartBand chartBand = new DaylightChartBand(band);
      LOGGER.log(Level.FINE, band.toString());
      final int currentDatasetNumber = plot.getDatasetCount();
      plot
        .setDataset(currentDatasetNumber, chartBand.getTimeSeriesCollection());
      plot.setRenderer(currentDatasetNumber, chartBand.getRenderer());
    }
  }

  /**
   * Creates the daylight chart.
   */
  private void createChart(final Options options)
  {

    setBackgroundPaint(Color.white);

    final XYPlot plot = getXYPlot();

    // Set the first renderer, so that the grid lines can be shown
    plot.setRenderer(new StandardXYItemRenderer());
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);

    plot.setBackgroundPaint(ChartConfiguration.nightColor);

    plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
    createMonthsAxis(plot);
    createHoursAxis(plot);

    // Create a marker region for daylight savings time
    if (riseSetData.usesDaylightTime())
    {
      createDSTMarker(plot);
    }

    createBandsInPlot(plot);

    ChartOrientation chartOrientation = ChartOrientation.STANDARD;
    if (options != null)
    {
      chartOrientation = options.getChartOrientation();
    }
    adjustForChartOrientation(chartOrientation);

    final Options optionsNotNull;
    if (options == null)
    {
      optionsNotNull = new Options();
    }
    else
    {
      optionsNotNull = options;
    }
    createTitles(optionsNotNull.getChartOptions(), ChartConfiguration.chartFont
      .deriveFont(Font.BOLD, 18));

    createLegend(optionsNotNull, ChartConfiguration.chartFont
      .deriveFont(Font.PLAIN, 12));

  }

  private void createDSTMarker(final XYPlot plot)
  {
    final long intervalStart = new Day(riseSetData.getDstStartDate())
      .getFirstMillisecond();
    final long intervalEnd = new Day(riseSetData.getDstEndDate())
      .getFirstMillisecond();
    final IntervalMarker dstMarker = new IntervalMarker(intervalStart,
                                                        intervalEnd,
                                                        ChartConfiguration.nightColor,
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
    axis.setTickLabelFont(ChartConfiguration.chartFont.deriveFont(Font.PLAIN,
                                                                  12));
    // Fix the axis range for all the hours in the day
    axis.setRange(new Date(70, 0, 1), new Date(70, 0, 2));
    //
    plot.setRangeAxis(axis);
  }

  private void createLegend(final Options options, final Font font)
  {
    removeLegend();

    if (options.isShowChartLegend())
    {
      final LegendItemSource legendItemSource = new DaylightChartLegendItemSource(options);
      final LegendTitle legendTitle = new LegendTitle(legendItemSource);
      legendTitle.setItemFont(font);
      legendTitle.setPosition(RectangleEdge.BOTTOM);
      addLegend(legendTitle);
    }
  }

  @SuppressWarnings("deprecation")
  private void createMonthsAxis(final XYPlot plot)
  {
    final DateAxis axis = new DateAxis();
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setLowerMargin(0.0f);
    axis.setUpperMargin(0.0f);
    axis.setTickLabelFont(ChartConfiguration.chartFont.deriveFont(Font.PLAIN,
                                                                  12));
    axis.setDateFormatOverride(ChartConfiguration.monthsFormat);
    axis.setVerticalTickLabels(true);
    axis.setTickUnit(new DateTickUnit(DateTickUnit.MONTH, 1), true, true);
    // Fix the axis range for all the months in the year
    final int dateYear = riseSetData.getYear() - 1900;
    axis.setRange(new Date(dateYear, 0, 1), new Date(dateYear, 11, 31));
    //
    plot.setDomainAxis(axis);
  }

  @SuppressWarnings("unchecked")
  private void createTitles(final ChartOptions chartOptions,
                            final Font titleFont)
  {

    // Clear all titles and subtitles
    setTitle((TextTitle) null);
    for (final Title subtitle: (List<Title>) getSubtitles())
    {
      if (subtitle instanceof TextTitle)
      {
        removeSubtitle(subtitle);
      }
    }

    // Build new titles and legend
    final Location location = riseSetData.getLocation();
    final boolean showTitle = chartOptions.getTitleOptions().getShowTitle();
    if (location != null && showTitle)
    {
      final TextTitle title = new TextTitle(location.toString(), titleFont);
      setTitle(title);

      final Font subtitleFont = titleFont.deriveFont(Font.PLAIN);
      final TextTitle subtitle = new TextTitle(location.getDetails(),
                                               subtitleFont);
      subtitle.setPaint(title.getPaint());
      addSubtitle(subtitle);
    }
  }

}
