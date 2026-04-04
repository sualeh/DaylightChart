/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options.persistence;

import java.io.Writer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoname.data.Location;
import org.geoname.parser.GNISFileParser;
import org.geoname.parser.GNSCountryFileParser;
import org.geoname.parser.LocationFormatter;
import org.geoname.parser.LocationsListParser;
import org.geoname.parser.LocationsParser;
import org.geoname.parser.resources.ResourceRef;
import org.geoname.parser.resources.ResourceRefs;

/** Represents a location file, with data. */
abstract class BaseLocationsDataFile extends BaseDataFile<LocationFileType, Collection<Location>> {

  private static final Logger LOGGER = Logger.getLogger(BaseLocationsDataFile.class.getName());

  /**
   * Constructor.
   *
   * @param file File
   * @param fileType Location file type
   */
  protected BaseLocationsDataFile(final Path file, final LocationFileType fileType) {
    super(file, fileType);
  }

  /**
   * Constructor.
   *
   * @param settingsDirectory Settings directory
   * @param resource Resource
   * @param fileType File type
   */
  protected BaseLocationsDataFile(
      final Path settingsDirectory, final String resource, final LocationFileType fileType) {
    super(settingsDirectory, resource, fileType);
  }

  /** Loads a list of locations from a file of a given format. */
  @Override
  public final void load() {
    if (!exists()) {
      LOGGER.log(Level.WARNING, "No locations file provided");
      data = null;
      return;
    }

    final List<ResourceRef> refs = new ArrayList<>();
    final Path file = getFile();
    try {
      switch (getFileType()) {
        case data:
        case gns_country_file:
        case gnis_state_file:
          refs.add(ResourceRefs.ofFile(file));
          break;
        case gns_country_file_zipped:
          try (FileSystem fs = FileSystems.newFileSystem(file, Map.of())) {
            final String zipStem = stem(file.getFileName().toString());
            Files.walk(fs.getPath("/"))
                .filter(Files::isRegularFile)
                .filter(entry -> stem(entry.getFileName().toString()).equals(zipStem))
                .map(entry -> ResourceRefs.ofZipEntry(file, entry.toString()))
                .forEach(refs::add);
          }
          break;
        case gnis_state_file_zipped:
          try (FileSystem fs = FileSystems.newFileSystem(file, Map.of())) {
            Files.walk(fs.getPath("/"))
                .filter(Files::isRegularFile)
                .map(entry -> ResourceRefs.ofZipEntry(file, entry.toString()))
                .forEach(refs::add);
          }
          break;
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not read locations from " + file, e);
      data = null;
      return;
    }

    load(refs.toArray(new ResourceRef[0]));
  }

  @Override
  protected final void load(final ResourceRef... refs) {
    data = new HashSet<>();
    try {
      for (final ResourceRef ref : refs) {
        final LocationsParser locationsFileParser =
            switch (getFileType()) {
              case data -> new LocationsListParser(ref);
              case gns_country_file, gns_country_file_zipped -> new GNSCountryFileParser(ref);
              case gnis_state_file, gnis_state_file_zipped -> new GNISFileParser(ref);
              default -> null;
            };
        if (locationsFileParser != null) {
          data.addAll(locationsFileParser.parseLocations());
        }
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not read locations", e);
      data = null;
    }

    if (data != null && data.isEmpty()) {
      data = null;
    }
  }

  /** Saves locations to a file. */
  @Override
  protected final void save() {
    final Path file = getFile();
    if (file == null) {
      LOGGER.log(Level.WARNING, "No locations file provided");
      return;
    }

    try {
      Files.deleteIfExists(file);

      final Writer writer = getFileWriter(file);
      if (writer == null) {
        return;
      }

      LocationFormatter.formatLocations(data, writer);
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not save locations to " + file, e);
    }
  }

  /** Returns the filename stem — the name without its last extension. */
  private static String stem(final String filename) {
    final int dot = filename.lastIndexOf('.');
    return dot > 0 ? filename.substring(0, dot) : filename;
  }
}
