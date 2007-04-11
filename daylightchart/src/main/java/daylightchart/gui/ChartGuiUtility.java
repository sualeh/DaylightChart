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
package daylightchart.gui;


import org.pointlocation6709.Angle;
import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;

import daylightchart.chart.DaylightChart;
import daylightchart.gui.preferences.ChartOptions;
import daylightchart.location.Location;

/**
 * GUI Utilties.
 * 
 * @author Sualeh Fatehi
 */
public class ChartGuiUtility
{

  /**
   * Creates a chart options instance.
   * 
   * @return Chart options
   */
  public final static ChartOptions getDefaultDaylightChartOptions()
  {
    // Create a fake chart
    final PointLocation pointLocation = new PointLocation(new Latitude(new Angle()),
                                                        new Longitude(new Angle()));
    final Location location = new Location("", "", "", pointLocation);
    final DaylightChart chart = new DaylightChart(location);

    final ChartOptions chartOptions = new ChartOptions();
    chartOptions.copyFromChart(chart);
    
    return chartOptions;
  }

  private ChartGuiUtility()
  {
  }

}
