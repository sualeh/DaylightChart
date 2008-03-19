package daylightchart.web.pages;



import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.request.WebClientInfo;

import daylightchart.location.Location;

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

    add(new Image("chart", new DaylightChartDynamicImageResource(location,
                                                                 width,
                                                                 height)));
  }

}
