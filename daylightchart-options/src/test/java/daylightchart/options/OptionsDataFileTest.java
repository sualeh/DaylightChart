/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import daylightchart.options.persistence.OptionsDataFile;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class OptionsDataFileTest {

  @Test
  void shouldLoadFallbackAndPersistOptions() throws Exception {
    final Path settingsDirectory = Files.createTempDirectory("daylight-persistence-options");
    final Path workingDirectory = Files.createTempDirectory(settingsDirectory, "work");

    final OptionsDataFile optionsDataFile = new OptionsDataFile(settingsDirectory);
    final Options options = optionsDataFile.getData();
    options.setWorkingDirectory(workingDirectory);
    optionsDataFile.save(options);

    final OptionsDataFile reloaded = new OptionsDataFile(settingsDirectory);

    assertThat(reloaded.getData(), is(notNullValue()));
    assertThat(reloaded.getData().getWorkingDirectory(), is(workingDirectory));
  }
}
