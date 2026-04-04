/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart;

import daylightchart.chart.data.DaylightBandType;
import daylightchart.chart.options.ChartOptions;
import daylightchart.options.Options;
import daylightchart.options.TwilightType;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.io.Serial;
import java.io.Serializable;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;

final class DaylightChartLegendItemSource implements LegendItemSource, Serializable {

  /** */
  @Serial private static final long serialVersionUID = -7877379059709945565L;

  private final Options options;
  private final ChartOptions chartOptions;

  DaylightChartLegendItemSource(final Options options, final ChartOptions chartOptions) {
    this.options = options;
    this.chartOptions = chartOptions;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.jfree.chart.LegendItemSource#getLegendItems()
   */
  @Override
  public LegendItemCollection getLegendItems() {
    final LegendItemCollection legendItemCollection = new LegendItemCollection();

    final Paint configuredNightColor =
        chartOptions != null
                && chartOptions.getPlotOptions() != null
                && chartOptions.getPlotOptions().getBackgroundPaint() != null
            ? chartOptions.getPlotOptions().getBackgroundPaint()
            : ChartConfiguration.nightColor;
    legendItemCollection.add(
        createLegendItem(resolveLegendLabel(null), configuredNightColor, false));

    for (final DaylightBandType daylightSavingsMode : DaylightBandType.values()) {
      if (daylightSavingsMode == DaylightBandType.twilight
          && options.getTwilightType() == TwilightType.NO) {
        continue;
      }
      legendItemCollection.add(getLegendItem(daylightSavingsMode));
    }

    return legendItemCollection;
  }

  private LegendItem createLegendItem(final String label, final Paint paint, final boolean isLine) {
    return new LegendItem(
        label, /* description */
        null, /* toolTipText */
        null, /* urlText */
        null, /* shapeVisible */
        !isLine, /* shape */
        new Rectangle(10, 10), /* shapeFilled */
        true,
        paint,
        /* shapeOutlineVisible */ true, /* outlinePaint */
        Color.black,
        /* outlineStroke */ new BasicStroke(0.2f), /* lineVisible */
        isLine, /* line */
        new Rectangle(10, 3),
        /* lineStroke */ new BasicStroke(0.6f), /* linePaint */
        Color.black);
  }

  private LegendItem getLegendItem(final DaylightBandType daylightSavingsMode) {
    final String legendLabel = resolveLegendLabel(daylightSavingsMode);
    return switch (daylightSavingsMode) {
      case with_clock_shift ->
          createLegendItem(legendLabel, ChartConfiguration.daylightColor, false);
      case without_clock_shift -> createLegendItem(legendLabel, Color.white, true);
      case twilight -> createLegendItem(legendLabel, ChartConfiguration.twilightColor, false);
      default -> null;
    };
  }

  private String resolveLegendLabel(final DaylightBandType daylightSavingsMode) {

    String legendLabel;
    if (daylightSavingsMode == null) {
      legendLabel = Messages.getString("DaylightChart.Legend.Night"); // $NON-NLS-1$
    } else {
      switch (daylightSavingsMode) {
        case with_clock_shift:
          legendLabel = Messages.getString("DaylightChart.Legend.Daylight"); // $NON-NLS-1$
          break;
        case without_clock_shift:
          legendLabel = Messages.getString("DaylightChart.Legend.WithoutDST"); // $NON-NLS-1$
          break;
        case twilight:
          final TwilightType twilight = options.getTwilightType();
          legendLabel =
              switch (twilight) {
                case CIVIL ->
                    Messages.getString("DaylightChart.Legend.Twilight.Civil"); // $NON-NLS-1$
                case NAUTICAL ->
                    Messages.getString("DaylightChart.Legend.Twilight.Nautical"); // $NON-NLS-1$
                case ASTRONOMICAL ->
                    Messages.getString("DaylightChart.Legend.Twilight.Astronomical"); // $NON-NLS-1$
                default -> Messages.getString("DaylightChart.Legend.Twilight"); // $NON-NLS-1$ ;
              };
          break;
        default:
          legendLabel = "";
          break;
      }
    }
    return legendLabel;
  }
}
