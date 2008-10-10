/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import sf.util.ui.GuiAction;
import daylightchart.location.Location;
import daylightchart.location.parser.FormatterException;
import daylightchart.location.parser.LocationFormatter;
import daylightchart.location.parser.LocationParser;
import daylightchart.location.parser.ParserException;

enum LocationsListOperation
{

  /** Add location. */
  add(
    Messages.getString("DaylightChartGui.Menu.Actions.AddLocation"), "/icons/add_location.gif", "shift INSERT"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  /** Edit location. */
  edit(
    Messages.getString("DaylightChartGui.Menu.Actions.EditLocation"), "/icons/edit_location.gif", "control E"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  /** Delete location. */
  delete(
    Messages.getString("DaylightChartGui.Menu.Actions.DeleteLocation"), "/icons/delete_location.gif", "shift DELETE"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  /** Copy location. */
  copy(
    Messages.getString("DaylightChartGui.Menu.Actions.CopyLocation"), "/icons/copy_location.gif", "ctrl C"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$    
  /** Paste location. */
  paste(
    Messages.getString("DaylightChartGui.Menu.Actions.PasteLocation"), "/icons/paste_location.gif", "ctrl V"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  private static final Logger LOGGER = Logger
    .getLogger(LocationsListOperation.class.getName());

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
      public void actionPerformed(@SuppressWarnings("unused") final ActionEvent event)
      {
        final Location selectedLocation = locationsList.getSelectedLocation();
        final Location editedLocation;
        switch (LocationsListOperation.this)
        {
          case add:
            // fall-through
          case edit:
            // fall-through
          case delete:
            editedLocation = LocationDialog
              .showLocationDialog(locationsList, LocationsListOperation.this);
            break;
          case copy:
            // fall-through
          case paste:
            // fall-through
          default:
            editedLocation = null;
            break;
        }

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
              break;
            default:
              break;
          }
        }
        else
        {
          switch (LocationsListOperation.this)
          {
            case copy:
              copyLocationToClipboard(selectedLocation);
              break;
            case paste:
              pasteLocationFromClipboard();
              break;
            default:
              break;
          }
        }
      }

      private void copyLocationToClipboard(final Location selectedLocation)
      {
        String selectedLocationString;
        try
        {
          selectedLocationString = LocationFormatter
            .formatLocation(selectedLocation);
        }
        catch (final FormatterException e)
        {
          selectedLocationString = "";
        }
        final Clipboard systemClipboard = Toolkit.getDefaultToolkit()
          .getSystemClipboard();
        final Transferable transferable = new StringSelection(selectedLocationString);
        systemClipboard.setContents(transferable, null);
      }

      private void pasteLocationFromClipboard()
      {
        String locationString = "";
        final Clipboard clipboard = Toolkit.getDefaultToolkit()
          .getSystemClipboard();
        final Transferable contents = clipboard.getContents(null);
        if (contents != null
            && contents.isDataFlavorSupported(DataFlavor.stringFlavor))
        {
          try
          {
            locationString = (String) contents
              .getTransferData(DataFlavor.stringFlavor);
            final Location location = LocationParser
              .parseLocation(locationString);
            locationsList.addLocation(location);
          }
          catch (final UnsupportedFlavorException e)
          {
            LOGGER.log(Level.FINE, "Could not paste from the clipboard", e);
          }
          catch (final IOException e)
          {
            LOGGER.log(Level.FINE, "Could not paste from the clipboard", e);
          }
          catch (final ParserException e)
          {
            LOGGER.log(Level.FINE, "Could not paste from the clipboard", e);
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
