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
package daylightchart.rcpgui.actions;


import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import daylightchart.rcpgui.commands.PrintChartCommandHandler;

public class PrintChartAction
  extends Action
{
  private static final Logger LOGGER = Logger.getLogger(PrintChartAction.class
    .getName());

  public static final String ID = PrintChartAction.class.getName();

  private final IWorkbenchWindow window;

  public PrintChartAction(final IWorkbenchWindow window)
  {
    this.window = window;
    setText("Print chart...");
    // The id is used to refer to the action in a menu or toolbar
    setId(ID);
    // Associate the action with a pre-defined command, to allow key
    // bindings.
    setActionDefinitionId(ID);
  }

  @Override
  public void run()
  {
    if (window != null)
    {
      ICommandService service = (ICommandService) PlatformUI.getWorkbench()
        .getService(ICommandService.class);
      Command command = service.getCommand(PrintChartCommandHandler.ID);
      if (command != null && command.isEnabled())
      {
        try
        {
          ExecutionEvent event = new ExecutionEvent(command,
                                                    new HashMap(),
                                                    null,
                                                    ID);
          command.executeWithChecks(event);
        }
        catch (Exception e)
        {
          LOGGER.log(Level.WARNING, "Execute print chart action - "
                                    + e.getMessage());
        }
      }
    }

  }

}
