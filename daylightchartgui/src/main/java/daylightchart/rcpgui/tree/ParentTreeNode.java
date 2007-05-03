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


import java.util.ArrayList;
import java.util.List;

/**
 * A parent tree node in the locations tree.
 * 
 * @author sfatehi
 */
public final class ParentTreeNode
  extends TreeNode
{

  private static final long serialVersionUID = -2162381060483721137L;

  private final List<TreeNode> children;

  /**
   * Create a parent tree node.
   * 
   * @param name
   *        Node name.
   */
  public ParentTreeNode(final String name)
  {
    super(name);
    children = new ArrayList<TreeNode>();
  }

  /**
   * Add a child to the parent.
   * 
   * @param child
   *        Child node
   */
  public void addChild(final TreeNode child)
  {
    children.add(child);
    child.setParent(this);
  }

  /**
   * Get all children.
   * 
   * @return Children
   */
  public TreeNode[] getChildren()
  {
    return children.toArray(new TreeNode[children.size()]);
  }

  /**
   * Whether this parent has any children.
   * 
   * @return Whether this parent has any children
   */
  public boolean hasChildren()
  {
    return children.size() > 0;
  }

  /**
   * Remove a child node from the tree
   * 
   * @param child
   *        Child to remove
   */
  public void removeChild(final TreeNode child)
  {
    children.remove(child);
    child.setParent(null);
  }
  
}
