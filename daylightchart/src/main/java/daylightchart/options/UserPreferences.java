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
package daylightchart.options;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import com.thoughtworks.xstream.XStream;

import daylightchart.daylightchart.chart.DaylightChart;
import daylightchart.daylightchart.layout.DaylightChartReport;
import daylightchart.location.Location;
import daylightchart.location.parser.FormatterException;
import daylightchart.location.parser.LocationFormatter;
import daylightchart.location.parser.LocationParser;
import daylightchart.location.parser.ParserException;
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

  private static boolean savePreferences = true;

  private static final File scratchDirectory;
  private static final File settingsDirectory;

  private static final File locationsDataFile;
  private static final File optionsFile;

  private static List<Location> locations;
  private static Options options;

  static
  {
    scratchDirectory = new File(System.getProperty("java.io.tmpdir"), ".");
    validateDirectory(scratchDirectory);

    settingsDirectory = new File(System.getProperty("user.home", "."),
                                 ".daylightchart");
    settingsDirectory.mkdirs();
    validateDirectory(settingsDirectory);

    locationsDataFile = new File(settingsDirectory, "locations.data");
    optionsFile = new File(settingsDirectory, "options.xml");

    locations = loadLocations();
    options = loadOptions();
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
    options = loadOptions();

    if (locationsDataFile.exists())
    {
      locationsDataFile.delete();
    }
    locations = loadLocations();
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
   * @return the workingDirectory
   */
  public static File getScratchDirectory()
  {
    return scratchDirectory;
  }

  /**
   * @return the applicationSettingsDirectory
   */
  public static File getSettingsDirectory()
  {
    settingsDirectory.mkdirs();
    return settingsDirectory;
  }

  public static boolean importReport(final File reportFile)
  {
    boolean imported = false;
    if (reportFile != null && reportFile.exists() && reportFile.canRead())
    {
      try
      {
        final InputStream reportStream = new BufferedInputStream(new FileInputStream(reportFile));
        final JasperReport jasperReport = compileReport(reportStream);
        if (jasperReport != null)
        {
          options.setJasperReport(jasperReport);
          setOptions(options); // Save options
          imported = true;
        }
      }
      catch (final FileNotFoundException e)
      {
        LOGGER.log(Level.WARNING, "Cannot load JasperReport", e);
      }
    }
    return imported;
  }

  /**
   * Whether to save preferences.
   * 
   * @return Whether to save preferences.
   */
  public static boolean isSavePreferences()
  {
    return savePreferences;
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

    if (!savePreferences)
    {
      return;
    }
    try
    {
      if (locationsDataFile.exists())
      {
        locationsDataFile.delete();
      }
      LocationFormatter.formatLocations(locations, locationsDataFile);
    }
    catch (final FormatterException e)
    {
      LOGGER.log(Level.WARNING, "Could not save user locations list", e);
    }
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
    saveOptions(options);
  }

  /**
   * Whether to save preferences.
   * 
   * @param savePreferences
   *        Whether to save preferences.
   */
  public static void setSavePreferences(final boolean savePreferences)
  {
    UserPreferences.savePreferences = savePreferences;
  }

  public static void setSlimUi(final boolean slimUi)
  {
    options.setSlimUi(slimUi);
    saveOptions(options);
  }

  public static void setWorkingDirectory(final File workingDirectory)
  {
    options.setWorkingDirectory(workingDirectory);
    saveOptions(options);
  }

  static JasperReport compileReport(final InputStream reportStream)
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
  }

  static JasperReport loadDefaultJasperReport()
  {
    final InputStream reportStream = DaylightChartReport.class
      .getResourceAsStream("/DaylightChartReport.jrxml");
    final JasperReport jasperReport = UserPreferences
      .compileReport(reportStream);
    if (jasperReport == null)
    {
      throw new RuntimeException("Cannot load default report");
    }
    return jasperReport;
  }

  /**
   * Creates a chart options instance.
   * 
   * @return Chart options
   */
  public static Options getDefaultDaylightChartOptions()
  {
    final ChartOptions chartOptions = new ChartOptions();
    chartOptions.copyFromChart(new DaylightChart());

    final Options options = new Options();
    options.setChartOptions(chartOptions);

    // Save the defaults
    setOptions(options);
    return options;
  }

  private static List<Location> loadLocations()
  {
    List<Location> locations;
    try
    {
      locations = LocationParser.parseLocations(locationsDataFile);
    }
    catch (final ParserException e)
    {
      LOGGER.log(Level.WARNING, "Could get locations", e);
      locations = null;
    }

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
        throw new IllegalStateException("Cannot read data from internal database",
                                        e);
      }
    }

    return locations;
  }

  private static Options loadOptions()
  {
    Options options = null;
    try
    {
      XStream xStream = new XStream();
      options = (Options) xStream.fromXML(new FileReader(optionsFile));
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could get options", e);
      options = null;
    }

    if (options == null)
    {
      options = getDefaultDaylightChartOptions();
    }

    return options;
  }

  private static void saveOptions(final Options options)
  {
    if (!savePreferences)
    {
      return;
    }

    try
    {
      XStream xStream = new XStream();
      xStream.toXML(options, new FileWriter(optionsFile));
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could save options", e);
    }
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
