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
public final class RecentLocationsDataFile
  extends BaseLocationsDataFile
{

  private static final int NUMBER_RECENT_LOCATIONS = 9;

  /**
   * Constructor.
   */
  RecentLocationsDataFile(final File settingsDirectory)
  {
    super(new File(settingsDirectory, "recent.locations.data"),
          LocationFileType.data);

    load();
    if (locations == null)
    {
      locations = new ArrayList<Location>();
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
    if (locations.contains(location))
    {
      locations.remove(location);
    }
    locations.add(0, location);

    if (locations.size() > NUMBER_RECENT_LOCATIONS)
    {
      locations = locations.subList(0, NUMBER_RECENT_LOCATIONS);
    }

    save();
  }

}
