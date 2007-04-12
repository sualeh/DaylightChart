package daylightchart;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.pointlocation6709.Angle;
import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;

import daylightchart.chart.DaylightChart;
import daylightchart.chart.Location;
import daylightchart.gui.options.ChartOptions;
import daylightchart.locationparser.LocationFormatter;
import daylightchart.locationparser.LocationParser;
import daylightchart.locationparser.ParserException;

/**
 * User preferences for the GUI.
 * 
 * @author Sualeh Fatehi
 */
public final class UserPreferences
{

  private static final String keyLocations = "daylightchart.locations";
  private static final String keyChartOptions = "daylightchart.chartOptions";

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
    final PointLocation pointLocation = new PointLocation(new Latitude(Angle
      .fromDegrees(0)), new Longitude(Angle.fromDegrees(0)));
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
      chartOptions = getDefaultDaylightChartOptions();
    }
    return chartOptions;
  }

  /**
   * Gets the locations for the current user.
   * 
   * @return Locations
   */
  public List<Location> getLocations()
  {
    String locationsString = preferences.get(keyLocations, null);
    try
    {
      return LocationParser.parseLocations(locationsString);
    }
    catch (ParserException e)
    {
      return new ArrayList<Location>();
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
      e.printStackTrace();
      bytes = new byte[0];
    }
    preferences.putByteArray(keyChartOptions, bytes);
  }

  /**
   * Sets the locations for the current user.
   * 
   * @param locations
   *        Locations
   */
  public void setLocations(final List<Location> locations)
  {
    final String locationsString = LocationFormatter.formatLocations(locations);
    preferences.put(keyLocations, locationsString);
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
