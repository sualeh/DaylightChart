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


import java.io.File;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

import daylightchart.location.Location;
import daylightchart.location.parser.LocationsLoader;
import daylightchart.options.UserPreferences;
import daylightchart.rcpgui.views.NavigationView;

public class OpenLocationsFileAction
  extends Action
{

  public static final String ID = OpenLocationsFileAction.class.getName();

  private final IWorkbenchWindow window;

  public OpenLocationsFileAction(final IWorkbenchWindow window)
  {
    this.window = window;
    setText( "Open Locations File...");
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
    
    FileDialog dialog = new FileDialog(window.getShell(), SWT.OPEN);
    String selectedFileName = dialog.open();
    if (selectedFileName == null)
    {
      return;
    }

    File selectedFile = new File(selectedFileName);
    List<Location> locations = LocationsLoader.load(selectedFile);
    if (locations == null || locations.size() > 1)
    {
      NavigationView navigationView = (NavigationView) window.getActivePage()
        .findView(NavigationView.ID);
      navigationView.setLocations(locations);
      UserPreferences.setLocations(locations);
    }
    
  }

}
