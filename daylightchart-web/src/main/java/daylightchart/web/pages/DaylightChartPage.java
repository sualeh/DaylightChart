package daylightchart.web.pages;


import java.util.Calendar;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.request.WebClientInfo;

import daylightchart.daylightchart.calculation.RiseSetUtility;
import daylightchart.daylightchart.calculation.RiseSetYearData;
import daylightchart.daylightchart.chart.DaylightChart;
import daylightchart.location.Location;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;

public class DaylightChartPage
  extends WebPage
{

  private static final long serialVersionUID = -7044812187092145509L;

  public DaylightChartPage(final Location location)
  {

    final ClientProperties properties = ((WebClientInfo) getRequestCycle()
      .getClientInfo()).getProperties();

    final int height = (int) (properties.getBrowserHeight() * 0.9);
    final int width = (int) (properties.getBrowserWidth() * 0.9);

    final Options options = UserPreferences.getDefaultDaylightChartOptions();
    final RiseSetYearData riseSetData = RiseSetUtility
      .createRiseSetYear(location,
                         Calendar.getInstance().get(Calendar.YEAR),
                         options);
    final DaylightChart chart = new DaylightChart(riseSetData, options);

    add(new JFreeChartImage("chart", chart, width, height));

  }

}
