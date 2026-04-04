/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options.service;

import daylightchart.options.persistence.LocationsDataFile;
import daylightchart.options.persistence.OptionsDataFile;
import daylightchart.options.persistence.RecentLocationsDataFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/** User preferences for the GUI. */
public final class UserPreferences {

  private static final Logger LOGGER = Logger.getLogger(UserPreferences.class.getName());

  private static final Path scratchDirectory;

  private static LocationsDataFile locationsFile;
  private static RecentLocationsDataFile recentLocationsFile;
  private static OptionsDataFile optionsFile;

  static {
    scratchDirectory = Path.of(System.getProperty("java.io.tmpdir"), ".");
    validateDirectory(scratchDirectory);

    initialize(PersistenceConfigurationService.configuration().resolvePreferencesDirectory());
  }

  /** Clears all user preferences. */
  public static void clear() {
    optionsFile.delete();
    locationsFile.delete();
    recentLocationsFile.delete();

    initialize(optionsFile.getDirectory());
  }

  /**
   * Set the location of the settings directory.
   *
   * @param settingsDir Location of the settings directory
   */
  public static void initialize(final Path settingsDir) {
    final Path settingsDirectory;
    if (settingsDir == null) {
      settingsDirectory =
          PersistenceConfigurationService.configuration().resolvePreferencesDirectory();
    } else {
      settingsDirectory = settingsDir;
    }

    try {
      Files.createDirectories(settingsDirectory);
      validateDirectory(settingsDirectory);
      LOGGER.fine("Created settings directory " + settingsDirectory);
    } catch (final IOException e) {
      LOGGER.log(Level.SEVERE, "Cannot create settings directory " + settingsDirectory, e);
    }

    optionsFile = new OptionsDataFile(settingsDirectory);
    locationsFile = new LocationsDataFile(settingsDirectory);
    recentLocationsFile = new RecentLocationsDataFile(settingsDirectory);
  }

  /**
   * Locations file.
   *
   * @return Locations file.
   */
  public static LocationsDataFile locationsFile() {
    return locationsFile;
  }

  /**
   * Options file.
   *
   * @return Options file.
   */
  public static OptionsDataFile optionsFile() {
    return optionsFile;
  }

  /**
   * Recent locations file.
   *
   * @return Recent locations file.
   */
  public static RecentLocationsDataFile recentLocationsFile() {
    return recentLocationsFile;
  }

  private static void validateDirectory(final Path directory) {
    final boolean isDirectoryValid =
        directory != null && Files.isDirectory(directory) && Files.isWritable(directory);
    if (!isDirectoryValid) {
      throw new IllegalArgumentException("Directory is not writable - " + directory);
    }
  }

  private UserPreferences() {
    // Prevent external instantiation
  }
}
