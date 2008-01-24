/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import org.jfree.chart.ChartPanel;

import sf.util.ui.BareBonesBrowserLaunch;
import sf.util.ui.ExitAction;
import sf.util.ui.GuiAction;
import daylightchart.daylightchart.chart.ChartConfiguration;
import daylightchart.daylightchart.layout.ChartFileType;
import daylightchart.daylightchart.layout.DaylightChartReport;
import daylightchart.gui.actions.AboutAction;
import daylightchart.gui.actions.ChartOptionsAction;
import daylightchart.gui.actions.CloseCurrentTabAction;
import daylightchart.gui.actions.OnlineHelpAction;
import daylightchart.gui.actions.OpenLocationsFileAction;
import daylightchart.gui.actions.OpenReportFileAction;
import daylightchart.gui.actions.OptionsAction;
import daylightchart.gui.actions.PrintChartAction;
import daylightchart.gui.actions.ResetAllAction;
import daylightchart.gui.actions.SaveChartAction;
import daylightchart.gui.actions.SaveLocationsFileAction;
import daylightchart.gui.actions.SaveReportFileAction;
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
  private final LocationsTabbedPane locationsTabbedPane;
  private final boolean slimUi;

  /**
   * Creates a new instance of a Daylight Chart main window.
   */
  public DaylightChartGui(final boolean slimUi)
  {
    this(null, slimUi);
  }

  /**
   * Creates a new instance of a Daylight Chart main window.
   * 
   * @param location
   *        Location for a single chart window, or null for the full UI
   * @param slimUi
   *        Whether to use a slim user interface
   */
  public DaylightChartGui(final Location location, final boolean slimUi)
  {

    this.slimUi = slimUi;

    setIconImage(new ImageIcon(DaylightChartGui.class.getResource("/icon.png")) //$NON-NLS-1$
      .getImage());

    setTitle("Daylight Chart"); //$NON-NLS-1$
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    if (location == null)
    {
      // Create basic UI
      locationsTabbedPane = new LocationsTabbedPane(this);
      locationsList = new LocationsList(this);

      if (slimUi)
      {
        getContentPane().add(locationsList);
      }
      else
      {
        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                    locationsList,
                                                    locationsTabbedPane);
        splitPane.setOneTouchExpandable(true);
        getContentPane().add(splitPane);
      }

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

      // Open the first location
      addLocationTab(locationsList.getSelectedDaylightChartReport());
    }
    else
    {
      locationsTabbedPane = null;
      locationsList = null;
      final DaylightChartReport daylightChartReport = new DaylightChartReport(location,
                                                                              UserPreferences
                                                                                .getOptions());
      final ChartPanel chartPanel = new ChartPanel(daylightChartReport
        .getChart());
      chartPanel.setPreferredSize(ChartConfiguration.chartDimension);
      setContentPane(chartPanel);
    }

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

  /**
   * @return the options
   */
  public Options getOptions()
  {
    return UserPreferences.getOptions();
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
   * @return the slimUi
   */
  public boolean isSlimUi()
  {
    return slimUi;
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
   * Add a new location tab.
   * 
   * @param location
   *        Location.
   */
  void addLocationTab(final DaylightChartReport daylightChartReport)
  {
    if (slimUi)
    {
      final File reportFile = new File(UserPreferences.getScratchDirectory(),
                                       daylightChartReport
                                         .getReportFileName(ChartFileType.html));
      daylightChartReport.write(reportFile, ChartFileType.html);
      try
      {
        BareBonesBrowserLaunch.openURL(reportFile.toURL().toString());
      }
      catch (final MalformedURLException e)
      {
        LOGGER.log(Level.FINE, "Cannot open file " + reportFile, e);
      }
    }
    else
    {
      locationsTabbedPane.addLocationTab(daylightChartReport);
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

    menu.addSeparator();
    menu.add(new CloseCurrentTabAction(locationsTabbedPane));

    menuBar.add(menu);
  }

  private void createFileMenu(final JMenuBar menuBar, final JToolBar toolBar)
  {

    final GuiAction openLocationsFile = new OpenLocationsFileAction(this);
    final GuiAction saveLocationsFile = new SaveLocationsFileAction(this);
    final GuiAction saveChart = new SaveChartAction(this);
    final GuiAction printChart = new PrintChartAction(locationsTabbedPane);
    final GuiAction openReportFile = new OpenReportFileAction(this);
    final GuiAction saveReportFile = new SaveReportFileAction(this);

    final ExitAction exit = new ExitAction(this, Messages
      .getString("DaylightChartGui.Menu.File.Exit")); //$NON-NLS-1$

    final JMenu menu = new JMenu(Messages
      .getString("DaylightChartGui.Menu.File")); //$NON-NLS-1$
    menu.setMnemonic('F');

    menu.add(openLocationsFile);
    menu.add(saveLocationsFile);
    menu.addSeparator();
    menu.add(saveChart);
    menu.add(printChart);
    menu.addSeparator();
    menu.add(openReportFile);
    menu.add(saveReportFile);
    menu.addSeparator();
    menu.add(exit);
    menuBar.add(menu);

    toolBar.add(openLocationsFile);
    toolBar.add(saveLocationsFile);
    toolBar.addSeparator();
    toolBar.add(saveChart);
    toolBar.add(printChart);
    toolBar.addSeparator();

  }

  private void createHelpMenu(final JMenuBar menuBar, final JToolBar toolBar)
  {

    final GuiAction onlineHelp = new OnlineHelpAction();
    final GuiAction about = new AboutAction(DaylightChartGui.this);

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

    final GuiAction options = new OptionsAction(this);
    menu.add(options);

    final GuiAction chartOptions = new ChartOptionsAction(this);
    menu.add(chartOptions);

    final GuiAction resetAll = new ResetAllAction(this);
    menu.add(resetAll);

    menu.addSeparator();

    final JCheckBoxMenuItem slimUiMenuItem = new JCheckBoxMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.SlimUi")); //$NON-NLS-1$
    slimUiMenuItem.setState(isSlimUi());
    slimUiMenuItem.addItemListener(new ItemListener()
    {
      public void itemStateChanged(final ItemEvent e)
      {
        final boolean slimUi = e.getStateChange() == ItemEvent.SELECTED;
        UserPreferences.setSlimUi(slimUi);
        ResetAllAction.restart(DaylightChartGui.this, slimUi);
      }
    });
    menu.add(slimUiMenuItem);

    menuBar.add(menu);

    toolBar.add(options);
    toolBar.add(chartOptions);
    toolBar.addSeparator();
  }

}
