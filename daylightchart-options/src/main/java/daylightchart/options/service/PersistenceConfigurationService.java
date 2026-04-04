/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/** Configures persistence-related startup concerns such as logging and preferences location. */
public final class PersistenceConfigurationService {

  static final String ENV_LOG_LEVEL = "DAYLIGHTCHART_LOG_LEVEL";
  static final String ENV_PREFERENCES_DIRECTORY = "DAYLIGHTCHART_PREFERENCES_DIR";

  private static final Logger LOGGER =
      Logger.getLogger(PersistenceConfigurationService.class.getName());
  private static final PersistenceConfigurationService PERSISTENCE_CONFIGURATION_SERVICE =
      new PersistenceConfigurationService(
          System::getenv, () -> Path.of(System.getProperty("user.home", ".")));

  private final Function<String, String> environmentReader;
  private final Supplier<Path> homeDirectorySupplier;

  public static PersistenceConfigurationService configuration() {
    return PERSISTENCE_CONFIGURATION_SERVICE;
  }

  PersistenceConfigurationService(
      final Function<String, String> environmentReader,
      final Supplier<Path> homeDirectorySupplier) {
    this.environmentReader =
        Objects.requireNonNull(environmentReader, "Environment reader missing");
    this.homeDirectorySupplier =
        Objects.requireNonNull(homeDirectorySupplier, "Home directory supplier missing");
  }

  public void applyLogLevel(final Level logLevel) {
    Objects.requireNonNull(logLevel, "Log level must not be null");

    final LogManager logManager = LogManager.getLogManager();
    for (final Enumeration<String> loggerNames = logManager.getLoggerNames();
        loggerNames.hasMoreElements(); ) {
      final String loggerName = loggerNames.nextElement();
      final Logger logger = logManager.getLogger(loggerName);
      if (logger == null) {
        continue;
      }
      logger.setLevel(null);
      for (final Handler handler : logger.getHandlers()) {
        handler.setLevel(logLevel);
      }
    }

    Logger.getLogger("").setLevel(logLevel);
  }

  public void configureLoggingFromEnvironment() {
    readRequestedLogLevel().ifPresent(this::applyLogLevel);
  }

  public Optional<Level> readRequestedLogLevel() {
    final String logLevelString = environmentReader.apply(ENV_LOG_LEVEL);
    if (logLevelString == null || logLevelString.isBlank()) {
      return Optional.empty();
    }

    try {
      return Optional.of(Level.parse(logLevelString.trim().toUpperCase()));
    } catch (final IllegalArgumentException e) {
      LOGGER.log(
          Level.WARNING, "Ignoring invalid log level in " + ENV_LOG_LEVEL + ": " + logLevelString);
      return Optional.empty();
    }
  }

  public Optional<Path> readRequestedPreferencesDirectory() {
    final String preferencesDirectory = environmentReader.apply(ENV_PREFERENCES_DIRECTORY);
    if (preferencesDirectory == null || preferencesDirectory.isBlank()) {
      return Optional.empty();
    }

    try {
      return Optional.of(Path.of(preferencesDirectory.trim()));
    } catch (final RuntimeException e) {
      throw new IllegalArgumentException(
          "Invalid preferences directory in "
              + ENV_PREFERENCES_DIRECTORY
              + ": "
              + preferencesDirectory,
          e);
    }
  }

  public Path resolvePreferencesDirectory() {
    final Path settingsDirectory =
        readRequestedPreferencesDirectory()
            .orElseGet(() -> homeDirectorySupplier.get().resolve(".daylightchart"));
    validateDirectory(settingsDirectory);
    return settingsDirectory;
  }

  private void validateDirectory(final Path directory) {
    try {
      java.nio.file.Files.createDirectories(directory);
    } catch (final IOException e) {
      throw new IllegalArgumentException("Cannot create settings directory " + directory, e);
    }

    if (directory == null
        || !java.nio.file.Files.isDirectory(directory)
        || !java.nio.file.Files.isWritable(directory)) {
      throw new IllegalArgumentException("Directory is not writable - " + directory);
    }
  }
}
