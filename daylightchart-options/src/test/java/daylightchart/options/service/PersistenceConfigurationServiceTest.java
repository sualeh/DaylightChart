/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

class PersistenceConfigurationServiceTest {

  @Test
  void shouldReadRequestedLogLevelFromEnvironment() {
    final PersistenceConfigurationService service =
        new PersistenceConfigurationService(
            Map.of(PersistenceConfigurationService.ENV_LOG_LEVEL, "fine")::get, () -> Path.of("."));

    assertThat(service.readRequestedLogLevel(), is(Optional.of(Level.FINE)));
  }

  @Test
  void shouldResolvePreferencesDirectoryFromEnvironment() throws Exception {
    final Path settingsDirectory = Files.createTempDirectory("daylight-env-settings");
    final PersistenceConfigurationService service =
        new PersistenceConfigurationService(
            Map.of(
                    PersistenceConfigurationService.ENV_PREFERENCES_DIRECTORY,
                    settingsDirectory.toString())
                ::get,
            () -> Path.of("."));

    assertThat(service.resolvePreferencesDirectory(), is(settingsDirectory));
  }

  @Test
  void shouldResolvePreferencesDirectoryFromHomeDirectoryWhenEnvironmentIsMissing()
      throws Exception {
    final Path homeDirectory = Files.createTempDirectory("daylight-home");
    final PersistenceConfigurationService service =
        new PersistenceConfigurationService(name -> null, () -> homeDirectory);

    assertThat(service.resolvePreferencesDirectory(), is(homeDirectory.resolve(".daylightchart")));
  }

  @Test
  void shouldApplyConfiguredLogLevel() {
    final Logger rootLogger = Logger.getLogger("");
    final Level previousRootLevel = rootLogger.getLevel();
    final Handler[] handlers = rootLogger.getHandlers();
    final Level[] previousHandlerLevels = new Level[handlers.length];
    for (int i = 0; i < handlers.length; i++) {
      previousHandlerLevels[i] = handlers[i].getLevel();
    }

    final PersistenceConfigurationService service =
        new PersistenceConfigurationService(
            Map.of(PersistenceConfigurationService.ENV_LOG_LEVEL, "warning")::get,
            () -> Path.of("."));

    try {
      service.configureLoggingFromEnvironment();

      assertThat(rootLogger.getLevel(), is(Level.WARNING));
      for (final Handler handler : handlers) {
        assertThat(handler.getLevel(), is(Level.WARNING));
      }
    } finally {
      rootLogger.setLevel(previousRootLevel);
      for (int i = 0; i < handlers.length; i++) {
        handlers[i].setLevel(previousHandlerLevels[i]);
      }
    }
  }
}
