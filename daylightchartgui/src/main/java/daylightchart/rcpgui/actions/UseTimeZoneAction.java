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

import daylightchart.chart.TimeZoneOption;
import daylightchart.location.Location;
import daylightchart.location.LocationsSortOrder;
import daylightchart.rcpgui.views.NavigationView;

public class UseTimeZoneAction
  extends Action
{

  public static final String ID = UseTimeZoneAction.class.getName();

  private final IWorkbenchWindow window;
  private TimeZoneOption timeZoneOption = null;

  public UseTimeZoneAction(final IWorkbenchWindow window)
  {
    this.window = window;

    flip();

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

  }

  private void flip()
  {
    if (timeZoneOption == null)
    {
      timeZoneOption = TimeZoneOption.USE_TIME_ZONE;
    }
    switch (timeZoneOption)
    {
      case USE_LOCAL_TIME:
        setText("Use Time Zone");
        timeZoneOption = TimeZoneOption.USE_TIME_ZONE;
        break;
      case USE_TIME_ZONE:
        setText("Use Local Time");
        timeZoneOption = TimeZoneOption.USE_LOCAL_TIME;
        break;
    }
  }

}
