package daylightchart.web.pages;


import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.RenderedDynamicImageResource;
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

  final class DaylightChartDynamicImageResource
    extends RenderedDynamicImageResource
  {

    private static final long serialVersionUID = -1060266442831931987L;

    DaylightChartDynamicImageResource(final int width, final int height)
    {
      super(width, height);
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

  /**
   * 
   */
  private static final long serialVersionUID = -7044812187092145509L;

  private final Location location;

  public DaylightChartPage(final Location location)
  {

    this.location = location;

    final ClientProperties properties = ((WebClientInfo) getRequestCycle()
      .getClientInfo()).getProperties();

    final int height = (int) (properties.getBrowserHeight() * 0.9);
    final int width = (int) (properties.getBrowserWidth() * 0.9);

    add(new Image("chart", new DaylightChartDynamicImageResource(width, height)));
  }

}
