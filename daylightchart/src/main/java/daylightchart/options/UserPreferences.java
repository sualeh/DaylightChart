/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2015, Sualeh Fatehi.
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
import java.util.logging.Logger;

/**
 * User preferences for the GUI.
 * 
 * @author Sualeh Fatehi
 */
public final class UserPreferences
{

  private static final Logger LOGGER = Logger.getLogger(UserPreferences.class
    .getName());

  private static final File scratchDirectory;

  private static LocationsDataFile locationsFile;
  private static RecentLocationsDataFile recentLocationsFile;
  private static OptionsDataFile optionsFile;

  static
  {
    scratchDirectory = new File(System.getProperty("java.io.tmpdir"), ".");
    validateDirectory(scratchDirectory);

    initialize((File) null);
  }

  /**
   * Clears all user preferences.
   */
  public static void clear()
  {
    optionsFile.delete();
    locationsFile.delete();
    recentLocationsFile.delete();

    initialize(optionsFile.getDirectory());
  }

  /**
   * Gets the scratch directory.
   * 
   * @return Gets the scratch directory
   */
  public static File getScratchDirectory()
  {
    return scratchDirectory;
  }

  /**
   * Set the location of the settings directory.
   * 
   * @param settingsDir
   *        Location of the settings directory
   */
  public static void initialize(final File settingsDir)
  {
    final File settingsDirectory;
    if (settingsDir == null)
    {
      settingsDirectory = new File(System.getProperty("user.home", "."),
                                   ".daylightchart");
    }
    else
    {
      settingsDirectory = settingsDir;
    }

    settingsDirectory.mkdirs();
    validateDirectory(settingsDirectory);
    LOGGER.fine("Created settings directory " + settingsDirectory);

    optionsFile = new OptionsDataFile(settingsDirectory);
    locationsFile = new LocationsDataFile(settingsDirectory);
    recentLocationsFile = new RecentLocationsDataFile(settingsDirectory);
  }

  /**
   * Locations file.
   * 
   * @return Locations file.
   */
  public static LocationsDataFile locationsFile()
  {
    return locationsFile;
  }

  /**
   * Main method. Lists all user preferences.
   * 
   * @param args
   *        Command line arguments
   * @throws Exception
   *         On an exception
   */
  public static void main(final String[] args)
    throws Exception
  {
    UserPreferences.clear();
  }

  /**
   * Options file.
   * 
   * @return Options file.
   */
  public static OptionsDataFile optionsFile()
  {
    return optionsFile;
  }

  /**
   * Recent locations file.
   * 
   * @return Recent locations file.
   */
  public static RecentLocationsDataFile recentLocationsFile()
  {
    return recentLocationsFile;
  }

  private static void validateDirectory(final File directory)
  {
    final boolean isDirectoryValid = directory != null && directory.exists()
                                     && directory.isDirectory()
                                     && directory.canWrite();
    if (!isDirectoryValid)
    {
      throw new IllegalArgumentException("Directory is not writable - "
                                         + directory);
    }
  }

  private UserPreferences()
  {
    // Prevent external instantiation
  }

}
