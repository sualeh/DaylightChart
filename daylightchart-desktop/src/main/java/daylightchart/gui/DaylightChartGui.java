/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui;

import daylightchart.chart.options.ChartOptions;
import daylightchart.chart.options.ChartOptionsService;
import daylightchart.chart.report.DaylightChartReport;
import daylightchart.chart.report.DaylightChartReportService;
import daylightchart.gui.actions.AboutAction;
import daylightchart.gui.actions.CloseCurrentTabAction;
import daylightchart.gui.actions.GetCountriesFilesAction;
import daylightchart.gui.actions.GetUSStatesFilesAction;
import daylightchart.gui.actions.LocationsListOperation;
import daylightchart.gui.actions.OnlineHelpAction;
import daylightchart.gui.actions.OpenLocationTabAction;
import daylightchart.gui.actions.OpenLocationsFileAction;
import daylightchart.gui.actions.OptionsAction;
import daylightchart.gui.actions.PrintChartAction;
import daylightchart.gui.actions.ResetAllAction;
import daylightchart.gui.actions.SaveChartAction;
import daylightchart.gui.actions.SaveLocationsFileAction;
import daylightchart.gui.util.ExitAction;
import daylightchart.gui.util.GuiAction;
import daylightchart.options.Options;
import daylightchart.options.service.UserPreferencesService;
import java.awt.BorderLayout;
import java.io.Serial;
import java.util.Collection;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import org.geoname.data.Location;

/** Provides an GUI for daylight charts. */
public final class DaylightChartGui extends JFrame implements LocationOperations {

  @Serial private static final long serialVersionUID = 3760840181833283637L;

  private final LocationsList locationsList;
  private JMenu recentLocationsMenu;
  private final LocationsTabbedPane locationsTabbedPane;

  /** Creates a new instance of a Daylight Chart main window. */
  public DaylightChartGui() {
    setIconImage(
        new ImageIcon(DaylightChartGui.class.getResource("/META-INF/resources/logo.png")) // $NON-NLS-1$
            .getImage());

    setTitle("Daylight Chart"); // $NON-NLS-1$
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Create basic UI
    locationsTabbedPane = new LocationsTabbedPane();
    locationsList = new LocationsList(this);
    final JSplitPane splitPane =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, locationsList, locationsTabbedPane);
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
    Location firstTabLocation;
    final Collection<Location> recentLocations =
        UserPreferencesService.preferences().getRecentLocations();
    if (recentLocations.size() > 0) {
      firstTabLocation = recentLocations.iterator().next();
    } else {
      firstTabLocation = locationsList.getSelectedLocation();
    }
    addLocationTab(firstTabLocation);

    pack();
  }

  /**
   * {@inheritDoc}
   *
   * @see daylightchart.gui.LocationOperations#addLocation(org.geoname.data.Location)
   */
  @Override
  public void addLocation(final Location location) {
    locationsList.addLocation(location);
  }

  /**
   * Add a new location tab.
   *
   * @param location Location
   */
  public void addLocationTab(final Location location) {
    if (location == null) {
      return;
    }
    final UserPreferencesService preferencesService = UserPreferencesService.preferences();
    final ChartOptions chartOptions = ChartOptionsService.chartOptions().loadChartOptions();
    final Options options = preferencesService.loadOptions();
    final DaylightChartReport daylightChartReport =
        DaylightChartReportService.reports().createReport(location, options, chartOptions);
    locationsTabbedPane.addLocationTab(daylightChartReport);

    // Add to recent locations
    preferencesService.addRecentLocation(location);
    final Collection<Location> recentLocations = preferencesService.getRecentLocations();
    recentLocationsMenu.removeAll();
    for (final Location recentLocation : recentLocations) {
      recentLocationsMenu.add(
          new OpenLocationTabAction(this, recentLocation, recentLocationsMenu.getItemCount()));
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see daylightchart.gui.LocationOperations#getLocations()
   */
  @Override
  public List<Location> getLocations() {
    return locationsList.getLocations();
  }

  /**
   * Get the currently selected location.
   *
   * @return Currently selected location.
   */
  @Override
  public Location getSelectedLocation() {
    return locationsList.getSelectedLocation();
  }

  /**
   * {@inheritDoc}
   *
   * @see daylightchart.gui.LocationOperations#removeLocation(org.geoname.data.Location)
   */
  @Override
  public void removeLocation(final Location location) {
    locationsList.removeLocation(location);
  }

  /**
   * {@inheritDoc}
   *
   * @see daylightchart.gui.LocationOperations#replaceLocation(org.geoname.data.Location,
   *     org.geoname.data.Location)
   */
  @Override
  public void replaceLocation(final Location selectedLocation, final Location editedLocation) {
    locationsList.replaceLocation(selectedLocation, editedLocation);
  }

  /**
   * Sets the locations list on the GUI.
   *
   * @param locations Locations
   */
  @Override
  public void setLocations(final Collection<Location> locations) {
    if (locations != null && locations.size() > 0) {
      locationsList.setLocations(locations);
      this.repaint();
    }
  }

  /** Sorts the locations list on the GUI. */
  @Override
  public void sortLocations() {
    locationsList.sortLocations();
    this.repaint();
  }

  private void createActions(final JMenuBar menuBar, final JToolBar toolBar) {
    final JMenu menu =
        new JMenu(Messages.getString("DaylightChartGui.Menu.Actions")); // $NON-NLS-1$
    menu.setMnemonic('A');

    for (final LocationsListOperation operation : LocationsListOperation.values()) {
      final GuiAction action = operation.getAction(this);
      menu.add(action);
    }

    menu.addSeparator();
    menu.add(new GetCountriesFilesAction());
    menu.add(new GetUSStatesFilesAction());

    menu.addSeparator();
    menu.add(new CloseCurrentTabAction(locationsTabbedPane));

    menuBar.add(menu);
  }

  private void createFileMenu(final JMenuBar menuBar, final JToolBar toolBar) {

    final GuiAction openLocationsFile = new OpenLocationsFileAction(this);
    final GuiAction saveLocationsFile = new SaveLocationsFileAction(this);
    final GuiAction saveChart = new SaveChartAction(this);
    final GuiAction printChart = new PrintChartAction(locationsTabbedPane);

    final ExitAction exit =
        new ExitAction(this, Messages.getString("DaylightChartGui.Menu.File.Exit")); // $NON-NLS-1$

    final JMenu menu = new JMenu(Messages.getString("DaylightChartGui.Menu.File")); // $NON-NLS-1$
    menu.setMnemonic('F');

    recentLocationsMenu =
        new JMenu(Messages.getString("DaylightChartGui.Menu.File.RecentLocations")); // $NON-NLS-1$
    menu.setMnemonic('R');

    menu.add(openLocationsFile);
    menu.add(saveLocationsFile);
    menu.addSeparator();
    menu.add(saveChart);
    menu.add(printChart);
    menu.addSeparator();
    menu.add(recentLocationsMenu);
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

  private void createHelpMenu(final JMenuBar menuBar, final JToolBar toolBar) {

    final GuiAction onlineHelp = new OnlineHelpAction();
    final GuiAction about = new AboutAction(DaylightChartGui.this);

    final JMenu menuHelp =
        new JMenu(Messages.getString("DaylightChartGui.Menu.Help")); // $NON-NLS-1$
    menuHelp.setMnemonic('H');

    menuHelp.add(onlineHelp);
    menuHelp.add(about);
    menuBar.add(menuHelp);

    toolBar.addSeparator();
    toolBar.add(onlineHelp);
  }

  private void createOptionsMenu(final JMenuBar menuBar, final JToolBar toolBar) {

    final JMenu menu =
        new JMenu(Messages.getString("DaylightChartGui.Menu.Options")); // $NON-NLS-1$
    menu.setMnemonic('O');

    final GuiAction options = new OptionsAction(this);
    menu.add(options);

    final GuiAction resetAll = new ResetAllAction(this);
    menu.add(resetAll);

    menuBar.add(menu);

    toolBar.add(options);
    toolBar.addSeparator();
  }
}
