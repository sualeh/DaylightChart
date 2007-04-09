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


import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.editor.ChartEditor;

import daylightchart.Version;
import daylightchart.chart.DaylightChart;
import daylightchart.location.Location;

/**
 * Provides an GUI for daylight charts.
 * 
 * @author Sualeh Fatehi
 */
public final class DaylightChartGui
  extends JFrame
{

  private final static long serialVersionUID = 3760840181833283637L;

  private DataLocations dataLocations;
  private LocationsSortOrder locationsSortOrder;
  private final JList listBox;
  private final ChartPanel chartPanel;
  private final ChartEditor chartEditor;
  private File lastSelectedDirectory = new File("."); //$NON-NLS-1$

  /**
   * Creates a new instance of a Daylight Chart main window.
   */
  public DaylightChartGui()
  {
    setTitle("Daylight Chart"); //$NON-NLS-1$
    addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(final WindowEvent event)
      {
        exit();
      }
    });

    setMenuBar(createMenuBar());

    final Font font = new Font("Sans-serif", Font.PLAIN, 11); //$NON-NLS-1$

    locationsSortOrder = LocationsSortOrder.BY_NAME;
    dataLocations = new DataLocations();
    dataLocations.sortLocations(locationsSortOrder);

    chartEditor = ChartGuiUtility.getChartEditor();

    listBox = new JList();
    listBox.setFont(font);
    listBox.addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(final ListSelectionEvent e)
      {
        Location location = (Location) listBox.getSelectedValue();
        if (location == null)
        {
          listBox.setSelectedIndex(0);
          location = (Location) listBox.getSelectedValue();
        }
        chartPanel.setChart(new DaylightChart(location, 2007, chartEditor));
      }
    });

    chartPanel = new ChartPanel(null);
    chartPanel.setPreferredSize(new Dimension(700, 500));

    final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                new JScrollPane(listBox),
                                                chartPanel);
    splitPane.setOneTouchExpandable(true);
    getContentPane().add(splitPane);
    refreshView();
    pack();
  }

  private Menu createFileMenu()
  {
    final Menu menuFile = new Menu(Messages
      .getString("DaylightChartGui.Menu.File")); //$NON-NLS-1$

    final MenuItem saveLocations = new MenuItem(Messages
      .getString("DaylightChartGui.Menu.File.SaveLocations")); //$NON-NLS-1$
    saveLocations.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent actionevent)
      {
        saveLocations();
      }
    });
    menuFile.add(saveLocations);
    final MenuItem loadLocations = new MenuItem(Messages
      .getString("DaylightChartGui.Menu.File.LoadLocations")); //$NON-NLS-1$
    loadLocations.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent actionevent)
      {
        loadLocations();
      }
    });
    menuFile.add(loadLocations);
    menuFile.addSeparator();
    final MenuItem saveImage = new MenuItem(Messages.getString("DaylightChartGui.Menu.File.SaveChart")); //$NON-NLS-1$
    saveImage.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent actionevent)
      {
        try
        {
          chartPanel.doSaveAs();
        }
        catch (final IOException e)
        {
          e.printStackTrace();
        }
      }
    });
    menuFile.add(saveImage);
    final MenuItem print = new MenuItem(Messages.getString("DaylightChartGui.Menu.File.PrintChart")); //$NON-NLS-1$
    print.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent actionevent)
      {
        chartPanel.createChartPrintJob();
      }
    });
    menuFile.add(print);
    menuFile.addSeparator();
    final MenuItem exit = new MenuItem(Messages
      .getString("DaylightChartGui.Menu.File.Exit")); //$NON-NLS-1$
    exit.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent actionevent)
      {
        exit();
      }
    });
    menuFile.add(exit);
    return menuFile;
  }

  private Menu createHelpMenu()
  {
    final Menu menuHelp = new Menu(Messages.getString("DaylightChartGui.Menu.Help")); //$NON-NLS-1$

    final MenuItem about = new MenuItem(Messages
      .getString("DaylightChartGui.Menu.Help.About")); //$NON-NLS-1$
    about.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent actionevent)
      {
        JOptionPane.showMessageDialog(DaylightChartGui.this, Version.about());
      }
    });
    menuHelp.add(about);

    return menuHelp;
  }

  private MenuBar createMenuBar()
  {
    final MenuBar menu = new MenuBar();
    menu.add(createFileMenu());
    menu.add(createOptionsMenu());
    menu.add(createHelpMenu());
    return menu;
  }

  private Menu createOptionsMenu()
  {

    final Menu menuOptions = new Menu(Messages
      .getString("DaylightChartGui.Menu.Options")); //$NON-NLS-1$

    final CheckboxMenuItem sortByName = new CheckboxMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.SortByName"), //$NON-NLS-1$
                                                             true);
    final CheckboxMenuItem sortByLatitude = new CheckboxMenuItem(Messages
      .getString("DaylightChartGui.Menu.Options.SortByLatitude"), //$NON-NLS-1$
                                                                 false);

    final MenuItem chartOptions = new MenuItem(Messages.getString("DaylightChartGui.Menu.Options.ChartOptions")); //$NON-NLS-1$
    chartOptions.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent actionevent)
      {
        JOptionPane.showConfirmDialog(DaylightChartGui.this,
                                      chartEditor,
                                      Messages.getString("DaylightChartGui.Menu.Options.ChartOptions"), //$NON-NLS-1$
                                      JOptionPane.OK_CANCEL_OPTION,
                                      JOptionPane.PLAIN_MESSAGE);
      }
    });

    menuOptions.add(sortByName);
    menuOptions.add(sortByLatitude);
    menuOptions.addSeparator();
    menuOptions.add(chartOptions);

    sortByName.addItemListener(new ItemListener()
    {
      public void itemStateChanged(final ItemEvent itemEvent)
      {
        locationsSortOrder = LocationsSortOrder.BY_NAME;
        sortByLatitude.setState(!sortByName.getState());
        refreshView();
      }
    });

    sortByLatitude.addItemListener(new ItemListener()
    {
      public void itemStateChanged(final ItemEvent itemEvent)
      {
        locationsSortOrder = LocationsSortOrder.BY_LATITUDE;
        sortByName.setState(!sortByLatitude.getState());
        refreshView();
      }
    });

    return menuOptions;
  }

  private void exit()
  {
    dispose();
    System.exit(0);
  }

  private void loadLocations()
  {

    final JFileChooser fileDialog = new JFileChooser();
    fileDialog.setDialogTitle(Messages
      .getString("DaylightChartGui.Menu.File.LoadLocations")); //$NON-NLS-1$
    fileDialog
      .setSelectedFile(new File(lastSelectedDirectory, "locations.data")); //$NON-NLS-1$
    fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);
    fileDialog.showDialog(listBox, Messages
      .getString("DaylightChartGui.Menu.Open")); //$NON-NLS-1$
    final File selectedFile = fileDialog.getSelectedFile();
    if (selectedFile == null)
    {
      return;
    }

    if (!selectedFile.exists() || !selectedFile.canRead())
    {
      JOptionPane
        .showMessageDialog(this,
                           selectedFile
                               + "\n" //$NON-NLS-1$
                               + Messages
                                 .getString("DaylightChartGui.Error.CannotReadFile")); //$NON-NLS-1$
      return;
    }

    try
    {
      dataLocations = new DataLocations(selectedFile);
    }
    catch (final Exception e)
    {
      e.printStackTrace();
      JOptionPane
        .showMessageDialog(this,
                           selectedFile
                               + "\n" //$NON-NLS-1$
                               + Messages
                                 .getString("DaylightChartGui.Error.CannotReadFile")); //$NON-NLS-1$
      return;
    }
    refreshView();

    lastSelectedDirectory = selectedFile.getParentFile();
  }

  private void refreshView()
  {
    chartPanel.setChart(null);
    dataLocations.sortLocations(locationsSortOrder);
    listBox.setListData(new Vector<Location>(dataLocations.getLocations()));
    listBox.setSelectedIndex(0);
    this.repaint();
  }

  private void saveLocations()
  {
    final File selectedFile = showSaveDialog(Messages
      .getString("DaylightChartGui.Menu.File.SaveLocations"), "locations.data"); //$NON-NLS-1$ //$NON-NLS-2$
    if (selectedFile != null)
    {
      try
      {
        dataLocations.writeDataToFile(selectedFile);
      }
      catch (final Exception e)
      {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, Messages
          .getString("DaylightChartGui.Error.CannotSaveFile") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                                            + selectedFile, Messages
          .getString("DaylightChartGui.Menu.File.SaveFile"), //$NON-NLS-1$
                                      JOptionPane.OK_OPTION);
      }
    }
  }

  private File showSaveDialog(final String dialogTitle,
                              final String suggestedFilename)
  {
    final JFileChooser fileDialog = new JFileChooser();
    fileDialog.setDialogTitle(dialogTitle);
    fileDialog.setSelectedFile(new File(lastSelectedDirectory,
                                        suggestedFilename));
    fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
    fileDialog.showDialog(listBox, Messages
      .getString("DaylightChartGui.Menu.File.SaveFile")); //$NON-NLS-1$
    File selectedFile = fileDialog.getSelectedFile();
    if (selectedFile != null)
    {
      if (selectedFile.exists())
      {
        final int confirm = JOptionPane
          .showConfirmDialog(this,
                             selectedFile
                                 + "\n" //$NON-NLS-1$ 
                                 + Messages
                                   .getString("DaylightChartGui.ConfirmOverwrite"), //$NON-NLS-1$
                             Messages
                               .getString("DaylightChartGui.Menu.File.SaveFile"), //$NON-NLS-1$
                             JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
        {
          selectedFile = null;
        }
      }
      else
      {
        try
        {
          selectedFile.createNewFile();
        }
        catch (final IOException e)
        {
          e.printStackTrace();
          selectedFile = null;
        }
      }
      if (selectedFile == null || !selectedFile.canWrite())
      {
        JOptionPane.showMessageDialog(this, Messages.getString(Messages
          .getString("DaylightChartGui.Error.CannotSaveFile"))); //$NON-NLS-1$
      }
      if (selectedFile != null)
      {
        selectedFile.delete();
      }
    }
    if (selectedFile != null)
    {
      // Save last selected directory
      lastSelectedDirectory = selectedFile.getParentFile();
    }

    return selectedFile;
  }

}
