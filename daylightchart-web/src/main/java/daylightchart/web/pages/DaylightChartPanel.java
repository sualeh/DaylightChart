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


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import daylightchart.location.Location;

final class DaylightChartPanel
  extends Panel
{

  private static final long serialVersionUID = 2753920209773575465L;

  DaylightChartPanel(final String id, final IModel model)
  {
    super(id, model);

    final Location location = (Location) getModelObject();
    final Link chartLink = new Link("chart", model)
    {
      private static final long serialVersionUID = 3335666889745114007L;

      @Override
      public void onClick()
      {
        setResponsePage(new DaylightChartPage(location));
      }
    };
    add(chartLink);
    chartLink.add(new Label("city", location.getDescription()));
  }

}
