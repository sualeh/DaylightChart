package daylightchart;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.pointlocation6709.Angle;
import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;

import daylightchart.chart.DaylightChart;
import daylightchart.chart.Location;
import daylightchart.gui.options.ChartOptions;

/**
 * User preferences for the GUI.
 * 
 * @author Sualeh Fatehi
 */
public final class UserPreferences
{

  private enum PreferenceKeys
  {
    locations("daylightchart.locations"), chartOptions(
      "daylightchart.chartOptions");

    private final String key;

    private PreferenceKeys(final String key)
    {
      this.key = key;
    }

    /**
     * @return the key
     */
    public String getKey()
    {
      return key;
    }

  }

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
    // Create a fake chart
    final PointLocation pointLocation = new PointLocation(new Latitude(new Angle()),
                                                          new Longitude(new Angle()));
    final Location location = new Location("", "", "", pointLocation);
    final DaylightChart chart = new DaylightChart(location);
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
      e.printStackTrace();
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
    final byte[] bytes = preferences.getByteArray(PreferenceKeys.chartOptions
      .getKey(), new byte[0]);
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
      chartOptions = getDefaultDaylightChartOptions();
    }
    return chartOptions;
  }

  /**
   * Gets the locations for the current user.
   * 
   * @return Locations
   */
  public String getLocations()
  {
    return preferences.get(PreferenceKeys.locations.getKey(), null);
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
      e.printStackTrace();
      bytes = new byte[0];
    }
    preferences.putByteArray(PreferenceKeys.chartOptions.getKey(), bytes);
  }

  /**
   * Sets the locations for the current user.
   * 
   * @param locations
   *        Locations
   */
  public void setLocations(final String locations)
  {
    preferences.put(PreferenceKeys.locations.getKey(), locations);
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
      e.printStackTrace();
    }
    catch (final BackingStoreException e)
    {
      e.printStackTrace();
    }
  }

}
