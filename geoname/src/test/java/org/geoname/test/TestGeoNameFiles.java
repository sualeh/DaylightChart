/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Logger;
import org.geoname.data.Location;
import org.geoname.parser.GNISFileParser;
import org.geoname.parser.GNSCountryFileParser;
import org.geoname.parser.ParserException;
import org.geoname.parser.resources.ResourceRef;
import org.geoname.parser.resources.ResourceRefs;
import org.geoname.timezones.DefaultTimezones;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestGeoNameFiles {

  @BeforeAll
  public static void turnOffLogs() {
    final Logger[] loggers = {
      Logger.getLogger(DefaultTimezones.class.getName()),
      Logger.getLogger("org.geoname.parser.BaseDelimitedLocationsFileParser")
    };
    for (final Logger logger : loggers) {
      logger.setUseParentHandlers(false);
      final Handler[] handlers = logger.getHandlers();
      for (final Handler handler : handlers) {
        logger.removeHandler(handler);
      }
    }
  }

  @Test
  public void GNISUSStates() throws ParserException, IOException, URISyntaxException {
    parseGNISUSStates("MA", 364);
    parseGNISUSStates("HI", 130);
  }

  @Test
  public void GNSCountries() throws ParserException, IOException, URISyntaxException {
    parseGNSCountryFile("Uzbekistan.zip", 6392);
    parseGNSCountryFile("Slovakia.zip", 4830);
  }

  private void parseGNISUSStates(final String state, final int numLocations)
      throws ParserException, IOException, URISyntaxException {
    final String filename = state + ".zip";
    final URL zipUrl = this.getClass().getClassLoader().getResource(filename);
    final Path zipPath = Path.of(zipUrl.toURI());

    Collection<Location> locations = new ArrayList<>();
    final String entryName = firstZipEntryName(zipPath);
    if (entryName != null) {
      final ResourceRef ref = ResourceRefs.ofZipEntry(zipPath, entryName);
      locations = new GNISFileParser(ref).parseLocations();
    }

    assertThat(
        "Number of locations in file %s:%s".formatted(filename, entryName),
        locations.size(),
        is(numLocations));
  }

  private void parseGNSCountryFile(final String filename, final int numLocations)
      throws ParserException, IOException, URISyntaxException {
    final URL zipUrl = this.getClass().getClassLoader().getResource(filename);
    final Path zipPath = Path.of(zipUrl.toURI());

    Collection<Location> locations = new ArrayList<>();
    final String entryName = firstZipEntryName(zipPath);
    if (entryName != null) {
      final ResourceRef ref = ResourceRefs.ofZipEntry(zipPath, entryName);
      locations = new GNSCountryFileParser(ref).parseLocations();
    }

    assertThat(
        "Number of locations in file %s:%s".formatted(filename, entryName),
        locations.size(),
        is(numLocations));
  }

  private static String firstZipEntryName(final Path zipPath) throws IOException {
    try (FileSystem fs = FileSystems.newFileSystem(zipPath, Map.of())) {
      return Files.walk(fs.getPath("/"))
          .filter(Files::isRegularFile)
          .findFirst()
          .map(Path::toString)
          .orElse(null);
    }
  }
}
