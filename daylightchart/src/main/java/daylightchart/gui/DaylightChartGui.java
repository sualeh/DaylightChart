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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import org.jfree.chart.editor.ChartEditor;

import sf.util.ui.ExitAction;
import daylightchart.chart.TimeZoneOption;
import daylightchart.gui.actions.AboutAction;
import daylightchart.gui.actions.OnlineHelpAction;
import daylightchart.gui.actions.OpenLocationsFileAction;
import daylightchart.gui.actions.PrintChartAction;
import daylightchart.gui.actions.SaveChartAction;
import daylightchart.gui.actions.SaveLocationsFileAction;
import daylightchart.location.Location;
import daylightchart.location.LocationsSortOrder;
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

    setIconImage(new ImageIcon(DaylightChartGui.class.getResource("/icon.png"))
      .getImage());

    setTitle("Daylight Chart"); //$NON-NLS-1$
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
    createOptionsMenu(menuBar, toolBar);
    createHelpMenu(menuBar, toolBar);

    // Open the first location
    addLocationTab(locationsList.getSelectedLocation());

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

  public int getSelectedLocationIndex()
  {
    return locationsList.getSelectedLocationIndex();
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
   * Add a new location tab.
   * 
   * @param location
   *        Location.
   */
  void addLocationTab(final Location location)
  {
    locationsTabbedPane.addLocationTab(location);
  }

  private void createFileMenu(final JMenuBar menuBar, final JToolBar toolBar)
  {

    final OpenLocationsFileAction openLocationsFile = new OpenLocationsFileAction(this);
    final SaveLocationsFileAction saveLocationsFile = new SaveLocationsFileAction(this);
    final SaveChartAction saveChart = new SaveChartAction(locationsTabbedPane);
    final PrintChartAction printChart = new PrintChartAction(locationsTabbedPane);

    final ExitAction exit = new ExitAction(this, Messages
      .getString("DaylightChartGui.Menu.File.Exit")); //$NON-NLS-1$

    final JMenu menuFile = new JMenu(Messages
      .getString("DaylightChartGui.Menu.File")); //$NON-NLS-1$
    menuFile.add(openLocationsFile);
    menuFile.add(saveLocationsFile);
    menuFile.addSeparator();
    menuFile.add(saveChart);
    menuFile.add(printChart);
    menuFile.addSeparator();
    menuFile.add(exit);
    menuBar.add(menuFile);

    toolBar.add(openLocationsFile);
    toolBar.add(saveLocationsFile);
    toolBar.addSeparator();
    toolBar.add(saveChart);
    toolBar.add(printChart);

  }

  private void createHelpMenu(final JMenuBar menuBar, final JToolBar toolBar)
  {

    final OnlineHelpAction onlineHelp = new OnlineHelpAction();
    final AboutAction about = new AboutAction(DaylightChartGui.this);

    final JMenu menuHelp = new JMenu(Messages
      .getString("DaylightChartGui.Menu.Help")); //$NON-NLS-1$
    menuHelp.add(onlineHelp);
    menuHelp.add(about);
    menuBar.add(menuHelp);

    toolBar.addSeparator();
    toolBar.add(onlineHelp);

  }

  private void createOptionsMenu(final JMenuBar menuBar, final JToolBar toolBar)
  {

    String text;
    Icon icon;
    boolean isSelected;

    final Options options = UserPreferences.getOptions();

    final JMenu menuOptions = new JMenu(Messages
      .getString("DaylightChartGui.Menu.Options")); //$NON-NLS-1$

    final ButtonGroup sortingMenuItems = new ButtonGroup();

    text = Messages.getString("DaylightChartGui.Menu.Options.SortByLatitude"); //$NON-NLS-1$
    icon = new ImageIcon(DaylightChartGui.class
      .getResource("/icons/sort_by_latitude.gif")); //$NON-NLS-1$
    isSelected = options.getLocationsSortOrder() == LocationsSortOrder.BY_LATITUDE;
    final JRadioButtonMenuItem sortByLatitude = new JRadioButtonMenuItem(text,
                                                                         icon,
                                                                         isSelected);
    sortByLatitude.setSelectedIcon(new ImageIcon(DaylightChartGui.class
      .getResource("/icons/sort_by_latitude_dim.gif")));
    sortingMenuItems.add(sortByLatitude);
    menuOptions.add(sortByLatitude);

    text = Messages.getString("DaylightChartGui.Menu.Options.SortByName"); //$NON-NLS-1$
    icon = new ImageIcon(DaylightChartGui.class
      .getResource("/icons/sort_by_name.gif")); //$NON-NLS-1$
    isSelected = options.getLocationsSortOrder() == LocationsSortOrder.BY_NAME;
    final JRadioButtonMenuItem sortByName = new JRadioButtonMenuItem(text,
                                                                     icon,
                                                                     isSelected);
    sortByName.setSelectedIcon(new ImageIcon(DaylightChartGui.class
      .getResource("/icons/sort_by_name_dim.gif")));
    sortingMenuItems.add(sortByName);
    menuOptions.add(sortByName);

    menuOptions.addSeparator();

    final ButtonGroup timeZoneMenuItems = new ButtonGroup();
    final JRadioButtonMenuItem useLocalTime = new JRadioButtonMenuItem(Messages
                                                                         .getString("DaylightChartGui.Menu.Options.UseLocalTime"), options.getTimeZoneOption() == TimeZoneOption.USE_LOCAL_TIME); //$NON-NLS-1$
    timeZoneMenuItems.add(useLocalTime);
    menuOptions.add(useLocalTime);
    final JRadioButtonMenuItem useTimeZone = new JRadioButtonMenuItem(Messages
                                                                        .getString("DaylightChartGui.Menu.Options.UseTimeZone"), options.getTimeZoneOption() == TimeZoneOption.USE_TIME_ZONE); //$NON-NLS-1$
    timeZoneMenuItems.add(useTimeZone);
    menuOptions.add(useTimeZone);

    menuOptions.addSeparator();

    final JMenuItem chartOptionsMenuItem = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.ChartOptions")); //$NON-NLS-1$

    final JMenuItem resetAll = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.ResetAll")); //$NON-NLS-1$

    menuOptions.add(chartOptionsMenuItem);
    menuOptions.addSeparator();
    menuOptions.add(resetAll);

    sortByName.addItemListener(new ItemListener()
    {
      public void itemStateChanged(final ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
          final Options options = UserPreferences.getOptions();
          options.setLocationsSortOrder(LocationsSortOrder.BY_NAME);
          UserPreferences.setOptions(options);

          locationsList.sortLocations();
          DaylightChartGui.this.repaint();
        }
      }
    });

    sortByLatitude.addItemListener(new ItemListener()
    {
      public void itemStateChanged(final ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
          final Options options = UserPreferences.getOptions();
          options.setLocationsSortOrder(LocationsSortOrder.BY_LATITUDE);
          UserPreferences.setOptions(options);

          locationsList.sortLocations();
          DaylightChartGui.this.repaint();
        }
      }
    });

    useTimeZone.addItemListener(new ItemListener()
    {
      public void itemStateChanged(final ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
          final Options options = UserPreferences.getOptions();
          options.setTimeZoneOption(TimeZoneOption.USE_TIME_ZONE);
          UserPreferences.setOptions(options);
        }
      }
    });

    useLocalTime.addItemListener(new ItemListener()
    {
      public void itemStateChanged(final ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
          final Options options = UserPreferences.getOptions();
          options.setTimeZoneOption(TimeZoneOption.USE_LOCAL_TIME);
          UserPreferences.setOptions(options);
        }
      }
    });

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

    resetAll.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        UserPreferences.clear();
      }
    });

    menuBar.add(menuOptions);
  }

}
