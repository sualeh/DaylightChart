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

import daylightchart.options.service.UserPreferencesService;
import java.nio.file.Files;
import java.nio.file.Path;
import org.geoname.data.Location;
import org.geoname.parser.LocationsListParser;
import org.junit.jupiter.api.Test;

class UserPreferencesServiceTest {

  @Test
  void shouldInitializeAndPersistOptions() throws Exception {
    final UserPreferencesService service = new UserPreferencesService();
    final Path settingsDirectory = Files.createTempDirectory("daylight-service-preferences");
    final Path workingDirectory = Files.createTempDirectory(settingsDirectory, "work");

    service.initialize(settingsDirectory);
    final Options options = service.loadOptions();
    options.setWorkingDirectory(workingDirectory);
    service.saveOptions(options);

    assertThat(service.loadOptions(), is(notNullValue()));
    assertThat(service.loadOptions().getWorkingDirectory(), is(workingDirectory));
    assertThat(service.getLocations().size(), is(greaterThan(0)));
  }

  @Test
  void shouldTrackRecentLocations() throws Exception {
    final UserPreferencesService service = new UserPreferencesService();
    service.initialize(Files.createTempDirectory("daylight-service-recent"));

    final Location location =
        LocationsListParser.parseLocation("Boston;US-MA;US;America/New_York;+4232-07104/");
    service.addRecentLocation(location);

    assertThat(service.getRecentLocations().contains(location), is(true));
  }
}
