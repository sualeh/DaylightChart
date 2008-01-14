/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * One place to change certain configuration options.
 * 
 * @author Sualeh Fatehi
 */
public final class ChartConfiguration
{

  /** Default dimensions for the chart. */
  public static final Dimension chartDimension = new Dimension(770, 600);

  /** Default month format. */
  public static final DateFormat monthsFormat = new SimpleDateFormat("MMM");

  /** Default daylight color for the chart. */
  public static final Color daylightColor = new Color(0xFF, 0xFF, 0x60, 200);
  /** Default twilight color for the chart. */
  public static final Color twilightColor = new Color(0xFF, 0xFF, 0xFF, 60);
  /** Default night color for the chart. */
  public static final Color nightColor = new Color(75, 11, 91, 190);

  /** Default font for the chart. */
  public static final Font chartFont = new Font("Helvetica", Font.PLAIN, 12);

  private ChartConfiguration()
  {
    // Prevent instantiation
  }

}
