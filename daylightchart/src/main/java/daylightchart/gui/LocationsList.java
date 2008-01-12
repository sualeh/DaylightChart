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


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import sf.util.ui.GuiAction;
import daylightchart.location.Location;
import daylightchart.location.parser.LocationFormatter;
import daylightchart.options.UserPreferences;

/**
 * Locations list component.
 * 
 * @author Sualeh Fatehi
 */
public class LocationsList
  extends JPanel
{

  private static final long serialVersionUID = -6884483130453983685L;

  private final DaylightChartGui parent;
  private final JList locationsList;
  private List<Location> locations;

  /**
   * Create a new locations list component.
   * 
   * @param parent
   *        Main window.
   */
  public LocationsList(final DaylightChartGui parent)
  {

    super(new BorderLayout());

    this.parent = parent;

    final JToolBar toolBar = new JToolBar();
    toolBar.setRollover(true);
    add(toolBar, BorderLayout.NORTH);

    final JPopupMenu popupMenu = new JPopupMenu();

    createActions(toolBar, popupMenu);

    locationsList = new JList();
    add(new JScrollPane(locationsList));
    locationsList.setCellRenderer(new DefaultListCellRenderer()
    {
      private static final long serialVersionUID = -5892518623547830472L;

      @Override
      public Component getListCellRendererComponent(final JList list,
                                                    final Object value,
                                                    final int index,
                                                    final boolean isSelected,
                                                    final boolean cellHasFocus)
      {
        super.getListCellRendererComponent(list,
                                           value,
                                           index,
                                           isSelected,
                                           cellHasFocus);
        setToolTipText(LocationFormatter.getToolTip((Location) value));
        return this;
      }
    });
    locationsList.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(final MouseEvent e)
      {
        if (!e.isConsumed() && e.getClickCount() == 2)
        {
          openNewLocationTab(parent);
          e.consume();
        }
        if (e.getButton() == MouseEvent.BUTTON2
            || e.getButton() == MouseEvent.BUTTON3)
        {
          popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });
    locationsList.addKeyListener(new KeyListener()
    {
      public void keyPressed(@SuppressWarnings("unused")
      final KeyEvent e)
      {
      }

      public void keyReleased(@SuppressWarnings("unused")
      final KeyEvent e)
      {
      }

      public void keyTyped(final KeyEvent e)
      {
        if (e.getKeyChar() == KeyEvent.VK_ENTER)
        {
          openNewLocationTab(parent);
        }
        e.consume();
      }
    });

    setLocations(UserPreferences.getLocations());
  }

  /**
   * Add a location to the list, in sorted order.
   * 
   * @param location
   *        Location to add
   */
  public void addLocation(final Location location)
  {
    if (location != null)
    {
      locations.add(location);
    }
    setLocations(locations);
  }

  /**
   * Gets all locations in the list.
   * 
   * @return Locations list.
   */
  public List<Location> getLocations()
  {
    return new ArrayList<Location>(locations);
  }

  /**
   * Gets the parent window.
   * 
   * @return Window
   */
  public DaylightChartGui getMainWindow()
  {
    return parent;
  }

  /**
   * Get the currently selected location.
   * 
   * @return Currently selected location.
   */
  public Location getSelectedLocation()
  {
    return (Location) locationsList.getSelectedValue();
  }

  /**
   * Removes the specified location.
   * 
   * @param location
   *        Location
   */
  public void removeLocation(final Location location)
  {
    if (location != null)
    {
      locations.remove(location);
    }
    setLocations(locations);
  }

  /**
   * Replaces a location on the list with another.
   * 
   * @param location
   *        Location to replace
   * @param newLocation
   *        New location
   */
  public void replaceLocation(final Location location,
                              final Location newLocation)
  {
    if (location != null && newLocation != null)
    {
      locations.remove(location);
      locations.add(newLocation);
    }
    setLocations(locations);
  }

  /**
   * Set the locations list.
   * 
   * @param locations
   *        Locations list.
   */
  public void setLocations(final List<Location> locations)
  {
    if (locations != null && locations.size() > 0)
    {
      UserPreferences.sortLocations(locations);
      this.locations = locations;
      locationsList.setListData(new Vector<Location>(locations));
      locationsList.setSelectedIndex(0);
      UserPreferences.setLocations(locations);
    }
  }

  /**
   * Sets the selected location.
   * 
   * @param location
   *        Location to select
   */
  public void setSelectedLocation(final Location location)
  {
    locationsList.setSelectedValue(location, true);
  }

  /**
   * Sort the locations in the list.
   */
  public void sortLocations()
  {
    setLocations(locations);
  }

  private void createActions(final JToolBar toolBar, final JPopupMenu popupMenu)
  {
    for (final LocationsListOperation operation: LocationsListOperation
      .values())
    {
      final GuiAction action = operation.getAction(this);
      toolBar.add(action);
      popupMenu.add(action);
    }
  }

  private void openNewLocationTab(final DaylightChartGui parent)
  {
    Location location = (Location) locationsList.getSelectedValue();
    if (location == null)
    {
      locationsList.setSelectedIndex(0);
      location = (Location) locationsList.getSelectedValue();
    }
    parent.openLocationInBrowser(location);
  }

}
