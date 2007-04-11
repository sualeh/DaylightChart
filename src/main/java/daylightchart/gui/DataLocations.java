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
package daylightchart.gui;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import daylightchart.iso6709.Latitude;
import daylightchart.location.Location;
import daylightchart.location.LocationFormatter;
import daylightchart.location.LocationParser;
import daylightchart.location.ParserException;

/**
 * In-memory database of locations.
 * 
 * @author Sualeh Fatehi
 */
public final class DataLocations
{

  private static final long serialVersionUID = -2155899588206966572L;

  private List<Location> locations;

  /**
   * Loads data from internal database.
   */
  public DataLocations()
  {

    Reader reader = null;

    final UserPreferences userPreferences = new UserPreferences();
    final String locationsString = userPreferences.getLocations();
    if (locationsString != null)
    {
      reader = new StringReader(locationsString);
    }
    else
    {
      final InputStream dataStream = this.getClass().getClassLoader()
        .getResourceAsStream("locations.data");
      if (dataStream == null)
      {
        throw new IllegalStateException("Cannot read data from internal database");
      }
      reader = new InputStreamReader(dataStream);
    }
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

  /**
   * Loads data from external file.
   * 
   * @param dataFile
   *        Data file.
   * @throws ParserException
   * @throws IOException
   */
  public DataLocations(final File dataFile)
    throws IOException, ParserException
  {
    if (!dataFile.exists() || !dataFile.canRead())
    {
      throw new IllegalArgumentException("Cannot read data from "
                                         + dataFile.getAbsolutePath());
    }

    final FileReader reader = new FileReader(dataFile);
    locations = LocationParser.parseLocations(reader);

    // Save locations to user preferences
    final String locationsString = LocationFormatter.formatLocations(locations);
    final UserPreferences userPreferences = new UserPreferences();
    userPreferences.setLocations(locationsString);

  }

  /**
   * Gets a list of locations.
   * 
   * @return Locations list.
   */
  public List<Location> getLocations()
  {
    return new ArrayList<Location>(locations);
  }

  /**
   * Sorts locations, given a sort order.
   * 
   * @param sortOrder
   *        Sort order.
   */
  public void sortLocations(final LocationsSortOrder sortOrder)
  {
    Comparator<Location> comparator = null;
    if (sortOrder == LocationsSortOrder.BY_NAME)
    {
      // Do nothing special
    }
    else if (sortOrder == LocationsSortOrder.BY_LATITUDE)
    {
      comparator = new Comparator<Location>()
      {

        public int compare(final Location location1, final Location location2)
        {
          final Latitude latitude1 = location1.getPointLocation().getLatitude();
          final Latitude latitude2 = location2.getPointLocation().getLatitude();
          return (int) Math.signum(latitude2.getRadians()
                                   - latitude1.getRadians());
        }
      };
    }
    Collections.sort(locations, comparator);
  }

  /**
   * Writes location data out to a file.
   * 
   * @param dataFile
   *        File to write to.
   * @throws IOException
   *         On an exception.
   */
  public void writeDataToFile(final File dataFile)
    throws IOException
  {
    final BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));
    LocationFormatter.formatLocations(locations, writer);
  }

}
