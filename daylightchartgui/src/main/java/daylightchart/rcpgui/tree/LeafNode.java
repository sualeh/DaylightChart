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
package daylightchart.rcpgui.tree;


import daylightchart.location.Location;

/**
 * Leaf node in the locations tree, representing a location.
 * 
 * @author sfatehi
 */
public final class LeafNode
  extends TreeNode
{

  private static final long serialVersionUID = -4483206291184366016L;
  
  private final Location location;

  /**
   * Create a new leaf node for a location.
   * 
   * @param location
   *        Location
   */
  public LeafNode(final Location location)
  {
    super(location.toString());
    this.location = location;
  }

  /**
   * Get the location associated with this leaf node.
   * 
   * @return Location
   */
  public final Location getLocation()
  {
    return location;
  }

}
