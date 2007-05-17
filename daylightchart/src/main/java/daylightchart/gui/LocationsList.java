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


import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import daylightchart.location.Location;
import daylightchart.location.parser.LocationFormatter;
import daylightchart.options.UserPreferences;

/**
 * Locations list component.
 * 
 * @author Sualeh Fatehi
 */
public class LocationsList
  extends JList
{

  private static final long serialVersionUID = -6884483130453983685L;

  private List<Location> locations;

  /**
   * Create a new locations list component.
   * 
   * @param parent
   *        Main window.
   */
  public LocationsList(final DaylightChartGui parent)
  {

    setLocations(UserPreferences.getLocations());

    setCellRenderer(new DefaultListCellRenderer()
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
    addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(final MouseEvent e)
      {
        if (!e.isConsumed() && e.getClickCount() == 2)
        {
          Location location = (Location) getSelectedValue();
          if (location == null)
          {
            setSelectedIndex(0);
            location = (Location) getSelectedValue();
          }
          parent.addLocationTab(location);
        }
        e.consume();
      }
    });
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
   * Get the currently selected location.
   * 
   * @return Currently selected location.
   */
  public Location getSelectedLocation()
  {
    return (Location) getSelectedValue();
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
      setListData(new Vector<Location>(locations));
      setSelectedIndex(0);
      UserPreferences.setLocations(locations);
    }
  }

  /**
   * Sort the locations in the list.
   */
  public void sortLocations()
  {
    UserPreferences.sortLocations(locations);
    setLocations(locations);
  }

}
