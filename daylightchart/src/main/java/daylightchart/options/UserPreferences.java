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
import java.util.List;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.lang.StringUtils;
import org.geoname.data.Location;

import daylightchart.gui.actions.LocationFileType;

/**
 * User preferences for the GUI.
 * 
 * @author Sualeh Fatehi
 */
public final class UserPreferences
{

  private static final Logger LOGGER = Logger.getLogger(UserPreferences.class
    .getName());

  private static final File scratchDirectory;
  private static File settingsDirectory;

  private static LocationsDataFile locationsFile;
  private static RecentLocationsDataFile recentLocationsFile;
  private static ReportDataFile reportFile;
  private static OptionsDataFile optionsFile;

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
    recentLocationsFile.add(location);
    recentLocationsFile.save();
  }

  /**
   * Clears all user preferences.
   */
  public static void clear()
  {
    optionsFile.delete();
    locationsFile.delete();
    recentLocationsFile.delete();
    reportFile.delete();

    if (settingsDirectory.exists())
    {
      settingsDirectory.delete();
    }

    initialize();
  }

  /**
   * Gets the locations for the current user.
   * 
   * @return Locations
   */
  public static List<Location> getLocations()
  {
    return locationsFile.getData();
  }

  /**
   * Gets the options for the current user.
   * 
   * @return Options
   */
  public static Options getOptions()
  {
    return optionsFile.getData();
  }

  /**
   * Gets the recent locations for the current user.
   * 
   * @return Locations
   */
  public static List<Location> getRecentLocations()
  {
    return recentLocationsFile.getData();
  }

  /**
   * Gets the report for the current user.
   * 
   * @return Report
   */
  public static JasperReport getReport()
  {
    return reportFile.getData();
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
    ReportDataFile importedReportFile = new ReportDataFile(reportFile);
    importedReportFile.load();
    JasperReport report = importedReportFile.getData();
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
    locationsFile.setLocations(locations);
    locationsFile.save();
  }

  /**
   * Sets the options for the current user.
   * 
   * @param options
   *        Options
   */
  public static void setOptions(final Options options)
  {
    optionsFile.setData(options);
    optionsFile.save();
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
    locationsFile = new LocationsDataFile(new File(settingsDirectory,
                                                   "locations.data"),
                                          LocationFileType.data);

    recentLocationsFile = new RecentLocationsDataFile(settingsDirectory);
    recentLocationsFile.loadWithFallback();

    reportFile = new ReportDataFile(new File(settingsDirectory,
                                             "DaylightChartReport.jrxml"));
    reportFile.loadWithFallback();

    optionsFile = new OptionsDataFile(settingsDirectory);
    optionsFile.loadWithFallback();
  }

  /**
   * Sets the slim mode for the user interface.
   * 
   * @param slimUi
   *        Slim mode for the user interface
   */
  public static void setSlimUi(final boolean slimUi)
  {
    Options options = optionsFile.getData();
    options.setSlimUi(slimUi);
    optionsFile.setData(options);
    optionsFile.save();
  }

  /**
   * Sets the working directory.
   * 
   * @param workingDirectory
   *        Working directory
   */
  public static void setWorkingDirectory(final File workingDirectory)
  {
    Options options = optionsFile.getData();
    options.setWorkingDirectory(workingDirectory);
    optionsFile.setData(options);
    optionsFile.save();
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
    locationsFile = new LocationsDataFile(new File(settingsDirectory,
                                                   "locations.data"),
                                          LocationFileType.data);
    locationsFile.loadWithFallback();
  }

  private static void initializeOptions()
  {
    optionsFile = new OptionsDataFile(settingsDirectory);
    optionsFile.loadWithFallback();
  }

  private static void initializeRecentLocations()
  {
    recentLocationsFile = new RecentLocationsDataFile(settingsDirectory);
    recentLocationsFile.loadWithFallback();
  }

  private static void initializeReport()
  {
    // Try loading from user preferences
    reportFile = new ReportDataFile(new File(settingsDirectory,
                                             "DaylightChartReport.jrxml"));
    reportFile.loadWithFallback();

    if (reportFile.getData() == null)
    {
      throw new RuntimeException("Cannot load report");
    }
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
    reportFile.setData(report);
    reportFile.save();
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
