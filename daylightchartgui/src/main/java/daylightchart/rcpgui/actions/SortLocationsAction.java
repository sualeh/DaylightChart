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
package daylightchart.rcpgui.actions;


import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

import daylightchart.gui.Messages;
import daylightchart.location.Location;
import daylightchart.location.LocationsSortOrder;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;
import daylightchart.rcpgui.views.NavigationView;

public class SortLocationsAction
  extends Action
{

  public static final String ID = UseTimeZoneAction.class.getName();

  private final IWorkbenchWindow window;

  public SortLocationsAction(final IWorkbenchWindow window)
  {
    this.window = window;

    Options options = UserPreferences.getOptions();
    switch (options.getLocationsSortOrder())
    {
      case BY_LATITUDE:
        setText(Messages.getString("DaylightChartGui.Menu.Options.SortByName")); //$NON-NLS-1$
        break;
      case BY_NAME:
        setText(Messages
          .getString("DaylightChartGui.Menu.Options.SortByLatitude")); //$NON-NLS-1$
        break;
    }

    // The id is used to refer to the action in a menu or toolbar
    setId(ID);
    // Associate the action with a pre-defined command, to allow key
    // bindings.
    setActionDefinitionId(ID);
  }

  @Override
  public void run()
  {
    if (window == null)
    {
      return;
    }

    final NavigationView navigationView = (NavigationView) window
      .getActivePage().findView(NavigationView.ID);

    flip();
    final List<Location> locations = navigationView.getLocations();
    Collections.sort(locations, UserPreferences.getOptions()
      .getLocationsSortOrder());
    navigationView.setLocations(locations);

  }

  private void flip()
  {
    Options options = UserPreferences.getOptions();
    LocationsSortOrder locationsSortOrder = options.getLocationsSortOrder();
    switch (locationsSortOrder)
    {
      case BY_NAME:
        setText(Messages.getString("DaylightChartGui.Menu.Options.SortByName")); //$NON-NLS-1$
        locationsSortOrder = LocationsSortOrder.BY_LATITUDE;
        break;
      case BY_LATITUDE:
        setText(Messages
          .getString("DaylightChartGui.Menu.Options.SortByLatitude")); //$NON-NLS-1$
        locationsSortOrder = LocationsSortOrder.BY_NAME;
        break;
    }
    options.setLocationsSortOrder(locationsSortOrder);
    UserPreferences.setOptions(options);
  }

}
