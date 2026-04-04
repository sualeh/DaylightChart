/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import daylightchart.options.persistence.LocationsDataFile;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class LocationsDataFileTest {

  @Test
  void shouldLoadBundledLocationsFallback() throws Exception {
    final Path settingsDirectory = Files.createTempDirectory("daylight-persistence-locations");
    final LocationsDataFile locationsDataFile = new LocationsDataFile(settingsDirectory);

    assertThat(locationsDataFile.getData(), is(notNullValue()));
    assertThat(locationsDataFile.getData().size(), is(greaterThan(0)));
  }
}
