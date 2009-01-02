/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2009, Sualeh Fatehi.
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
package daylightchart.options;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.geoname.data.Location;

import daylightchart.daylightchart.chart.DaylightChart;
import daylightchart.daylightchart.layout.DaylightChartReport;
import daylightchart.gui.actions.LocationFileType;
import daylightchart.options.chart.ChartOptions;

/**
 * User preferences for the GUI.
 * 
 * @author Sualeh Fatehi
 */
public final class UserPreferences
{

  private static final Logger LOGGER = Logger.getLogger(UserPreferences.class
    .getName());

  private static final int NUMBER_RECENT_LOCATIONS = 9;

  private static final File scratchDirectory;
  private static File settingsDirectory;

  private static File locationsDataFile;
  private static File recentLocationsDataFile;
  private static File reportFile;
  private static File optionsFile;

  private static List<Location> locations;
  private static List<Location> recentLocations;
  private static JasperReport report;
  private static Options options;

  static
  {
    scratchDirectory = new File(System.getProperty("java.io.tmpdir"), ".");
    validateDirectory(scratchDirectory);

    settingsDirectory = new File(System.getProperty("user.home", "."),
                                 ".daylightchart");
    initialize();
  }

  /**
   * Adds a recent location for the current user.
   * 
   * @param location
   *        Location
   */
  public static void addRecentLocation(final Location location)
  {
    if (location == null)
    {
      return;
    }
    if (recentLocations.contains(location))
    {
      recentLocations.remove(location);
    }
    recentLocations.add(location);

    setRecentLocations(recentLocations);
  }

  /**
   * Clears all user preferences.
   */
  public static void clear()
  {
    try
    {
      FileUtils.deleteDirectory(settingsDirectory);
    }
    catch (IOException e)
    {
      LOGGER.log(Level.WARNING, "Could not delete " + settingsDirectory, e);
      return;
    }
    initialize();
  }

  /**
   * Creates a options instance.
   * 
   * @return Options
   */
  public static Options getDefaultDaylightChartOptions()
  {
    final ChartOptions chartOptions = new ChartOptions();
    chartOptions.copyFromChart(new DaylightChart());

    final Options options = new Options();
    options.setChartOptions(chartOptions);

    return options;
  }

  /**
   * Gets the locations for the current user.
   * 
   * @return Locations
   */
  public static List<Location> getLocations()
  {
    return locations;
  }

  /**
   * Gets the options for the current user.
   * 
   * @return Options
   */
  public static Options getOptions()
  {
    return options;
  }

  /**
   * Gets the recent locations for the current user.
   * 
   * @return Locations
   */
  public static List<Location> getRecentLocations()
  {
    List<Location> lastRecentLocations = new ArrayList<Location>(UserPreferences.recentLocations);
    Collections.reverse(lastRecentLocations);
    final int numberRecentLocations = Math.min(lastRecentLocations.size(),
                                               NUMBER_RECENT_LOCATIONS);
    lastRecentLocations = lastRecentLocations.subList(0, numberRecentLocations);
    return lastRecentLocations;
  }

  /**
   * Gets the report for the current user.
   * 
   * @return Report
   */
  public static JasperReport getReport()
  {
    return report;
  }

  /**
   * @return the workingDirectory
   */
  public static File getScratchDirectory()
  {
    return scratchDirectory;
  }

  /**
   * Import a report file.
   * 
   * @param reportFile
   *        Report file
   * @return Whether the file could be imported
   */
  public static boolean importReport(final File reportFile)
  {
    boolean imported = false;
    final JasperReport report = FileOperations.loadReportFromFile(reportFile);
    if (report != null)
    {
      setReport(report);
      imported = true;
    }
    return imported;
  }

  /**
   * Main method. Lists all user preferences.
   * 
   * @param args
   *        Command line arguments
   * @throws Exception
   *         On an exception
   */
  public static void main(final String[] args)
    throws Exception
  {
    UserPreferences.clear();
  }

  /**
   * Sets the locations for the current user.
   * 
   * @param locations
   *        Locations
   */
  public static void setLocations(final List<Location> locations)
  {
    if (locations == null)
    {
      return;
    }
    UserPreferences.locations = locations;
    FileOperations.saveLocationsToFile(locations, locationsDataFile);
  }

  /**
   * Sets the options for the current user.
   * 
   * @param options
   *        Options
   */
  public static void setOptions(final Options options)
  {
    if (options == null)
    {
      return;
    }
    UserPreferences.options = options;
    FileOperations.saveOptionsToFile(options, optionsFile);
  }

  /**
   * Set the location of the settings directory.
   * 
   * @param settingsDirectoryPath
   *        Location of the settings directory
   */
  public static void setSettingsDirectory(final String settingsDirectoryPath)
  {
    if (StringUtils.isBlank(settingsDirectoryPath))
    {
      return;
    }
    final File settingsDirectory = new File(settingsDirectoryPath);

    settingsDirectory.mkdirs();
    validateDirectory(settingsDirectory);
    LOGGER.fine("Created settings directory " + settingsDirectoryPath);

    UserPreferences.settingsDirectory = settingsDirectory;
    locationsDataFile = new File(settingsDirectory, "locations.data");
    recentLocationsDataFile = new File(settingsDirectory,
                                       "recent.locations.data");
    reportFile = new File(settingsDirectory, "DaylightChartReport.jrxml");
    optionsFile = new File(settingsDirectory, "options.xml");
  }

  /**
   * Sets the slim mode for the user interface.
   * 
   * @param slimUi
   *        Slim mode for the user interface
   */
  public static void setSlimUi(final boolean slimUi)
  {
    options.setSlimUi(slimUi);
    setOptions(options);
  }

  /**
   * Sets the working directory.
   * 
   * @param workingDirectory
   *        Working directory
   */
  public static void setWorkingDirectory(final File workingDirectory)
  {
    options.setWorkingDirectory(workingDirectory);
    setOptions(options);
  }

  private static void initialize()
  {
    setSettingsDirectory(settingsDirectory.getAbsolutePath());

    initializeLocations();
    initializeRecentLocations();
    initializeReport();
    initializeOptions();
  }

  private static void initializeLocations()
  {
    // Try loading from user preferences
    locations = FileOperations.loadLocationsFromFile(LocationFileType.data,
                                                     locationsDataFile);

    // Load from internal store
    if (locations == null)
    {
      final InputStream input = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("locations.data");
      locations = FileOperations.loadLocations(LocationFileType.data, input);
    }

    if (locations == null)
    {
      throw new RuntimeException("Cannot load locations");
    }
  }

  private static void initializeOptions()
  {
    // Try loading from user preferences
    options = FileOperations.loadOptionsFromFile(optionsFile);

    // Load from internal store
    if (options == null)
    {
      options = UserPreferences.getDefaultDaylightChartOptions();
    }
  }

  private static void initializeRecentLocations()
  {
    // Try loading from user preferences
    recentLocations = FileOperations
      .loadLocationsFromFile(LocationFileType.data, recentLocationsDataFile);

    if (recentLocations == null)
    {
      recentLocations = new ArrayList<Location>();
    }
  }

  private static void initializeReport()
  {
    // Try loading from user preferences
    report = FileOperations.loadReportFromFile(reportFile);

    // Load from internal store
    if (report == null)
    {
      final InputStream input = DaylightChartReport.class
        .getResourceAsStream("/DaylightChartReport.jrxml");
      report = FileOperations.loadReport(input);
    }

    if (report == null)
    {
      throw new RuntimeException("Cannot load report");
    }
  }

  /**
   * Sets the recent locations for the current user.
   * 
   * @param locations
   *        Locations
   */
  private static void setRecentLocations(final List<Location> locations)
  {
    if (locations == null)
    {
      return;
    }
    UserPreferences.recentLocations = locations;
    FileOperations
      .saveLocationsToFile(recentLocations, recentLocationsDataFile);
  }

  /**
   * Sets the report for the current user.
   * 
   * @param report
   *        Report
   */
  private static void setReport(final JasperReport report)
  {
    if (report == null)
    {
      return;
    }
    UserPreferences.report = report;
    FileOperations.saveReportToFile(report, reportFile);
  }

  /**
   * @param directory
   *        the workingDirectory to set
   */
  private static void validateDirectory(final File directory)
  {
    final boolean isDirectoryValid = directory != null && directory.exists()
                                     && directory.isDirectory()
                                     && directory.canWrite();
    if (!isDirectoryValid)
    {
      throw new IllegalArgumentException("Directory is not writable - "
                                         + directory);
    }
  }

  private UserPreferences()
  {
    // Prevent external instantiation
  }

}
