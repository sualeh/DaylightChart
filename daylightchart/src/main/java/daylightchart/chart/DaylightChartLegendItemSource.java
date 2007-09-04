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
import java.awt.Paint;
import java.awt.Rectangle;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;

import daylightchart.chart.calculation.DaylightSavingsMode;
import daylightchart.chart.calculation.Twilight;
import daylightchart.options.Options;

final class DaylightChartLegendItemSource
  implements LegendItemSource
{

  private final Options options;

  DaylightChartLegendItemSource(final Options options)
  {
    this.options = options;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.jfree.chart.LegendItemSource#getLegendItems()
   */
  public LegendItemCollection getLegendItems()
  {
    final LegendItemCollection legendItemCollection = new LegendItemCollection();

    final Paint configuredNightColor = options.getChartOptions()
      .getPlotOptions().getBackgroundPaint();
    legendItemCollection.add(createLegendItem(resolveLegendLabel(null),
                                              configuredNightColor,
                                              false));

    for (final DaylightSavingsMode daylightSavingsMode: DaylightSavingsMode
      .values())
    {
      if (daylightSavingsMode == DaylightSavingsMode.twilight
          && options.getTwilight() == Twilight.none)
      {
        continue;
      }
      legendItemCollection.add(getLegendItem(daylightSavingsMode));
    }

    return legendItemCollection;
  }

  private LegendItem createLegendItem(final String label,
                                      final Paint paint,
                                      final boolean isLine)
  {
    final LegendItem legendItem = new LegendItem(label, /* description */
    null,
    /* toolTipText */null, /* urlText */
    null,
    /* shapeVisible */!isLine, /* shape */new Rectangle(10, 10),
    /* shapeFilled */true, paint,
    /* shapeOutlineVisible */true, /* outlinePaint */Color.black,
    /* outlineStroke */new BasicStroke(0.2f),
    /* lineVisible */isLine, /* line */new Rectangle(10, 3),
    /* lineStroke */new BasicStroke(0.6f), /* linePaint */Color.black);

    return legendItem;
  }

  private LegendItem getLegendItem(final DaylightSavingsMode daylightSavingsMode)
  {
    final String legendLabel = resolveLegendLabel(daylightSavingsMode);
    LegendItem legendItem;
    switch (daylightSavingsMode)
    {
      case with_clock_shift:
        legendItem = createLegendItem(legendLabel,
                                      ChartConfiguration.daylightColor,
                                      false);
        break;
      case without_clock_shift:
        legendItem = createLegendItem(legendLabel, Color.white, true);
        break;
      case twilight:
        legendItem = createLegendItem(legendLabel,
                                      ChartConfiguration.twilightColor,
                                      false);
        break;
      default:
        legendItem = null;
        break;
    }
    return legendItem;
  }

  private String resolveLegendLabel(final DaylightSavingsMode daylightSavingsMode)
  {

    String legendLabel;
    if (daylightSavingsMode == null)
    {
      legendLabel = Messages.getString("DaylightChart.Legend.Night"); //$NON-NLS-1$
    }
    else
    {
      switch (daylightSavingsMode)
      {
        case with_clock_shift:
          legendLabel = Messages.getString("DaylightChart.Legend.Daylight"); //$NON-NLS-1$
          break;
        case without_clock_shift:
          legendLabel = Messages.getString("DaylightChart.Legend.WithoutDST"); //$NON-NLS-1$
          break;
        case twilight:
          final Twilight twilight = options.getTwilight();
          switch (twilight)
          {
            case civil:
              legendLabel = Messages
                .getString("DaylightChart.Legend.Twilight.Civil"); //$NON-NLS-1$
              break;
            case nautical:
              legendLabel = Messages
                .getString("DaylightChart.Legend.Twilight.Nautical"); //$NON-NLS-1$
              break;
            case astronomical:
              legendLabel = Messages
                .getString("DaylightChart.Legend.Twilight.Astronomical"); //$NON-NLS-1$
              break;
            default:
              legendLabel = Messages.getString("DaylightChart.Legend.Twilight"); //$NON-NLS-1$;
          }
          break;
        default:
          legendLabel = "";
          break;
      }
    }
    return legendLabel;
  }
}
