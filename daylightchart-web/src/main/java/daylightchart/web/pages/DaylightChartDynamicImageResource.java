/*
 * Copyright 2007-2008, Sualeh Fatehi <sualeh@hotmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution 3.0 License. 
 * To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by/3.0/ 
 * or send a letter to 
 * Creative Commons
 * 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
package daylightchart.web.pages;


import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;

import org.apache.wicket.markup.html.image.resource.RenderedDynamicImageResource;

import daylightchart.daylightchart.calculation.RiseSetUtility;
import daylightchart.daylightchart.calculation.RiseSetYearData;
import daylightchart.daylightchart.chart.DaylightChart;
import daylightchart.location.Location;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;

final class DaylightChartDynamicImageResource
  extends RenderedDynamicImageResource
{

  private static final long serialVersionUID = -1060266442831931987L;

  private final Location location;

  DaylightChartDynamicImageResource(final Location location,
                                    final int width,
                                    final int height)
  {
    super(width, height);
    this.location = location;
  }

  @Override
  protected boolean render(final Graphics2D graphics)
  {
    final Options options = UserPreferences.getDefaultDaylightChartOptions();
    final RiseSetYearData riseSetData = RiseSetUtility
      .createRiseSetYear(location,
                         Calendar.getInstance().get(Calendar.YEAR),
                         options);
    final DaylightChart chart = new DaylightChart(riseSetData, options);

    chart.draw(graphics,
               new Rectangle2D.Double(0, 0, getWidth(), getHeight()),
               null,
               null);
    return true;
  }

}
