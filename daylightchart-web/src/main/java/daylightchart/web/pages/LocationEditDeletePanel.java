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


import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import daylightchart.location.Location;
import daylightchart.options.UserPreferences;

final class LocationEditDeletePanel
  extends Panel
{

  private static final long serialVersionUID = 2753920209773575465L;

  LocationEditDeletePanel(final String id, final IModel model)
  {
    super(id, model);

    final Panel locationPanel = new LocationPanel("locationEdit", model);
    locationPanel.setVisible(false);
    locationPanel.setOutputMarkupPlaceholderTag(true);
    add(locationPanel);

    final Link deleteLink = new Link("delete", model)
    {
      private static final long serialVersionUID = 8375528747622018389L;

      @Override
      public void onClick()
      {
        Location location = (Location) getModelObject();
        List<Location> locations = UserPreferences.getLocations();
        locations.remove(location);
        UserPreferences.setLocations(locations);
        setResponsePage(LocationsPage.class);
      }
    };
    final String callConfirmJs = String
      .format("return confirmDelete('Are you you want to permanently delete \"%s\"?')",
              ((Location) getModelObject()).toString());
    deleteLink.add(new AttributeModifier("onClick",
                                         true,
                                         new Model(callConfirmJs)));
    add(deleteLink);

    final AjaxLink editLink = new AjaxLink("edit")
    {
      private static final long serialVersionUID = 7695320796784956116L;

      @Override
      public void onClick(final AjaxRequestTarget target)
      {
        locationPanel.setVisible(!locationPanel.isVisible());
        target.addComponent(locationPanel);
      }
    };
    add(editLink);

  }

}
