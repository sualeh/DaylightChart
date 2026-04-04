/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import daylightchart.options.persistence.RecentLocationsDataFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.geoname.data.Location;
import org.geoname.parser.LocationsListParser;
import org.junit.jupiter.api.Test;

class RecentLocationsDataFileTest {

  @Test
  void shouldDeduplicateAndCapRecentLocations() throws Exception {
    final Path settingsDirectory = Files.createTempDirectory("daylight-persistence-recent");
    final RecentLocationsDataFile recentLocations = new RecentLocationsDataFile(settingsDirectory);

    final List<Location> locations = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      locations.add(
          LocationsListParser.parseLocation(
              "City%d;;US;America/New_York;+420%d-0710%d/".formatted(i, i, i)));
    }
    for (final Location location : locations) {
      recentLocations.add(location);
    }
    recentLocations.add(locations.get(9));

    assertThat(recentLocations.getData(), hasSize(9));
    assertThat(recentLocations.getData().stream().distinct().count(), is(9L));
  }
}
