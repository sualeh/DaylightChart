/*
 *
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2016, Sualeh Fatehi.
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


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
   *
   * @param settingsDirectory
   *        Settings directory
   */
  public RecentLocationsDataFile(final Path settingsDirectory)
  {
    super(settingsDirectory, "recent.locations.data", LocationFileType.data);
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

    List<Location> data = new ArrayList<>(this.data);
    if (data.contains(location))
    {
      data.remove(location);
    }
    data.add(location);

    if (data.size() > NUMBER_RECENT_LOCATIONS)
    {
      data = data.subList(0, NUMBER_RECENT_LOCATIONS);
    }
    this.data = data;

    save();
  }

  @Override
  protected void loadWithFallback()
  {
    load();
    if (data == null)
    {
      data = new ArrayList<Location>();
    }
  }

}
