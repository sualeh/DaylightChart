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

import daylightchart.location.Location;
import daylightchart.location.LocationsSortOrder;
import daylightchart.rcpgui.views.NavigationView;

public class SortLocationsAction
  extends Action
{

  public static final String ID = SortLocationsAction.class.getName();

  private final IWorkbenchWindow window;
  private LocationsSortOrder locationsSortOrder = null;

  public SortLocationsAction(final IWorkbenchWindow window)
  {
    this.window = window;

    flipNewSortOrder();

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
    
    flipNewSortOrder();
    final List<Location> locations = navigationView.getLocations();    
    Collections.sort(locations, locationsSortOrder);
    navigationView.setLocations(locations);    

  }

  private void flipNewSortOrder()
  {
    if (locationsSortOrder == null || locationsSortOrder == LocationsSortOrder.BY_NAME)
    {
      setText("Sort Locations by Name");
      locationsSortOrder = LocationsSortOrder.BY_LATITUDE;
    }
    else
    {
      setText("Sort Locations by Latitude");
      locationsSortOrder = LocationsSortOrder.BY_NAME;
    }
  }

}
