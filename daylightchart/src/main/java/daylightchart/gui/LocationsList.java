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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
  private GuiAction add;
  private GuiAction delete;
  private GuiAction edit;

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

    createActions();

    final JToolBar toolBar = new JToolBar();
    toolBar.setRollover(true);
    add(toolBar, BorderLayout.NORTH);
    toolBar.add(add);
    toolBar.add(delete);
    toolBar.add(edit);

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
          Location location = (Location) locationsList.getSelectedValue();
          if (location == null)
          {
            locationsList.setSelectedIndex(0);
            location = (Location) locationsList.getSelectedValue();
          }
          parent.addLocationTab(location);
          e.consume();
        }
        if (e.getButton() == MouseEvent.BUTTON2
            || e.getButton() == MouseEvent.BUTTON3)
        {
          createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });

    setLocations(UserPreferences.getLocations());
  }

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

  public int getSelectedLocationIndex()
  {
    return locationsList.getSelectedIndex();
  }

  public void removeLocation(final Location editLocation)
  {
    if (editLocation != null)
    {
      locations.remove(editLocation);
    }
    setLocations(locations);
  }

  public void replaceLocation(final Location editLocation,
                              final Location location)
  {
    if (editLocation != null && location != null)
    {
      locations.remove(editLocation);
      locations.add(location);
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
    UserPreferences.sortLocations(locations);
    setLocations(locations);
  }

  private void createActions()
  {

    add = new GuiAction("Add", "/icons/add_location.gif");
    delete = new GuiAction("Delete", "/icons/delete_location.gif");
    edit = new GuiAction("Edit", "/icons/edit_location.gif");

    add.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        new LocationDialog(LocationsList.this,
                           LocationDialog.LocationMaintenanceOperation.Add);
      }
    });

    delete.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        new LocationDialog(LocationsList.this,
                           LocationDialog.LocationMaintenanceOperation.Delete);
      }
    });

    edit.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        new LocationDialog(LocationsList.this,
                           LocationDialog.LocationMaintenanceOperation.Edit);
      }
    });

  }

  private JPopupMenu createPopupMenu()
  {
    final JPopupMenu rightPopup = new JPopupMenu();
    rightPopup.add(add);
    rightPopup.add(delete);
    rightPopup.add(edit);
    return rightPopup;
  }

}
