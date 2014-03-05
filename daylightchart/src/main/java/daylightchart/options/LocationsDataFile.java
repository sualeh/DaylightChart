/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2014, Sualeh Fatehi.
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


import java.io.File;
import java.io.InputStream;

import daylightchart.gui.actions.LocationFileType;

/**
 * Represents a location file, with data.
 * 
 * @author sfatehi
 */
public final class LocationsDataFile
  extends BaseLocationsDataFile
{

  /**
   * Constructor.
   * 
   * @param locationDataFile
   *        File
   */
  public LocationsDataFile(final BaseTypedFile<LocationFileType> locationDataFile)
  {
    this(locationDataFile.getFile(), locationDataFile.getFileType());
  }

  /**
   * Constructor.
   * 
   * @param settingsDirectory
   *        Settings directory
   */
  public LocationsDataFile(final File settingsDirectory)
  {
    super(settingsDirectory, "locations.data", LocationFileType.data);
  }

  /**
   * Constructor.
   * 
   * @param file
   *        File
   * @param fileType
   *        Location file type
   */
  public LocationsDataFile(final File file, final LocationFileType fileType)
  {
    super(file, fileType);

    // Validation
    if (file == null)
    {
      throw new IllegalArgumentException("No file provided");
    }
    if (fileType == null)
    {
      throw new IllegalArgumentException("No file type provided");
    }
  }

  /**
   * Loads a list of locations from a file of a given format, falling
   * back to an internal resource with the same name.
   */
  protected void loadWithFallback()
  {
    // 1. Load from file
    load();
    // 2. Load from internal store
    if (data == null)
    {
      final InputStream input = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(getFilename());
      load(input);
    }
    // 3. If no locations are loaded, fail
    if (data == null)
    {
      throw new RuntimeException("Cannot load locations");
    }
  }

}
