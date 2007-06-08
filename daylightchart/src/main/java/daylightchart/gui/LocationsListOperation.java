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

import daylightchart.location.Location;

import sf.util.ui.GuiAction;

enum LocationsListOperation
{

  /** Delete location. */
  delete("Delete Location", "/icons/delete_location.gif"),
  /** Edit location. */
  edit("Edit Location", "/icons/edit_location.gif"),
  /** Add location. */
  add("Add Location", "/icons/add_location.gif");

  private final String iconResource;
  private final String text;

  private LocationsListOperation(final String text,
                                            final String iconResource)
  {
    this.text = text;
    this.iconResource = iconResource;
  }

  /**
   * Creates an action for this operation, on the given locations list.
   * 
   * @param locationsList
   *        Locations list.
   * @return Action
   */
  public GuiAction getAction(final LocationsList locationsList)
  {
    final GuiAction action = new GuiAction(text, iconResource);
    action.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent e)
      {
        Location selectedLocation = locationsList.getSelectedLocation();
        Location editedLocation = LocationDialog
          .showLocationDialog(locationsList,
                              LocationsListOperation.this);
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
  public final String getText()
  {
    return text;
  }

}
