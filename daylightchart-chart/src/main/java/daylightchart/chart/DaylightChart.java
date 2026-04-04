/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart;

import daylightchart.chart.data.DaylightBand;
import daylightchart.chart.data.RiseSetUtility;
import daylightchart.chart.data.RiseSetYearData;
import daylightchart.chart.options.ChartOptions;
import daylightchart.chart.options.ChartOptionsListener;
import daylightchart.chart.options.ChartOptionsService;
import daylightchart.options.ChartOrientation;
import daylightchart.options.Options;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.Serial;
import java.time.Year;
import java.time.ZoneId;
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
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;

/** Produces a chart of daylight times for any location. */
public class DaylightChart extends JFreeChart implements ChartOptionsListener {

  @Serial private static final long serialVersionUID = 1223227216177061127L;

  private static final Logger LOGGER = Logger.getLogger(DaylightChart.class.getName());
  private static final ChartOptionsService CHART_OPTIONS_SERVICE = new ChartOptionsService();

  private final RiseSetYearData riseSetData;

  /** Create an empty chart, just to get the default chart options. */
  public DaylightChart() {
    this(
        RiseSetUtility.createRiseSetYear(null, Year.now().getValue(), new Options()),
        new Options(),
        null);
    setTitle("");
  }

  /**
   * Instantiate the chart for a given location, and given year.
   *
   * @param riseSetData Rise and set data for the year
   * @param options Options
   */
  public DaylightChart(
      final RiseSetYearData riseSetData, final Options options, final ChartOptions chartOptions) {
    super(new XYPlot());
    this.riseSetData = riseSetData;
    createChart(options, chartOptions);
    if (chartOptions != null) {
      CHART_OPTIONS_SERVICE.applyChartOptions(chartOptions, this);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see daylightchart.chart.options.ChartOptionsListener#afterSettingChartOptions(ChartOptions)
   */
  @Override
  public void afterSettingChartOptions(final ChartOptions chartOptions) {
    Font titleFont;
    final TextTitle title = getTitle();
    if (title != null) {
      titleFont = title.getFont();
    } else {
      titleFont = ChartConfiguration.chartFont;
    }
    createTitles(chartOptions, titleFont.deriveFont(Font.BOLD, 18));
  }

  /**
   * {@inheritDoc}
   *
   * @see daylightchart.chart.options.ChartOptionsListener#beforeSettingChartOptions(ChartOptions)
   */
  @Override
  public void beforeSettingChartOptions(final ChartOptions chartOptions) {
    // No-op
  }

  private void adjustForChartOrientation(final ChartOrientation chartOrientation) {
    if (chartOrientation == null) {
      return;
    }

    final XYPlot plot = getXYPlot();
    final ValueAxis hoursAxis = plot.getRangeAxis();
    final ValueAxis monthsAxis = plot.getDomainAxis();

    switch (chartOrientation) {
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

  /** Creates bands for the sunrise and sunset times for the whole year. */
  private void createBandsInPlot(final XYPlot plot) {
    final List<DaylightBand> bands = riseSetData.getBands();
    for (final DaylightBand band : bands) {
      final DaylightChartBand chartBand = new DaylightChartBand(band);
      LOGGER.log(Level.FINE, band.toString());
      final int currentDatasetNumber = plot.getDatasetCount();
      plot.setDataset(currentDatasetNumber, chartBand.getTimeSeriesCollection());
      plot.setRenderer(currentDatasetNumber, chartBand.getRenderer());
    }
  }

  /** Creates the daylight chart. */
  private void createChart(final Options options, final ChartOptions chartOptions) {

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
    if (riseSetData.usesDaylightTime()) {
      createDSTMarker(plot);
    }

    createBandsInPlot(plot);

    ChartOrientation chartOrientation = ChartOrientation.STANDARD;
    if (options != null) {
      chartOrientation = options.getChartOrientation();
    }
    adjustForChartOrientation(chartOrientation);

    final Options optionsNotNull;
    if (options == null) {
      optionsNotNull = new Options();
    } else {
      optionsNotNull = options;
    }
    createTitles(
        chartOptions == null ? new ChartOptions() : chartOptions,
        ChartConfiguration.chartFont.deriveFont(Font.BOLD, 18));
    createLegend(
        optionsNotNull, chartOptions, ChartConfiguration.chartFont.deriveFont(Font.PLAIN, 12));
  }

  private void createDSTMarker(final XYPlot plot) {
    if (!riseSetData.usesDaylightTime()) {
      return;
    }

    final long intervalStart =
        riseSetData
            .getDstStartDate()
            .atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();
    final long intervalEnd =
        riseSetData
            .getDstEndDate()
            .atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();
    final IntervalMarker dstMarker =
        new IntervalMarker(
            intervalStart,
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
  private void createHoursAxis(final XYPlot plot) {
    final DateAxis axis = new DateAxis();
    axis.setLowerMargin(0.0f);
    axis.setUpperMargin(0.0f);
    axis.setTickLabelFont(ChartConfiguration.chartFont.deriveFont(Font.PLAIN, 12));
    // Fix the axis range for all the hours in the day
    axis.setRange(new Date(70, 0, 1), new Date(70, 0, 2));
    //
    plot.setRangeAxis(axis);
  }

  private void createLegend(
      final Options options, final ChartOptions chartOptions, final Font font) {
    removeLegend();

    if (options.isShowChartLegend()) {
      final LegendItemSource legendItemSource =
          new DaylightChartLegendItemSource(options, chartOptions);
      final LegendTitle legendTitle = new LegendTitle(legendItemSource);
      legendTitle.setItemFont(font);
      legendTitle.setPosition(RectangleEdge.BOTTOM);
      addLegend(legendTitle);
    }
  }

  @SuppressWarnings("deprecation")
  private void createMonthsAxis(final XYPlot plot) {
    final DateAxis axis = new DateAxis();
    axis.setTickMarkPosition(DateTickMarkPosition.START);
    axis.setLowerMargin(0.0f);
    axis.setUpperMargin(0.0f);
    axis.setTickLabelFont(ChartConfiguration.chartFont.deriveFont(Font.PLAIN, 12));
    axis.setDateFormatOverride(ChartConfiguration.monthsFormat);
    axis.setVerticalTickLabels(true);
    axis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1), true, true);
    // Fix the axis range for all the months in the year
    final int dateYear = riseSetData.getYear() - 1900;
    axis.setRange(new Date(dateYear, 0, 1), new Date(dateYear, 11, 31));
    //
    plot.setDomainAxis(axis);
  }

  private void createTitles(final ChartOptions chartOptions, final Font titleFont) {

    // Clear all titles and subtitles
    setTitle((TextTitle) null);
    for (final Title subtitle : getSubtitles()) {
      if (subtitle instanceof TextTitle) {
        removeSubtitle(subtitle);
      }
    }

    // Build new titles and legend
    final Location location = riseSetData.getLocation();
    final boolean showTitle = chartOptions.getTitleOptions().getShowTitle();
    if (location != null && showTitle) {
      final TextTitle title = new TextTitle(location.toString(), titleFont);
      setTitle(title);

      final Font subtitleFont = titleFont.deriveFont(Font.PLAIN);
      final TextTitle subtitle = new TextTitle(location.getDetails(), subtitleFont);
      subtitle.setPaint(title.getPaint());
      addSubtitle(subtitle);
    }
  }
}
