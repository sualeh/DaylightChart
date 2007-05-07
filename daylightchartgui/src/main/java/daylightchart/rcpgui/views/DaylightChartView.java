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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartPanel;

import daylightchart.chart.DaylightChart;
import daylightchart.chart.TimeZoneOption;
import daylightchart.location.Location;
import daylightchart.options.UserPreferences;
import daylightchart.rcpgui.commands.PrintChartCommandHandler;
import daylightchart.rcpgui.commands.SaveChartCommandHandler;
import daylightchart.rcpgui.tree.LeafNode;

public class DaylightChartView
  extends ViewPart
{

  private static final Logger LOGGER = Logger.getLogger(DaylightChartView.class
    .getName());

  public static final String ID = "daylightchart.rcpgui.DaylightChartView";

  private Location location;
  private ChartPanel chartPanel;

  @Override
  public void createPartControl(final Composite parent)
  {

    ISelection selection = getSite().getWorkbenchWindow().getSelectionService()
      .getSelection(NavigationView.ID);
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
      chartPanel = new ChartPanel(daylightChart);
      frame.add(chartPanel);

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
    registerCommandListener(SaveChartCommandHandler.ID);
    registerCommandListener(PrintChartCommandHandler.ID);
  }

  private void registerCommandListener(String commandId)
  {
    ICommandService service = (ICommandService) PlatformUI.getWorkbench()
      .getService(ICommandService.class);
    Command cmd = service.getCommand(commandId);
    // Remove all listeners
    for (CommandListener commandListener: listeners)
    {
      cmd.removeExecutionListener(commandListener);
    }
    // Add listener for this view
    cmd.addExecutionListener(listener);
  }

  private class CommandListener
    implements IExecutionListener
  {

    public CommandListener()
    {
      DaylightChartView.listeners.add(this);
    }

    public void notHandled(String commandId, NotHandledException exception)
    {

    }

    public void postExecuteFailure(String commandId,
                                   ExecutionException exception)
    {

    }

    public void postExecuteSuccess(String commandId, Object returnValue)
    {
      if (commandId.equals(SaveChartCommandHandler.ID))
      {
        try
        {
          chartPanel.doSaveAs();
        }
        catch (IOException e)
        {
          LOGGER.log(Level.WARNING, "Could not execute action to save chart");
        }
      }
      if (commandId.equals(PrintChartCommandHandler.ID))
      {
        chartPanel.createChartPrintJob();
      }
    }

    public void preExecute(String commandId, ExecutionEvent event)
    {

    }

  }

  private CommandListener listener = new CommandListener();
  static List<CommandListener> listeners = new ArrayList<CommandListener>();

}
