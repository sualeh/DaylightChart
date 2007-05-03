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
package daylightchart.rcpgui;


import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import daylightchart.rcpgui.actions.OpenLocationsFileAction;
import daylightchart.rcpgui.actions.SaveChartAction;
import daylightchart.rcpgui.actions.SaveLocationsFileAction;

/**
 * An action bar advisor is responsible for creating, adding, and
 * disposing of the actions added to a workbench window. Each window
 * will be populated with new actions.
 */
public class ApplicationActionBarAdvisor
  extends ActionBarAdvisor
{

  // Actions - important to allocate these only in makeActions, and
  // then use them in the fill methods. This ensures that the actions
  // aren't recreated when fillActionBars is called
  // with FILL_PROXY.
  private IWorkbenchAction exitAction;
  private IWorkbenchAction aboutAction;
  private OpenLocationsFileAction openLocationsFileAction;
  private SaveLocationsFileAction saveLocationsFileAction;
  private SaveChartAction saveChartAction;

  public ApplicationActionBarAdvisor(final IActionBarConfigurer configurer)
  {
    super(configurer);

  }

  // /**
  // * {@inheritDoc}
  // *
  // * @see
  // org.eclipse.ui.application.ActionBarAdvisor#fillCoolBar(org.eclipse.jface.action.ICoolBarManager)
  // */
  // @Override
  // protected void fillCoolBar(final ICoolBarManager coolBar)
  // {
  // final IToolBarManager toolbar = new ToolBarManager(SWT.FLAT |
  // SWT.RIGHT);
  // coolBar.add(new ToolBarContributionItem(toolbar, "main"));
  // toolbar.add(openViewAction);
  // toolbar.add(messagePopupAction);
  // }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface.action.IMenuManager)
   */
  @Override
  protected void fillMenuBar(final IMenuManager menuBar)
  {
    final MenuManager fileMenu = new MenuManager("&File",
                                                 IWorkbenchActionConstants.M_FILE);
    final MenuManager helpMenu = new MenuManager("&Help",
                                                 IWorkbenchActionConstants.M_HELP);

    menuBar.add(fileMenu);
    // Add a group marker indicating where action set menus will
    // appear.
    menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    menuBar.add(helpMenu);

    // File
    fileMenu.add(openLocationsFileAction);
    fileMenu.add(saveLocationsFileAction);
    fileMenu.add(new Separator());
    fileMenu.add(saveChartAction);
    fileMenu.add(new Separator());
    fileMenu.add(exitAction);

    // Help
    helpMenu.add(aboutAction);
  }

  @Override
  protected void makeActions(final IWorkbenchWindow window)
  {
    // Creates the actions and registers them.
    // Registering is needed to ensure that key bindings work.
    // The corresponding commands keybindings are defined in the
    // plugin.xml file.
    // Registering also provides automatic disposal of the actions
    // when the window is closed.

    exitAction = ActionFactory.QUIT.create(window);
    register(exitAction);

    aboutAction = ActionFactory.ABOUT.create(window);
    register(aboutAction);

    openLocationsFileAction = new OpenLocationsFileAction(window, "Open Locations File...");
    register(openLocationsFileAction);
    
    saveLocationsFileAction = new SaveLocationsFileAction(window, "Save Locations File...");
    register(saveLocationsFileAction);
    
    saveChartAction = new SaveChartAction(window, "Save Chart As...");
    register(saveChartAction);
  }

}
