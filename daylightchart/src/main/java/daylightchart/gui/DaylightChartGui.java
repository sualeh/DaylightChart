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


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.editor.ChartEditor;

import daylightchart.Version;
import daylightchart.chart.TimeZoneOption;
import daylightchart.location.Location;
import daylightchart.location.LocationsSortOrder;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;
import daylightchart.options.chart.ChartEditorFactory;
import daylightchart.options.chart.ChartOptions;

/**
 * Provides an GUI for daylight charts.
 * 
 * @author Sualeh Fatehi
 */
public final class DaylightChartGui
  extends JFrame
{

  protected final class LocationsFileActionListener
    implements ActionListener
  {

    private final DaylightChartGui mainWindow;

    protected LocationsFileActionListener()
    {
      mainWindow = DaylightChartGui.this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final ActionEvent actionevent)
    {

      if (actionevent.getActionCommand()
        .equals("DaylightChartGui.Menu.File.SaveLocations")) //$NON-NLS-1$
      {
        Actions.doSaveLocationsFileAction(mainWindow);
      }
      else if (actionevent.getActionCommand()
        .equals("DaylightChartGui.Menu.File.LoadLocations")) //$NON-NLS-1$
      {
        Actions.doOpenLocationsFileAction(mainWindow);
      }
    }

  }

  private final static long serialVersionUID = 3760840181833283637L;

  private static final Logger LOGGER = Logger.getLogger(DaylightChartGui.class
    .getName());

  private List<Location> locations;

  private final JList listBox;
  private final LocationsTabbedPane locationsTabbedPane;

  /**
   * Creates a new instance of a Daylight Chart main window.
   */
  public DaylightChartGui()
  {
    setTitle("Daylight Chart"); //$NON-NLS-1$
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    final Font font = new Font("Sans-serif", Font.PLAIN, 11); //$NON-NLS-1$

    locations = UserPreferences.getLocations();

    locationsTabbedPane = new LocationsTabbedPane();

    listBox = new JList();
    listBox.setFont(font);
    listBox.addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(@SuppressWarnings("unused")
      final ListSelectionEvent listSelectionEvent)
      {
        Location location = (Location) listBox.getSelectedValue();
        if (location == null)
        {
          listBox.setSelectedIndex(0);
          location = (Location) listBox.getSelectedValue();
        }
        locationsTabbedPane.addLocationTab(location);
      }
    });

    final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                new JScrollPane(listBox),
                                                locationsTabbedPane);
    splitPane.setOneTouchExpandable(true);
    getContentPane().add(splitPane);

    setJMenuBar(createMenuBar());

    refreshView();
    pack();
  }

  /**
   * Get the locations list.
   * 
   * @return Locations
   */
  public List<Location> getLocations()
  {
    return locations;
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
      this.locations = locations;
      refreshView();
      UserPreferences.setLocations(locations);
    }
  }

  private JMenu createFileMenu()
  {
    final JMenu menuFile = new JMenu(Messages
      .getString("DaylightChartGui.Menu.File")); //$NON-NLS-1$

    final JMenuItem openLocationsFile = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.File.LoadLocations")); //$NON-NLS-1$
    openLocationsFile
      .setActionCommand("DaylightChartGui.Menu.File.LoadLocations"); //$NON-NLS-1$
    openLocationsFile.addActionListener(new LocationsFileActionListener());
    menuFile.add(openLocationsFile);

    final JMenuItem saveLocationsFile = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.File.SaveLocations")); //$NON-NLS-1$
    saveLocationsFile
      .setActionCommand("DaylightChartGui.Menu.File.SaveLocations"); //$NON-NLS-1$
    saveLocationsFile.addActionListener(new LocationsFileActionListener());
    menuFile.add(saveLocationsFile);

    menuFile.addSeparator();

    final JMenuItem saveImage = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.File.SaveChart")); //$NON-NLS-1$
    saveImage.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        locationsTabbedPane.saveSelectedChart();
      }
    });
    menuFile.add(saveImage);

    final JMenuItem print = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.File.PrintChart")); //$NON-NLS-1$
    print.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        locationsTabbedPane.printSelectedChart();
      }
    });
    menuFile.add(print);

    menuFile.addSeparator();

    final JMenuItem exit = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.File.Exit")); //$NON-NLS-1$
    exit.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        exit();
      }
    });
    menuFile.add(exit);

    return menuFile;

  }

  private JMenu createHelpMenu()
  {
    final JMenu menuHelp = new JMenu(Messages
      .getString("DaylightChartGui.Menu.Help")); //$NON-NLS-1$

    final JMenuItem onlineHelp = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.Help.Online")); //$NON-NLS-1$
    onlineHelp.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        BareBonesBrowserLaunch
          .openURL("http://daylightchart.sourceforge.net/readme.html"); //$NON-NLS-1$
      }
    });
    menuHelp.add(onlineHelp);

    final JMenuItem about = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.Help.About")); //$NON-NLS-1$
    about.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        JOptionPane.showMessageDialog(DaylightChartGui.this, Version.about());
      }
    });
    menuHelp.add(about);

    return menuHelp;
  }

  private JMenuBar createMenuBar()
  {
    final JMenuBar menu = new JMenuBar();
    menu.add(createFileMenu());
    menu.add(createOptionsMenu());
    menu.add(createHelpMenu());
    return menu;
  }

  private JMenu createOptionsMenu()
  {

    final Options options = UserPreferences.getOptions();

    final JMenu menuOptions = new JMenu(Messages
      .getString("DaylightChartGui.Menu.Options")); //$NON-NLS-1$

    final JMenuItem sortLocations = new JMenuItem();
    switch (options.getLocationsSortOrder())
    {
      case BY_NAME:
        sortLocations.setText(Messages
          .getString("DaylightChartGui.Menu.Options.SortByLatitude")); //$NON-NLS-1$
        break;
      case BY_LATITUDE:
        sortLocations.setText(Messages
          .getString("DaylightChartGui.Menu.Options.SortByName")); //$NON-NLS-1$
        break;
    }

    final JMenuItem useTimeZone = new JMenuItem();
    switch (options.getTimeZoneOption())
    {
      case USE_TIME_ZONE:
        useTimeZone.setText(Messages
          .getString("DaylightChartGui.Menu.Options.UseLocalTime")); //$NON-NLS-1$
        break;
      case USE_LOCAL_TIME:
        useTimeZone.setText(Messages
          .getString("DaylightChartGui.Menu.Options.UseTimeZone")); //$NON-NLS-1$
        break;
    }

    final JMenuItem chartOptionsMenuItem = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.ChartOptions")); //$NON-NLS-1$

    final JMenuItem resetAll = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.ResetAll")); //$NON-NLS-1$

    menuOptions.add(sortLocations);
    menuOptions.add(useTimeZone);
    menuOptions.addSeparator();
    menuOptions.add(chartOptionsMenuItem);
    menuOptions.addSeparator();
    menuOptions.add(resetAll);

    sortLocations.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        final Options options = UserPreferences.getOptions();
        LocationsSortOrder locationsSortOrder = options.getLocationsSortOrder();
        switch (locationsSortOrder)
        {
          case BY_NAME:
            locationsSortOrder = LocationsSortOrder.BY_LATITUDE;
            sortLocations.setText(Messages
              .getString("DaylightChartGui.Menu.Options.SortByName")); //$NON-NLS-1$
            break;
          case BY_LATITUDE:
            locationsSortOrder = LocationsSortOrder.BY_NAME;
            sortLocations.setText(Messages
              .getString("DaylightChartGui.Menu.Options.SortByLatitude")); //$NON-NLS-1$
            break;
        }
        options.setLocationsSortOrder(locationsSortOrder);
        UserPreferences.setOptions(options);

        UserPreferences.sortLocations(locations);
        refreshView();
      }
    });

    useTimeZone.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        final Options options = UserPreferences.getOptions();
        TimeZoneOption timeZoneOption = options.getTimeZoneOption();
        switch (timeZoneOption)
        {
          case USE_TIME_ZONE:
            timeZoneOption = TimeZoneOption.USE_LOCAL_TIME;
            useTimeZone.setText(Messages
              .getString("DaylightChartGui.Menu.Options.UseLocalTime")); //$NON-NLS-1$
            break;
          case USE_LOCAL_TIME:
            timeZoneOption = TimeZoneOption.USE_TIME_ZONE;
            useTimeZone.setText(Messages
              .getString("DaylightChartGui.Menu.Options.UseTimeZone")); //$NON-NLS-1$
            break;
        }
        options.setTimeZoneOption(timeZoneOption);
        UserPreferences.setOptions(options);

        refreshView();
      }
    });

    chartOptionsMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        final Options options = UserPreferences.getOptions();
        final ChartOptions chartOptions = options.getChartOptions();

        final ChartEditor chartEditor = ChartEditorFactory
          .getXYPlotChartEditorFromOptions(chartOptions);
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
        JOptionPane
          .showMessageDialog(DaylightChartGui.this, Messages
            .getString("DaylightChartGui.Message.ResetAll"), //$NON-NLS-1$
                             Messages
                               .getString("DaylightChartGui.Menu.Options.ResetAll"), //$NON-NLS-1$
                             JOptionPane.INFORMATION_MESSAGE);
      }
    });

    return menuOptions;
  }

  private void exit()
  {
    dispose();
    System.exit(0);
  }

  private void refreshView()
  {
    listBox.setListData(new Vector<Location>(locations));
    listBox.setSelectedIndex(0);
    this.repaint();
  }

}
