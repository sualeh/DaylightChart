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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import daylightchart.chart.DaylightChart;
import daylightchart.gui.options.ChartOptions;
import daylightchart.location.Location;
import daylightchart.location.parser.FormatterException;
import daylightchart.location.parser.LocationFormatter;
import daylightchart.location.parser.LocationParser;
import daylightchart.location.parser.ParserException;

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
  private static final String keyChartOptions = "daylightchart.chartOptions";
  private static final String keyDataFileDirectory = "daylightchart.dataFileDirectory";

  /**
   * Main method. Lists all user preferences.
   * 
   * @param args
   *        Command line arguments
   */
  public static void main(final String[] args)
  {
    new UserPreferences().listAllPreferences();
  }

  /**
   * Creates a chart options instance.
   * 
   * @return Chart options
   */
  private static ChartOptions getDefaultDaylightChartOptions()
  {
    final DaylightChart chart = new DaylightChart(null);
    chart.setTitle("");

    final ChartOptions chartOptions = new ChartOptions();
    chartOptions.copyFromChart(chart);

    return chartOptions;
  }

  private final Preferences preferences = Preferences.userNodeForPackage(this
    .getClass());

  /**
   * Clears all user preferences.
   */
  public void clear()
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
   * Gets the chart options for the current user.
   * 
   * @return Chart options
   */
  public ChartOptions getChartOptions()
  {
    ChartOptions chartOptions = null;
    final byte[] bytes = preferences.getByteArray(keyChartOptions, new byte[0]);
    try
    {
      // Deserialize from a byte array
      final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
      chartOptions = (ChartOptions) in.readObject();
      in.close();
    }
    catch (final ClassNotFoundException e)
    {
      throw new ClassCastException(e.getMessage());
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Could get chart options", e);
      chartOptions = getDefaultDaylightChartOptions();
    }
    return chartOptions;
  }

  /**
   * Get the default directory for data files.
   * 
   * @return Directory for data files
   */
  public File getDataFileDirectory()
  {
    final String dataFileDirectory = preferences.get(keyDataFileDirectory, ".");
    return new File(dataFileDirectory);
  }

  /**
   * Gets the locations for the current user.
   * 
   * @return Locations
   */
  public List<Location> getLocations()
  {

    final String locationsDataFileName = preferences.get(keyLocations, null);
    if (locationsDataFileName != null)
    {
      try
      {
        final File locationsDataFile = new File(locationsDataFileName);
        return LocationParser.parseLocations(locationsDataFile);
      }
      catch (final ParserException e)
      {
        LOGGER.log(Level.WARNING, "Could get locations", e);
      }
    }

    try
    {
      final InputStream dataStream = this.getClass().getClassLoader()
        .getResourceAsStream("locations.data");
      if (dataStream == null)
      {
        throw new IllegalStateException("Cannot read data from internal database");
      }
      final Reader reader = new InputStreamReader(dataStream);
      return LocationParser.parseLocations(reader);
    }
    catch (final ParserException e)
    {
      throw new IllegalStateException("Cannot read data from internal database",
                                      e);
    }

  }

  /**
   * Sets the chart options for the current user.
   * 
   * @param chartOptions
   *        Chart options
   */
  public void setChartOptions(final ChartOptions chartOptions)
  {
    byte[] bytes;
    try
    {
      // Serialize to a byte array
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final ObjectOutput out = new ObjectOutputStream(bos);
      out.writeObject(chartOptions);
      out.close();
      // Get the bytes of the serialized object
      bytes = bos.toByteArray();
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could set chart options", e);
      bytes = new byte[0];
    }
    preferences.putByteArray(keyChartOptions, bytes);
  }

  /**
   * Set the default directory for data files.
   * 
   * @param dataFileDirectory
   *        Default directory for data files
   */
  public void setDataFileDirectory(final File dataFileDirectory)
  {
    preferences.put(keyDataFileDirectory, dataFileDirectory.getAbsolutePath());
  }

  /**
   * Sets the locations for the current user.
   * 
   * @param locations
   *        Locations
   */
  public void setLocations(final List<Location> locations)
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

  void listAllPreferences()
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

}
