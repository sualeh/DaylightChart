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


import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import daylightchart.options.chart.ChartOptionsListener;
import daylightchart.rcpgui.actions.ChartOptionsAction;
import daylightchart.rcpgui.actions.OpenLocationsFileAction;
import daylightchart.rcpgui.actions.PrintChartAction;
import daylightchart.rcpgui.actions.ResetAllAction;
import daylightchart.rcpgui.actions.SaveChartAction;
import daylightchart.rcpgui.actions.SaveLocationsFileAction;
import daylightchart.rcpgui.actions.SortLocationsAction;
import daylightchart.rcpgui.actions.UseTimeZoneAction;

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
  private PrintChartAction printChartAction;

  private SortLocationsAction sortLocationsAction;
  private UseTimeZoneAction useTimeZoneAction;
  private ChartOptionsAction chartOptionsAction;
  private ResetAllAction resetAllAction;

  public ApplicationActionBarAdvisor(final IActionBarConfigurer configurer)
  {
    super(configurer);
  }

// @Override
// protected void fillCoolBar(final ICoolBarManager coolBar)
// {
// final IToolBarManager toolbar = new ToolBarManager(SWT.FLAT |
// SWT.RIGHT);
// coolBar.add(new ToolBarContributionItem(toolbar, "main"));
// toolbar.add(openLocationsFileAction);
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
    final MenuManager optionsMenu = new MenuManager("&Options", "options");
    final MenuManager helpMenu = new MenuManager("&Help",
                                                 IWorkbenchActionConstants.M_HELP);

    menuBar.add(fileMenu);
    menuBar.add(optionsMenu);
    menuBar.add(helpMenu);

    // File
    fileMenu.add(openLocationsFileAction);
    fileMenu.add(saveLocationsFileAction);
    fileMenu.add(new Separator());
    fileMenu.add(saveChartAction);
    fileMenu.add(printChartAction);
    fileMenu.add(new Separator());
    fileMenu.add(exitAction);

    // Options
    optionsMenu.add(sortLocationsAction);
    optionsMenu.add(useTimeZoneAction);    
    optionsMenu.add(new Separator());
    optionsMenu.add(chartOptionsAction);
    optionsMenu.add(new Separator());
    optionsMenu.add(resetAllAction);

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

    openLocationsFileAction = new OpenLocationsFileAction(window);
    register(openLocationsFileAction);

    saveLocationsFileAction = new SaveLocationsFileAction(window);
    register(saveLocationsFileAction);

    saveChartAction = new SaveChartAction(window);
    register(saveChartAction);

    printChartAction = new PrintChartAction(window);
    register(printChartAction);

    sortLocationsAction = new SortLocationsAction(window);
    register(sortLocationsAction);

    useTimeZoneAction = new UseTimeZoneAction(window);
    register(useTimeZoneAction);

    chartOptionsAction = new ChartOptionsAction(window);
    register(chartOptionsAction);
    
    resetAllAction = new ResetAllAction(window);
    register(resetAllAction);
  }

}
