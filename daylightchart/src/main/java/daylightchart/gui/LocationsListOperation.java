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
package daylightchart.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.KeyStroke;

import daylightchart.location.Location;

import sf.util.ui.GuiAction;

enum LocationsListOperation
{

  /** Add location. */
  add(Messages.getString("DaylightChartGui.Menu.Actions.AddLocation"), "/icons/add_location.gif", "shift INSERT"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  /** Edit location. */
  edit(Messages.getString("DaylightChartGui.Menu.Actions.EditLocation"), "/icons/edit_location.gif", "control E"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  /** Delete location. */
  delete(Messages.getString("DaylightChartGui.Menu.Actions.DeleteLocation"), "/icons/delete_location.gif", "shift DELETE"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  private final String iconResource;
  private final String text;
  private final KeyStroke keyStroke;

  private LocationsListOperation(final String text,
                                 final String iconResource,
                                 final String keyStroke)
  {
    this.text = text;
    this.iconResource = iconResource;
    this.keyStroke = KeyStroke.getKeyStroke(keyStroke);
  }

  /**
   * Creates an action for this operation, on the given locations list.
   * 
   * @param locationsList
   *        Locations list.
   * @return Action
   */
  GuiAction getAction(final LocationsList locationsList)
  {
    final GuiAction action = new GuiAction(text, iconResource);
    action.setShortcutKey(keyStroke);
    action.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused") //$NON-NLS-1$
      final ActionEvent e)
      {
        final Location selectedLocation = locationsList.getSelectedLocation();
        final Location editedLocation = LocationDialog
          .showLocationDialog(locationsList, LocationsListOperation.this);
        if (editedLocation != null)
        {
          switch (LocationsListOperation.this)
          {
            case add:
              locationsList.addLocation(editedLocation);
              break;
            case edit:
              locationsList.replaceLocation(selectedLocation, editedLocation);
              break;
            case delete:
              locationsList.removeLocation(selectedLocation);
          }
        }
      }
    });
    return action;
  }

  /**
   * Text of the operation.
   * 
   * @return Text
   */
  final String getText()
  {
    return text;
  }

}
