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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import org.apache.commons.lang.StringUtils;
import org.geoname.data.Location;
import org.geoname.parser.FormatterException;
import org.geoname.parser.GNISFilesParser;
import org.geoname.parser.GNSCountryFilesParser;
import org.geoname.parser.LocationFormatter;
import org.geoname.parser.LocationParser;
import org.geoname.parser.ParserException;
import org.geoname.parser.UnicodeReader;

import com.thoughtworks.xstream.XStream;

import daylightchart.daylightchart.chart.DaylightChart;
import daylightchart.daylightchart.layout.DaylightChartReport;
import daylightchart.gui.actions.LocationFileType;
import daylightchart.gui.util.SelectedFile;
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
    if (optionsFile.exists())
    {
      optionsFile.delete();
    }

    if (locationsDataFile.exists())
    {
      locationsDataFile.delete();
    }

    if (recentLocationsDataFile.exists())
    {
      recentLocationsDataFile.delete();
    }

    if (reportFile.exists())
    {
      reportFile.delete();
    }

    if (settingsDirectory.exists())
    {
      settingsDirectory.delete();
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
    final JasperReport report = loadReportFromFile(reportFile);
    if (report != null)
    {
      setReport(report);
      imported = true;
    }
    return imported;
  }

  /**
   * Loads a list of locations from a file of a given format.
   * 
   * @param selectedFile
   *        Selected file of a known location file format.
   * @return List of locations, read from the file
   */
  public static List<Location> loadLocationsFromFile(final SelectedFile<LocationFileType> selectedFile)
  {
    List<Location> locations = new ArrayList<Location>();
    if (selectedFile == null || !selectedFile.isSelected())
    {
      return locations;
    }

    LocationFileType fileType = selectedFile.getFileType();
    List<InputStream> inputs = new ArrayList<InputStream>();
    try
    {
      switch (fileType)
      {
        case data:
        case gns_country_file:
        case gnis_state_file:
          inputs.add(new FileInputStream(selectedFile.getFile()));
          break;
        case gns_country_file_zipped:
        case gnis_state_file_zipped:
          ZipFile zipFile = new ZipFile(selectedFile.getFile());
          List<ZipEntry> zippedFiles = (List<ZipEntry>) Collections
            .list(zipFile.entries());
          for (ZipEntry zipEntry: zippedFiles)
          {
            inputs.add(zipFile.getInputStream(zipEntry));
          }
          break;
      }

      for (InputStream inputStream: inputs)
      {
        Reader reader = new UnicodeReader(inputStream, "UTF-8");
        switch (fileType)
        {
          case data:
            locations.addAll(LocationParser.parseLocations(reader));
            break;
          case gns_country_file:
          case gns_country_file_zipped:
            locations.addAll(GNSCountryFilesParser.parseLocations(reader));
            break;
          case gnis_state_file:
          case gnis_state_file_zipped:
            locations.addAll(GNISFilesParser.parseLocations(reader));
            break;
        }
        reader.close();
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING,
                 "Could not read locations from " + selectedFile,
                 e);
      locations = new ArrayList<Location>();
    }
    finally
    {
      for (InputStream inputStream: inputs)
      {
        if (inputStream != null)
        {
          try
          {
            inputStream.close();
          }
          catch (final IOException e)
          {
            LOGGER.log(Level.WARNING, "Could not close input stream", e);
          }
        }
      }
    }
    return locations;
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
   * Saves locations to a file.
   * 
   * @param locations
   *        Locations to save
   * @param file
   *        File
   */
  public static void saveLocationsToFile(final List<Location> locations,
                                         final File file)
  {
    try
    {
      if (file == null)
      {
        LOGGER.log(Level.WARNING, "No locations file provided");
        return;
      }
      if (file.exists())
      {
        file.delete();
      }
      final Writer writer = getFileWriter(file);
      if (writer == null)
      {
        LOGGER.log(Level.WARNING, "Could not save locations to " + file);
        return;
      }
      LocationFormatter.formatLocations(locations, writer);
    }
    catch (final FormatterException e)
    {
      LOGGER.log(Level.WARNING, "Could not save locations to " + file, e);
    }
  }

  /**
   * Saves report to a file.
   * 
   * @param file
   *        File to write
   */
  public static void saveReportToFile(final File file)
  {
    try
    {
      if (file.exists())
      {
        file.delete();
      }
      JRXmlWriter.writeReport(report, file.getAbsolutePath(), "UTF-8");
    }
    catch (final JRException e)
    {
      LOGGER.log(Level.WARNING, "Could not save report to " + file, e);
    }
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
    saveLocationsToFile(locations, locationsDataFile);
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
    saveOptionsToFile(optionsFile);
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

  private static JasperReport compileReport(final InputStream reportStream)
  {
    try
    {
      if (reportStream == null)
      {
        return null;
      }
      final JasperDesign jasperDesign = JRXmlLoader.load(reportStream);
      final JasperReport jasperReport = JasperCompileManager
        .compileReport(jasperDesign);
      return jasperReport;
    }
    catch (final JRException e)
    {
      LOGGER.log(Level.WARNING, "Cannot load JasperReport", e);
      return null;
    }
    finally
    {
      if (reportStream != null)
      {
        try
        {
          reportStream.close();
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, "Cannot close input stream", e);
        }
      }
    }
  }

  private static Writer getFileWriter(final File file)
  {
    try
    {
      final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
                                                                              "UTF-8"));
      return writer;
    }
    catch (final UnsupportedEncodingException e)
    {
      LOGGER.log(Level.WARNING, "Cannot write file " + file, e);
      return null;
    }
    catch (final FileNotFoundException e)
    {
      LOGGER.log(Level.WARNING, "Cannot write file " + file, e);
      return null;
    }
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
    try
    {
      // Try loading from user preferences
      locations = LocationParser
        .parseLocations(new UnicodeReader(new FileInputStream(locationsDataFile),
                                          "UTF-8"));
    }
    catch (Exception e)
    {
      LOGGER.log(Level.WARNING, "Cannot read locations from file "
                                + locationsDataFile, e);
      locations = null;
    }

    // Load from internal store
    if (locations == null)
    {
      final InputStream dataStream = Thread.currentThread()
        .getContextClassLoader().getResourceAsStream("locations.data");
      if (dataStream == null)
      {
        throw new IllegalStateException("Cannot read data from internal database");
      }
      final Reader reader = new InputStreamReader(dataStream);
      try
      {
        locations = LocationParser.parseLocations(reader);
      }
      catch (final ParserException e)
      {
        throw new RuntimeException("Cannot read data from internal database", e);
      }
    }
  }

  private static void initializeOptions()
  {
    // Try loading from user preferences
    options = loadOptionsFromFile(optionsFile);

    // Load from internal store
    if (options == null)
    {
      options = getDefaultDaylightChartOptions();
    }
  }

  private static void initializeRecentLocations()
  {
    try
    {
      // Try loading from user preferences
      recentLocations = new ArrayList<Location>();
      recentLocations
        .addAll(LocationParser
          .parseLocations(new UnicodeReader(new FileInputStream(recentLocationsDataFile),
                                            "UTF-8")));
    }
    catch (Exception e)
    {
      LOGGER.log(Level.WARNING, "Cannot read recent locations from file "
                                + recentLocationsDataFile, e);
      recentLocations = new ArrayList<Location>();
    }
  }

  private static void initializeReport()
  {
    // Try loading from user preferences
    report = loadReportFromFile(reportFile);

    // Load from internal store
    if (report == null)
    {
      final InputStream reportStream = DaylightChartReport.class
        .getResourceAsStream("/DaylightChartReport.jrxml");
      report = compileReport(reportStream);
      if (report == null)
      {
        throw new RuntimeException("Cannot load default report");
      }
    }
  }

  private static Options loadOptionsFromFile(final File file)
  {
    Reader reader = null;
    Options options;
    try
    {
      reader = new UnicodeReader(new FileInputStream(file), "UTF-8");
      final XStream xStream = new XStream();
      options = (Options) xStream.fromXML(reader);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not read options from " + file, e);
      options = null;
    }
    finally
    {
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, "Could not close " + file, e);
        }
      }
    }
    return options;
  }

  private static JasperReport loadReportFromFile(final File file)
  {
    if (file == null || !file.exists() || !file.canRead())
    {
      return null;
    }
    FileInputStream fileInputStream;
    try
    {
      fileInputStream = new FileInputStream(file);
      return compileReport(fileInputStream);
    }
    catch (final FileNotFoundException e)
    {
      LOGGER.log(Level.WARNING, "Could not read report from " + file, e);
      return null;
    }
  }

  /**
   * Saves options to a file.
   * 
   * @param file
   *        File to write
   */
  private static void saveOptionsToFile(final File file)
  {
    try
    {
      final Writer writer = getFileWriter(file);
      if (writer == null)
      {
        LOGGER.log(Level.WARNING, "Could not save options to " + file);
        return;
      }
      final XStream xStream = new XStream();
      xStream.toXML(options, writer);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could save options to " + file, e);
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
    saveLocationsToFile(recentLocations, recentLocationsDataFile);
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
    saveReportToFile(reportFile);
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
