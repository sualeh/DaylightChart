package daylightchart.options;


import java.io.File;
import java.util.ArrayList;

import org.geoname.data.Location;

import daylightchart.gui.actions.LocationFileType;

/**
 * Represents a location file, with data.
 * 
 * @author sfatehi
 */
final class RecentLocationsDataFile
  extends BaseLocationsDataFile
{

  private static final int NUMBER_RECENT_LOCATIONS = 9;

  /**
   * Constructor.
   * 
   * @param settingsDirectory
   *        Settings directory
   */
  RecentLocationsDataFile(final File settingsDirectory)
  {
    super(new File(settingsDirectory, "recent.locations.data"),
          LocationFileType.data);
    // Validation
    if (!getFile().isDirectory() || !getFile().exists())
    {
      throw new IllegalArgumentException("Settings directory is not a directory");
    }
  }

  public void loadWithFallback()
  {
    load();
    if (data == null)
    {
      data = new ArrayList<Location>();
    }
  }

  /**
   * Adds a recent location.
   * 
   * @param location
   *        Location
   */
  public void add(final Location location)
  {
    if (location == null)
    {
      return;
    }
    if (data.contains(location))
    {
      data.remove(location);
    }
    data.add(0, location);

    if (data.size() > NUMBER_RECENT_LOCATIONS)
    {
      data = data.subList(0, NUMBER_RECENT_LOCATIONS);
    }

    save();
  }

}
