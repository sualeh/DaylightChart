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


import java.awt.Frame;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartPanel;

import daylightchart.chart.DaylightChart;
import daylightchart.chart.TimeZoneOption;
import daylightchart.location.Location;
import daylightchart.options.UserPreferences;
import daylightchart.rcpgui.tree.LeafNode;

public class DaylightChartView
  extends ViewPart
{

  public static final String ID = "daylightchart.rcpgui.DaylightChartView";

  @Override
  public void createPartControl(final Composite parent)
  {

    ISelection selection = getSite().getWorkbenchWindow().getSelectionService()
      .getSelection(NavigationView.ID);
    Location location = null;
    if (!selection.isEmpty())
    {
      if (selection instanceof IStructuredSelection)
      {
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        Object object = structuredSelection.getFirstElement();
        if (object instanceof LeafNode)
        {
          LeafNode leaf = (LeafNode) object;
          location = leaf.getLocation();
        }
      }
    }

    if (location != null)
    {
      setPartName(location.toString());
      
      final TimeZoneOption timeZoneOption = UserPreferences.getOptions()
        .getTimeZoneOption();
      DaylightChart daylightChart = new DaylightChart(location, timeZoneOption);
      
      Composite chartComposite;

      chartComposite = new Composite(parent, SWT.EMBEDDED);
      chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
      final Frame frame = SWT_AWT.new_Frame(chartComposite);
      frame.add(new ChartPanel(daylightChart));

      // chartComposite = new ChartComposite(parent, SWT.NONE,
      // daylightChart, true);
    }
    else
    {
      MessageDialog.openError(getSite().getShell(),
                              "Error",
                              "Error getting selected location");
    }
  }

  @Override
  public void setFocus()
  {
  }

}
