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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

import daylightchart.location.Location;
import daylightchart.location.parser.FormatterException;
import daylightchart.location.parser.LocationFormatter;
import daylightchart.location.parser.LocationsLoader;
import daylightchart.rcpgui.views.NavigationView;

public class SaveLocationsFileAction
  extends Action
{

  public static final String ID = SaveLocationsFileAction.class.getName();

  private final IWorkbenchWindow window;

  public SaveLocationsFileAction(final IWorkbenchWindow window,
                                 final String label)
  {
    this.window = window;
    setText(label);
    // The id is used to refer to the action in a menu or toolbar
    setId(ICommandIds.CMD_SAVE_CHART);
    // Associate the action with a pre-defined command, to allow key
    // bindings.
    setActionDefinitionId(ICommandIds.CMD_SAVE_CHART);
  }

  @Override
  public void run()
  {
    if (window != null)
    {
      FileDialog dialog = new FileDialog(window.getShell(), SWT.SAVE);
      String selectedFile = dialog.open();

      if (selectedFile == null)
      {
        return;
      }

      NavigationView navigationView = (NavigationView) window.getActivePage()
        .findView(NavigationView.ID);
      List<Location> locations = navigationView.getLocations();
      try
      {
        LocationFormatter.formatLocations(locations, new File(selectedFile));
      }
      catch (FormatterException e)
      {
        MessageDialog.openError(window.getShell(),
                                "Save Locations",
                                "Error saving locations file " + selectedFile);
      }
    }
  }
}
