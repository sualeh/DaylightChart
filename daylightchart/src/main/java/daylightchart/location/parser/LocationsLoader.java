/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
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
package daylightchart.location.parser;


import java.io.File;
import java.util.List;

import daylightchart.location.Location;
import daylightchart.options.UserPreferences;

/**
 * Loads locations from a file of any supported format.
 * 
 * @author Sualeh Fatehi
 */
public final class LocationsLoader
{

  /**
   * Attempts to load a locations file, trying each format in turn.
   * 
   * @param file
   *        File to load
   * @return List of locations, or null on error
   */
  public static List<Location> load(final File file)
  {
    List<Location> locations = null;

    // 1. Attempt to load as a Daylight Chart locations file
    if (locations == null)
    {
      try
      {
        locations = LocationParser.parseLocations(file);
      }
      catch (final ParserException e)
      {
        locations = null;
      }
    }
    if (locations != null && locations.size() == 0)
    {
      locations = null;
    }

    // 2. Attempt to load as a GNS country file
    if (locations == null)
    {
      try
      {
        locations = GNSCountryFilesParser.parseLocations(file);
      }
      catch (final ParserException e)
      {
        locations = null;
      }
    }
    if (locations != null && locations.size() == 0)
    {
      locations = null;
    }

    // 3. Attempt to load as a GNIS file
    if (locations == null)
    {
      try
      {
        locations = GNISFilesParser.parseLocations(file);
      }
      catch (final ParserException e)
      {
        locations = null;
      }
    }
    if (locations != null && locations.size() == 0)
    {
      locations = null;
    }

    UserPreferences.sortLocations(locations);

    return locations;
  }

  private LocationsLoader()
  {
  }

}
