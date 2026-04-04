/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options.persistence;

import java.nio.file.Path;
import org.geoname.parser.resources.ResourceRefs;

/** Represents a location file, with data. */
public final class LocationsDataFile extends BaseLocationsDataFile {

  /**
   * Constructor.
   *
   * @param locationDataFile File
   */
  public LocationsDataFile(final BaseTypedFile<LocationFileType> locationDataFile) {
    this(locationDataFile.getFile(), locationDataFile.getFileType());
  }

  /**
   * Constructor.
   *
   * @param settingsDirectory Settings directory
   */
  public LocationsDataFile(final Path settingsDirectory) {
    super(settingsDirectory, "locations.data", LocationFileType.data);
  }

  /**
   * Constructor.
   *
   * @param file File
   * @param fileType Location file type
   */
  public LocationsDataFile(final Path file, final LocationFileType fileType) {
    super(file, fileType);

    // Validation
    if (file == null) {
      throw new IllegalArgumentException("No file provided");
    }
    if (fileType == null) {
      throw new IllegalArgumentException("No file type provided");
    }
  }

  /**
   * Loads a list of locations from a file of a given format, falling back to an internal resource
   * with the same name.
   */
  @Override
  protected void loadWithFallback() {
    // 1. Load from file
    load();
    // 2. Load from internal store
    if (data == null) {
      load(ResourceRefs.ofClasspath(getFilename()));
    }
    // 3. If no locations are loaded, fail
    if (data == null) {
      throw new RuntimeException("Cannot load locations");
    }
  }
}
