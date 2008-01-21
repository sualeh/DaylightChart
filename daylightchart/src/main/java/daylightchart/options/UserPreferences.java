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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import daylightchart.daylightchart.chart.DaylightChart;
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
    optionsFile = new File(settingsDirectory, "options.ser");

    locations = loadLocations();
    options = loadOptions();
  }

  public static void setWorkingDirectory(final File workingDirectory)
  {
    options.setWorkingDirectory(workingDirectory);
    saveOptions(options);
  }

  public static void setSlimUi(final boolean slimUi)
  {
    options.setSlimUi(slimUi);
    saveOptions(options);
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

  private static void saveOptions(final Options options)
  {
    if (!savePreferences)
    {
      return;
    }

    try
    {
      final ObjectOutput out = new ObjectOutputStream(new FileOutputStream(optionsFile));
      out.writeObject(options);
      out.close();
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Could save options", e);
    }
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

  /**
   * Creates a chart options instance.
   * 
   * @return Chart options
   */
  private static Options getDefaultDaylightChartOptions()
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
      final ObjectInputStream in = new ObjectInputStream(new FileInputStream(optionsFile));
      options = (Options) in.readObject();
      in.close();
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
