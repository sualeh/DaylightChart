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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.joda.time.LocalDateTime;

import sf.util.ui.BareBonesBrowserLaunch;
import sf.util.ui.ExitAction;
import sf.util.ui.GuiAction;
import daylightchart.daylightchart.layout.ChartFileType;
import daylightchart.daylightchart.layout.DaylightChartReport;
import daylightchart.gui.actions.AboutAction;
import daylightchart.gui.actions.ChartOptionsAction;
import daylightchart.gui.actions.ChartOrientationChoiceAction;
import daylightchart.gui.actions.LocationsSortChoiceAction;
import daylightchart.gui.actions.OnlineHelpAction;
import daylightchart.gui.actions.OpenLocationsFileAction;
import daylightchart.gui.actions.ResetAllAction;
import daylightchart.gui.actions.SaveChartAction;
import daylightchart.gui.actions.SaveLocationsFileAction;
import daylightchart.gui.actions.TimeZoneOptionChoiceAction;
import daylightchart.gui.actions.TwilightChoiceAction;
import daylightchart.location.Location;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;

/**
 * Provides an GUI for daylight charts.
 * 
 * @author Sualeh Fatehi
 */
public final class DaylightChartGui
  extends JFrame
{

  private final static long serialVersionUID = 3760840181833283637L;

  private static final Logger LOGGER = Logger.getLogger(DaylightChartGui.class
    .getName());

  private final LocationsList locationsList;

  /**
   * Creates a new instance of a Daylight Chart main window.
   */
  public DaylightChartGui()
  {

    setIconImage(new ImageIcon(DaylightChartGui.class.getResource("/icon.png")) //$NON-NLS-1$
      .getImage());

    setTitle("Daylight Chart"); //$NON-NLS-1$
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Create basic UI
    locationsList = new LocationsList(this);
    locationsList.setSize(200, 400);
    getContentPane().add(locationsList);

    // Create menus and toolbars
    final JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    final JToolBar toolBar = new JToolBar();
    toolBar.setRollover(true);
    add(toolBar, BorderLayout.NORTH);

    createFileMenu(menuBar, toolBar);
    createActions(menuBar, toolBar);
    createOptionsMenu(menuBar, toolBar);
    createHelpMenu(menuBar, toolBar);

    // Prevent resizing of the window beyond a certain point
    addComponentListener(new ComponentAdapter()
    {
      @Override
      public void componentResized(final ComponentEvent event)
      {
        setSize(Math.max(300, getWidth()), Math.max(500, getHeight()));
      }
    });

    pack();
  }

  /**
   * Gets locations from the list.
   * 
   * @return Locations list.
   */
  public List<Location> getLocations()
  {
    return locationsList.getLocations();
  }

  public DaylightChartReport getSelectedDaylightChartReport()
  {
    return locationsList.getSelectedDaylightChartReport();
  }

  public Location getSelectedLocation()
  {
    return locationsList.getSelectedLocation();
  }

  /**
   * Sets the locations list on the GUI.
   * 
   * @param locations
   *        Locations
   */
  public void setLocations(final List<Location> locations)
  {
    if (locations != null && locations.size() > 0)
    {
      locationsList.setLocations(locations);
      this.repaint();
    }
  }

  /**
   * Sorts the locations list on the GUI.
   */
  public void sortLocations()
  {
    locationsList.sortLocations();
    this.repaint();
  }

  /**
   * Add a new location tab in the browser.
   * 
   * @param location
   *        Location.
   */
  void openLocationInBrowser(final Location location)
  {
    final Options options = UserPreferences.getOptions();
    final String reportFilename = location.getDescription()
                                  + "."
                                  + new LocalDateTime()
                                    .toString("yyyyMMddhhmm") + ".html";
    final File reportFile = new File(options.getWorkingDirectory(),
                                     reportFilename);
    getSelectedDaylightChartReport().write(reportFile, ChartFileType.html);
    try
    {
      BareBonesBrowserLaunch.openURL(reportFile.toURL().toString());
    }
    catch (final MalformedURLException e)
    {
      LOGGER.log(Level.WARNING, "Cannot convert file name to URL", e);
    }
  }

  private void createActions(final JMenuBar menuBar,
                             @SuppressWarnings("unused")
                             final JToolBar toolBar)
  {
    final JMenu menu = new JMenu(Messages
      .getString("DaylightChartGui.Menu.Actions")); //$NON-NLS-1$
    menu.setMnemonic('A');

    for (final LocationsListOperation operation: LocationsListOperation
      .values())
    {
      final GuiAction action = operation.getAction(locationsList);
      menu.add(action);
    }

    menuBar.add(menu);
  }

  private void createFileMenu(final JMenuBar menuBar, final JToolBar toolBar)
  {

    final OpenLocationsFileAction openLocationsFile = new OpenLocationsFileAction(this);
    final SaveLocationsFileAction saveLocationsFile = new SaveLocationsFileAction(this);
    final SaveChartAction saveChart = new SaveChartAction(this);

    final ExitAction exit = new ExitAction(this, Messages
      .getString("DaylightChartGui.Menu.File.Exit")); //$NON-NLS-1$

    final JMenu menu = new JMenu(Messages
      .getString("DaylightChartGui.Menu.File")); //$NON-NLS-1$
    menu.setMnemonic('F');

    menu.add(openLocationsFile);
    menu.add(saveLocationsFile);
    menu.addSeparator();
    menu.add(saveChart);
    menu.addSeparator();
    menu.add(exit);
    menuBar.add(menu);

    toolBar.add(openLocationsFile);
    toolBar.add(saveLocationsFile);
    toolBar.addSeparator();

  }

  private void createHelpMenu(final JMenuBar menuBar, final JToolBar toolBar)
  {

    final OnlineHelpAction onlineHelp = new OnlineHelpAction();
    final AboutAction about = new AboutAction(DaylightChartGui.this);

    final JMenu menuHelp = new JMenu(Messages
      .getString("DaylightChartGui.Menu.Help")); //$NON-NLS-1$
    menuHelp.setMnemonic('H');

    menuHelp.add(onlineHelp);
    menuHelp.add(about);
    menuBar.add(menuHelp);

    toolBar.addSeparator();
    toolBar.add(onlineHelp);

  }

  private void createOptionsMenu(final JMenuBar menuBar,
                                 @SuppressWarnings("unused")
                                 final JToolBar toolBar)
  {

    final JMenu menu = new JMenu(Messages
      .getString("DaylightChartGui.Menu.Options")); //$NON-NLS-1$
    menu.setMnemonic('O');

    LocationsSortChoiceAction.addAllToMenu(this, menu);
    menu.addSeparator();

    TimeZoneOptionChoiceAction.addAllToMenu(menu);
    menu.addSeparator();

    ChartOrientationChoiceAction.addAllToMenu(menu);
    menu.addSeparator();

    TwilightChoiceAction.addAllToMenu(menu);
    menu.addSeparator();

    final JCheckBoxMenuItem showLegendMenuItem = new JCheckBoxMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.ShowChartLegend")); //$NON-NLS-1$
    showLegendMenuItem.setState(UserPreferences.getOptions()
      .isShowChartLegend());
    showLegendMenuItem.addItemListener(new ItemListener()
    {
      public void itemStateChanged(final ItemEvent e)
      {
        final Options options = UserPreferences.getOptions();
        options.setShowChartLegend(e.getStateChange() == ItemEvent.SELECTED);
        UserPreferences.setOptions(options);
      }
    });
    menu.add(showLegendMenuItem);

    final ChartOptionsAction chartOptionsAction = new ChartOptionsAction(this);
    menu.add(chartOptionsAction);
    menu.addSeparator();

    final GuiAction resetAll = new ResetAllAction(this);
    menu.add(resetAll);

    menuBar.add(menu);

  }
}
