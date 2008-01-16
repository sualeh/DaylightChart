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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

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

  private static final String keyLocations = "daylightchart.locations";
  private static final String keyOptions = "daylightchart.options";
  private static final String keyDataFileDirectory = "daylightchart.dataFileDirectory";
  private static final String keySlimUi = "daylightchart.slimUi";

  private static final Preferences preferences = Preferences
    .userNodeForPackage(UserPreferences.class);
  private static boolean savePreferences = true;
  private static File workingDirectory = new File(System
    .getProperty("java.io.tmpdir"));

  /**
   * Clears all user preferences.
   */
  public static void clear()
  {
    try
    {
      preferences.clear();
    }
    catch (final BackingStoreException e)
    {
      LOGGER.log(Level.WARNING, "Could clear preferences", e);
    }
  }

  /**
   * Get the default directory for data files.
   * 
   * @return Directory for data files
   */
  public static File getDataFileDirectory()
  {
    final String dataFileDirectory = preferences.get(keyDataFileDirectory, ".");
    return new File(dataFileDirectory);
  }

  /**
   * Gets the locations for the current user.
   * 
   * @return Locations
   */
  public static List<Location> getLocations()
  {

    List<Location> locations = null;

    final String locationsDataFileName = preferences.get(keyLocations, null);
    if (locationsDataFileName != null)
    {
      try
      {
        final File locationsDataFile = new File(locationsDataFileName);
        locations = LocationParser.parseLocations(locationsDataFile);
      }
      catch (final ParserException e)
      {
        LOGGER.log(Level.WARNING, "Could get locations", e);
        locations = null;
      }
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

  /**
   * Gets the options for the current user.
   * 
   * @return Options
   */
  public static Options getOptions()
  {
    Options options = null;
    final byte[] bytes = preferences.getByteArray(keyOptions, new byte[0]);
    try
    {
      // Deserialize from a byte array
      final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
      options = (Options) in.readObject();
      in.close();
    }
    catch (final ClassNotFoundException e)
    {
      LOGGER.log(Level.WARNING, "Could get chart options", e);
      options = getDefaultDaylightChartOptions();
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.FINE, "Could get chart options", e);
      options = getDefaultDaylightChartOptions();
    }
    return options;
  }

  /**
   * @return the workingDirectory
   */
  public static File getWorkingDirectory()
  {
    return workingDirectory;
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
   * Get the the slim UI option.
   * 
   * @return Directory for data files
   */
  public static boolean isSlimUi()
  {
    final boolean slimUi = preferences.getBoolean(keySlimUi, false);
    return slimUi;
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
    System.out.println("User preferences:");
    UserPreferences.preferences.exportNode(System.out);
    UserPreferences.clear();
  }

  /**
   * Set the default directory for data files.
   * 
   * @param dataFileDirectory
   *        Default directory for data files
   */
  public static void setDataFileDirectory(final File dataFileDirectory)
  {
    if (!savePreferences)
    {
      return;
    }
    preferences.put(keyDataFileDirectory, dataFileDirectory.getAbsolutePath());
  }

  /**
   * Sets the locations for the current user.
   * 
   * @param locations
   *        Locations
   */
  public static void setLocations(final List<Location> locations)
  {
    if (!savePreferences)
    {
      return;
    }
    try
    {
      // Delete previous locations file
      final String previousFileName = preferences.get(keyLocations, null);
      if (previousFileName != null)
      {
        final File previousFile = new File(previousFileName);
        if (previousFile.exists())
        {
          previousFile.delete();
        }
      }
      // Create a new locations file
      final File locationsDataFile = File
        .createTempFile("daylightchart.locations.", ".data");
      LocationFormatter.formatLocations(locations, locationsDataFile);
      preferences.put(keyLocations, locationsDataFile.getAbsolutePath());
    }
    catch (final FormatterException e)
    {
      LOGGER.log(Level.WARNING, "Could not save user locations list", e);
    }
    catch (final IOException e)
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
    if (!savePreferences)
    {
      return;
    }
    byte[] bytes;
    try
    {
      // Serialize to a byte array
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final ObjectOutput out = new ObjectOutputStream(bos);
      out.writeObject(options);
      out.close();
      // Get the bytes of the serialized object
      bytes = bos.toByteArray();
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could set chart options", e);
      bytes = new byte[0];
    }
    preferences.putByteArray(keyOptions, bytes);
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
   * Set the slim UI perference.
   * 
   * @param slimUi
   *        Whether to use a slim UI
   */
  public static void setSlimUi(final boolean slimUi)
  {
    if (!savePreferences)
    {
      return;
    }
    preferences.putBoolean(keySlimUi, slimUi);
  }

  /**
   * @param workingDirectory
   *        the workingDirectory to set
   */
  public static void setWorkingDirectory(final File workingDirectory)
  {
    final boolean isWorkingDirectoryValid = workingDirectory.exists()
                                            && workingDirectory.isDirectory()
                                            && workingDirectory.canWrite();
    if (isWorkingDirectoryValid)
    {
      UserPreferences.workingDirectory = workingDirectory;
    }
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

  private UserPreferences()
  {
    // Prevent external instantiation
  }

}
