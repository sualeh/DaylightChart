/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options.persistence;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.geoname.data.Location;

/** Represents a location file, with data. */
public final class RecentLocationsDataFile extends BaseLocationsDataFile {

  private static final int NUMBER_RECENT_LOCATIONS = 9;

  /**
   * Constructor.
   *
   * @param settingsDirectory Settings directory
   */
  public RecentLocationsDataFile(final Path settingsDirectory) {
    super(settingsDirectory, "recent.locations.data", LocationFileType.data);
  }

  /**
   * Adds a recent location.
   *
   * @param location Location
   */
  public void add(final Location location) {
    if (location == null) {
      return;
    }

    List<Location> data = new ArrayList<>(this.data);
    if (data.contains(location)) {
      data.remove(location);
    }
    data.add(location);

    if (data.size() > NUMBER_RECENT_LOCATIONS) {
      data = data.subList(0, NUMBER_RECENT_LOCATIONS);
    }
    this.data = data;

    save();
  }

  @Override
  protected void loadWithFallback() {
    load();
    if (data == null) {
      data = new ArrayList<Location>();
    }
  }
}
