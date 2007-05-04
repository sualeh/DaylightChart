package daylightchart;


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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import daylightchart.chart.DaylightChart;
import daylightchart.location.Location;
import daylightchart.location.LocationsSortOrder;
import daylightchart.location.parser.FormatterException;
import daylightchart.location.parser.LocationFormatter;
import daylightchart.location.parser.LocationParser;
import daylightchart.location.parser.ParserException;
import daylightchart.options.Options;
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

  private static final Preferences preferences = Preferences
    .userNodeForPackage(UserPreferences.class);

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

    // Sort by current user preferences
    LocationsSortOrder locationsSortOrder = UserPreferences.getOptions()
      .getLocationsSortOrder();
    Collections.sort(locations, locationsSortOrder);

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
      throw new ClassCastException(e.getMessage());
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Could get chart options", e);
      options = getDefaultDaylightChartOptions();
    }
    return options;
  }

  /**
   * Main method. Lists all user preferences.
   * 
   * @param args
   *        Command line arguments
   */
  public static void main(final String[] args)
  {
    UserPreferences.listAllPreferences();
  }

  /**
   * Set the default directory for data files.
   * 
   * @param dataFileDirectory
   *        Default directory for data files
   */
  public static void setDataFileDirectory(final File dataFileDirectory)
  {
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

  static void listAllPreferences()
  {
    System.out.println("User preferences:");
    try
    {
      preferences.exportNode(System.out);
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Could list preferences", e);
    }
    catch (final BackingStoreException e)
    {
      LOGGER.log(Level.WARNING, "Could list preferences", e);
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

    return options;
  }

  private UserPreferences()
  {
    // prevent external instantiation
  }

}
