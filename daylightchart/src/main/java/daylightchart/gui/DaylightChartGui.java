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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
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

import org.jfree.chart.ChartPanel;
import org.jfree.chart.editor.ChartEditor;

import daylightchart.UserPreferences;
import daylightchart.Version;
import daylightchart.chart.DaylightChart;
import daylightchart.gui.options.ChartOptions;
import daylightchart.location.Location;
import daylightchart.location.LocationComparator;
import daylightchart.location.LocationsSortOrder;

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
        GuiHelper.saveLocations(mainWindow);
      }
      else if (actionevent.getActionCommand()
        .equals("DaylightChartGui.Menu.File.LoadLocations")) //$NON-NLS-1$
      {
        GuiHelper.loadLocations(mainWindow);
      }
    }

  }

  private final static long serialVersionUID = 3760840181833283637L;

  private static final Logger LOGGER = Logger.getLogger(DaylightChartGui.class
    .getName());
  private List<Location> locations;
  private LocationsSortOrder locationsSortOrder;
  private final JList listBox;
  private final ChartPanel chartPanel;
  private final ChartOptions chartOptions;

  /**
   * Creates a new instance of a Daylight Chart main window.
   */
  public DaylightChartGui()
  {
    setTitle("Daylight Chart"); //$NON-NLS-1$
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    setJMenuBar(createMenuBar());

    final Font font = new Font("Sans-serif", Font.PLAIN, 11); //$NON-NLS-1$

    locationsSortOrder = LocationsSortOrder.BY_NAME;
    locations = new UserPreferences().getLocations();
    Collections.sort(locations, new LocationComparator(locationsSortOrder));

    chartOptions = new UserPreferences().getChartOptions();

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
        final DaylightChart chart = new DaylightChart(location);
        chartOptions.updateChart(chart);
        chartPanel.setChart(chart);
      }
    });

    chartPanel = new ChartPanel(null);
    chartPanel.setBackground(Color.WHITE);
    chartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    chartPanel.setPreferredSize(new Dimension(700, 495));

    final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                new JScrollPane(listBox),
                                                chartPanel);
    splitPane.setOneTouchExpandable(true);
    getContentPane().add(splitPane);
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
      // new UserPreferences().setLocations(locations);
    }
  }

  private JMenu createFileMenu()
  {
    final JMenu menuFile = new JMenu(Messages
      .getString("DaylightChartGui.Menu.File")); //$NON-NLS-1$

    final JMenuItem saveLocations = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.File.SaveLocations")); //$NON-NLS-1$
    saveLocations.setActionCommand("DaylightChartGui.Menu.File.SaveLocations");
    saveLocations.addActionListener(new LocationsFileActionListener());
    menuFile.add(saveLocations);

    final JMenuItem loadLocations = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.File.LoadLocations")); //$NON-NLS-1$
    loadLocations.setActionCommand("DaylightChartGui.Menu.File.LoadLocations");
    loadLocations.addActionListener(new LocationsFileActionListener());
    menuFile.add(loadLocations);

    menuFile.addSeparator();

    final JMenuItem saveImage = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.File.SaveChart")); //$NON-NLS-1$
    saveImage.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        try
        {
          chartPanel.doSaveAs();
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, Messages
            .getString("DaylightChartGui.Error.SaveChart"), e); //$NON-NLS-1$
        }
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
        chartPanel.createChartPrintJob();
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

    final JMenu menuOptions = new JMenu(Messages
      .getString("DaylightChartGui.Menu.Options")); //$NON-NLS-1$

    final JCheckBoxMenuItem sortByName = new JCheckBoxMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.SortByName"), //$NON-NLS-1$
                                                               true);
    final JCheckBoxMenuItem sortByLatitude = new JCheckBoxMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.SortByLatitude"), //$NON-NLS-1$
                                                                   false);

    final JMenuItem chartOptionsMenuItem = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.ChartOptions")); //$NON-NLS-1$

    final JMenuItem resetAll = new JMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.ResetAll")); //$NON-NLS-1$

    menuOptions.add(sortByName);
    menuOptions.add(sortByLatitude);
    menuOptions.addSeparator();
    menuOptions.add(chartOptionsMenuItem);
    menuOptions.addSeparator();
    menuOptions.add(resetAll);

    sortByName.addItemListener(new ItemListener()
    {
      public void itemStateChanged(@SuppressWarnings("unused")
      final ItemEvent itemEvent)
      {
        final boolean isSelected = sortByName.getState();
        if (isSelected)
        {
          locationsSortOrder = LocationsSortOrder.BY_NAME;
          refreshView();
        }
        sortByLatitude.setSelected(!isSelected);
      }
    });

    sortByLatitude.addItemListener(new ItemListener()
    {
      public void itemStateChanged(@SuppressWarnings("unused")
      final ItemEvent itemEvent)
      {
        final boolean isSelected = sortByLatitude.getState();
        if (isSelected)
        {
          locationsSortOrder = LocationsSortOrder.BY_LATITUDE;
          refreshView();
        }
        sortByName.setSelected(!sortByLatitude.getState());
      }
    });

    chartOptionsMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
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
          new UserPreferences().setChartOptions(chartOptions);
          // Also apply changes to the current chart
          final DaylightChart chart = (DaylightChart) chartPanel.getChart();
          chartOptions.updateChart(chart);
        }
      }
    });

    resetAll.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent actionevent)
      {
        new UserPreferences().clear();
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
    chartPanel.setChart(null);
    Collections.sort(locations, new LocationComparator(locationsSortOrder));
    listBox.setListData(new Vector<Location>(locations));
    listBox.setSelectedIndex(0);
    this.repaint();
  }

}
