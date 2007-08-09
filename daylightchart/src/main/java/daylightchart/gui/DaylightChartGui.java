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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.editor.ChartEditor;

import sf.util.ui.ExitAction;
import sf.util.ui.GuiAction;
import daylightchart.chart.ChartConfiguration;
import daylightchart.chart.DaylightChart;
import daylightchart.gui.actions.AboutAction;
import daylightchart.gui.actions.ChartOrientationChoiceAction;
import daylightchart.gui.actions.CloseCurrentTabAction;
import daylightchart.gui.actions.LocationsSortChoiceAction;
import daylightchart.gui.actions.OnlineHelpAction;
import daylightchart.gui.actions.OpenLocationsFileAction;
import daylightchart.gui.actions.PrintChartAction;
import daylightchart.gui.actions.ResetAllAction;
import daylightchart.gui.actions.SaveChartAction;
import daylightchart.gui.actions.SaveLocationsFileAction;
import daylightchart.gui.actions.TimeZoneOptionChoiceAction;
import daylightchart.gui.actions.TwilightChoiceAction;
import daylightchart.location.Location;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;
import daylightchart.options.chart.ChartOptions;

/**
 * Provides an GUI for daylight charts.
 * 
 * @author Sualeh Fatehi
 */
public final class DaylightChartGui
  extends JFrame
{

  private final static long serialVersionUID = 3760840181833283637L;

  private final LocationsList locationsList;
  private final LocationsTabbedPane locationsTabbedPane;

  /**
   * Creates a new instance of a Daylight Chart main window.
   */
  public DaylightChartGui()
  {
    this(null);
  }

  /**
   * Creates a new instance of a Daylight Chart main window.
   * 
   * @param location
   *        Location for a single chart window, or null for the full UI
   */
  public DaylightChartGui(final Location location)
  {

    setIconImage(new ImageIcon(DaylightChartGui.class.getResource("/icon.png")) //$NON-NLS-1$
      .getImage());

    setTitle("Daylight Chart"); //$NON-NLS-1$
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    if (location == null)
    {
      // Create basic UI
      locationsTabbedPane = new LocationsTabbedPane();
      locationsList = new LocationsList(this);

      final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                  locationsList,
                                                  locationsTabbedPane);
      splitPane.setOneTouchExpandable(true);
      getContentPane().add(splitPane);

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
      addLocationTab(locationsList.getSelectedLocation());
    }
    else
    {
      locationsTabbedPane = null;
      locationsList = null;

      final ChartPanel chartPanel = new ChartPanel(new DaylightChart(location,
                                                                     Calendar
                                                                       .getInstance()
                                                                       .get(Calendar.YEAR),
                                                                     new Options()));
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
  void addLocationTab(final Location location)
  {
    locationsTabbedPane.addLocationTab(location);
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

    final OpenLocationsFileAction openLocationsFile = new OpenLocationsFileAction(this);
    final SaveLocationsFileAction saveLocationsFile = new SaveLocationsFileAction(this);
    final SaveChartAction saveChart = new SaveChartAction(locationsTabbedPane);
    final PrintChartAction printChart = new PrintChartAction(locationsTabbedPane);

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

    final Options options = UserPreferences.getOptions();

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

    final JMenuItem chartOptionsMenuItem = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.ChartOptions")); //$NON-NLS-1$
    chartOptionsMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        final Options options = UserPreferences.getOptions();
        final ChartOptions chartOptions = options.getChartOptions();

        final ChartEditor chartEditor = chartOptions.getChartEditor();
        final int confirmValue = JOptionPane
          .showConfirmDialog(DaylightChartGui.this, chartEditor, Messages
            .getString("DaylightChartGui.Menu.Options.ChartOptions"), //$NON-NLS-1$
                             JOptionPane.OK_CANCEL_OPTION,
                             JOptionPane.PLAIN_MESSAGE);
        if (confirmValue == JOptionPane.OK_OPTION)
        {
          // Get chart options from the editor
          chartOptions.copyFromChartEditor(chartEditor);
          // Save preferences
          UserPreferences.setOptions(options);
        }
      }
    });

    final GuiAction resetAll = new ResetAllAction(this);

    menu.add(chartOptionsMenuItem);
    menu.addSeparator();
    menu.add(resetAll);

    menuBar.add(menu);

  }

}
