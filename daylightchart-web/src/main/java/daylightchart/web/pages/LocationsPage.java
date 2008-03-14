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


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class LocationsPage
  extends WebPage
{

  private static final long serialVersionUID = -4454721164415868831L;

  public LocationsPage()
  {
    add(new FeedbackPanel("errorMessages"));

    add(new LocationsTable("locationsTable", 5));

    final Panel locationPanel = new LocationPanel("locationAdd");
    locationPanel.setVisible(false);
    locationPanel.setOutputMarkupPlaceholderTag(true);
    add(locationPanel);

    final AjaxLink addBugLink = new AjaxLink("add")
    {
      private static final long serialVersionUID = -846141758899328311L;

      @Override
      public void onClick(final AjaxRequestTarget target)
      {
        locationPanel.setVisible(!locationPanel.isVisible());
        target.addComponent(locationPanel);
      }
    };
    add(addBugLink);
  }

}
