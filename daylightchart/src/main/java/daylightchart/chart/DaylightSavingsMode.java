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

import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

enum DaylightSavingsMode
{

  /** With clock shift. */
  with_clock_shift("With clock shift", true),
  /** Without clock shift. */
  without_clock_shift("Without clock shift", false),
  /** Twilight. */
  twilight("Twilight", true);

  private final boolean adjustedForDaylightSavings;
  private final String description;

  private DaylightSavingsMode(final String description,
                              final boolean adjustedForDaylightSavings)
  {
    this.description = description;
    this.adjustedForDaylightSavings = adjustedForDaylightSavings;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString()
  {
    return description;
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
      case with_clock_shift:
        renderer = createDifferenceRenderer(ChartConfiguration.daylightColor);
        break;
      case without_clock_shift:
        renderer = createOutlineRenderer();
        break;
      case twilight:
        renderer = createDifferenceRenderer(ChartConfiguration.twilightColor);
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

  private XYItemRenderer createDifferenceRenderer(final Color color)
  {
    XYItemRenderer renderer;
    renderer = new XYDifferenceRenderer(color, color, false);
    renderer.setBaseStroke(new BasicStroke(0.1f));
    renderer.setSeriesPaint(0, color);
    renderer.setSeriesPaint(1, color);
    return renderer;
  }

  private XYItemRenderer createOutlineRenderer()
  {
    XYItemRenderer renderer;
    renderer = new XYLineAndShapeRenderer(true, false);
    renderer.setBaseStroke(new BasicStroke(0.8f));
    renderer.setSeriesPaint(0, Color.white);
    renderer.setSeriesPaint(1, Color.white);
    return renderer;
  }

}
