/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2010, Sualeh Fatehi.
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
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.geoname.data.Location;
import org.geoname.parser.LocationFormatter;

import daylightchart.gui.actions.LocationsListOperation;
import daylightchart.gui.util.GuiAction;
import daylightchart.options.UserPreferences;

/**
 * Locations list component.
 * 
 * @author Sualeh Fatehi
 */
class LocationsList
  extends JPanel
  implements LocationOperations
{

  private static final long serialVersionUID = -6884483130453983685L;

  private final DaylightChartGui mainWindow;
  private final JList locationsList;
  private List<Location> locations;

  /**
   * Create a new locations list component.
   * 
   * @param mainWindow
   *        Main window.
   */
  public LocationsList(final DaylightChartGui mainWindow)
  {

    super(new BorderLayout());

    this.mainWindow = mainWindow;

    final JToolBar toolBar = new JToolBar();
    toolBar.setRollover(true);
    add(toolBar, BorderLayout.NORTH);

    final JPopupMenu popupMenu = new JPopupMenu();

    createActions(toolBar, popupMenu);

    locationsList = new JList();
    locationsList.setVisibleRowCount(20);
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
          mainWindow.addLocationTab(getSelectedLocation());
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
      public void keyPressed(final KeyEvent e)
      {
      }

      public void keyReleased(final KeyEvent e)
      {
      }

      public void keyTyped(final KeyEvent e)
      {
        if (e.getKeyChar() == KeyEvent.VK_ENTER)
        {
          mainWindow.addLocationTab(getSelectedLocation());
        }
        e.consume();
      }
    });

    final List<Location> locations = UserPreferences.locationsFile().getData();
    Collections.sort(locations, UserPreferences.optionsFile().getData()
      .getLocationsSortOrder());
    setLocations(locations);
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.gui.LocationOperations#addLocation(org.geoname.data.Location)
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
   * {@inheritDoc}
   * 
   * @see daylightchart.gui.LocationOperations#getLocations()
   */
  public List<Location> getLocations()
  {
    return new ArrayList<Location>(locations);
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.gui.LocationOperations#getSelectedLocation()
   */
  public Location getSelectedLocation()
  {
    if (locationsList.getSelectedIndex() == -1)
    {
      locationsList.setSelectedIndex(0);
    }
    return (Location) locationsList.getSelectedValue();
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.gui.LocationOperations#removeLocation(org.geoname.data.Location)
   */
  public void removeLocation(final Location location)
  {
    if (location != null)
    {
      locations.remove(location);
      setLocations(locations);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.gui.LocationOperations#replaceLocation(org.geoname.data.Location,
   *      org.geoname.data.Location)
   */
  public void replaceLocation(final Location location,
                              final Location newLocation)
  {
    if (location != null && newLocation != null)
    {
      locations.remove(location);
      locations.add(newLocation);
      setLocations(locations);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.gui.LocationOperations#setLocations(java.util.List)
   */
  public void setLocations(final List<Location> locations)
  {
    if (locations != null && locations.size() > 0)
    {
      Collections.sort(locations, UserPreferences.optionsFile().getData()
        .getLocationsSortOrder());
      this.locations = locations;
      locationsList.setListData(new Vector<Location>(locations));
      locationsList.setSelectedIndex(0);
      UserPreferences.locationsFile().save(locations);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.gui.LocationOperations#sortLocations()
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
      final GuiAction action = operation.getAction(mainWindow);
      toolBar.add(action);
      popupMenu.add(action);
    }
  }

}
