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
package daylightchart.rcpgui.views;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import daylightchart.UserPreferences;
import daylightchart.location.Location;
import daylightchart.rcpgui.tree.LeafNode;
import daylightchart.rcpgui.tree.ParentTreeNode;
import daylightchart.rcpgui.tree.TreeNode;

public class NavigationView
  extends ViewPart
{
  class ViewContentProvider
    implements IStructuredContentProvider, ITreeContentProvider
  {

    public void dispose()
    {
    }

    public Object[] getChildren(final Object parent)
    {
      if (parent instanceof ParentTreeNode)
      {
        return ((ParentTreeNode) parent).getChildren();
      }
      return new Object[0];
    }

    public Object[] getElements(final Object parent)
    {
      return getChildren(parent);
    }

    public Object getParent(final Object child)
    {
      if (child instanceof TreeNode)
      {
        return ((TreeNode) child).getParent();
      }
      return null;
    }

    public boolean hasChildren(final Object parent)
    {
      if (parent instanceof ParentTreeNode)
      {
        return ((ParentTreeNode) parent).hasChildren();
      }
      return false;
    }

    public void inputChanged(final Viewer v,
                             final Object oldInput,
                             final Object newInput)
    {
    }
  }

  class ViewLabelProvider
    extends LabelProvider
  {

    @Override
    public Image getImage(final Object obj)
    {
      String imageKey = ISharedImages.IMG_OBJ_FILE;
      if (obj instanceof ParentTreeNode)
      {
        imageKey = ISharedImages.IMG_OBJ_FOLDER;
      }
      return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
    }

    @Override
    public String getText(final Object obj)
    {
      return obj.toString();
    }
  }

  public static final String ID = "daylightchart.rcpgui.NavigationView";

  private TreeViewer treeViewer;

  @Override
  public void createPartControl(final Composite parent)
  {
    treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
                                        | SWT.BORDER);
    treeViewer.setContentProvider(new ViewContentProvider());
    treeViewer.setLabelProvider(new ViewLabelProvider());
    treeViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        try
        {
          DaylightChartView chartView = (DaylightChartView) getSite()
            .getWorkbenchWindow().getActivePage()
            .showView(DaylightChartView.ID,
                      Long.toString(event.hashCode()),
                      IWorkbenchPage.VIEW_ACTIVATE);
        }
        catch (PartInitException e)
        {
          MessageDialog.openError(getSite().getShell(),
                                  "Error",
                                  "Error creating daylight chart view");
        }
      }
    });
    setLocations(UserPreferences.getLocations());
    // Register the viewer with the selection services
    getSite().setSelectionProvider(treeViewer);
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus()
  {
    treeViewer.getControl().setFocus();
  }

  public void setLocations(List<Location> locations)
  {
    final ParentTreeNode root = new ParentTreeNode("");
    for (Location location: locations)
    {
      root.addChild(new LeafNode(location));
    }

    treeViewer.setInput(root);
  }

  public List<Location> getLocations()
  {
    List<Location> locations = new ArrayList<Location>();
    ParentTreeNode root = (ParentTreeNode) treeViewer.getInput();
    if (root != null)
    {
      TreeNode[] nodes = root.getChildren();
      for (TreeNode treeNode: nodes)
      {
        if (treeNode instanceof LeafNode)
        {
          LeafNode node = (LeafNode) treeNode;
          locations.add(node.getLocation());
        }
      }
    }
    return locations;
  }

}
