/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options.persistence;

import daylightchart.options.Options;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoname.parser.resources.ResourceRef;
import org.geoname.parser.resources.ResourceRefs;
import tools.jackson.dataformat.yaml.YAMLMapper;

/** Represents a location file, with data. */
public final class OptionsDataFile extends BaseDataFile<OptionsFileType, Options> {

  private static final YAMLMapper YAML_MAPPER = new YAMLMapper();

  private static final Logger LOGGER = Logger.getLogger(OptionsDataFile.class.getName());

  /**
   * Constructor.
   *
   * @param settingsDirectory Settings directory
   */
  public OptionsDataFile(final Path settingsDirectory) {
    super(settingsDirectory, "options.yaml", new OptionsFileType());
  }

  /** Loads options from a file. */
  @Override
  protected void load() {
    if (!exists()) {
      LOGGER.log(Level.WARNING, "No options file provided");
      return;
    }
    load(ResourceRefs.ofFile(getFile()));
  }

  @Override
  protected void load(final ResourceRef... refs) {
    if (refs == null || refs.length == 0) {
      return;
    }

    Reader reader = null;
    try {
      final InputStream input = refs[0].openStream();
      reader = new InputStreamReader(input, "UTF-8");
      data = YAML_MAPPER.readValue(reader, Options.class);
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not read options", e);
      data = null;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (final IOException e) {
          LOGGER.log(Level.WARNING, "Could not close stream", e);
        }
      }
    }
  }

  @Override
  protected void loadWithFallback() {
    load();
    if (data == null) {
      data = new Options();
    }
  }

  /**
   * Saves options to a file.
   *
   * @param file File to write
   * @param options Options
   */
  @Override
  protected void save() {
    try {
      delete();
      try (Writer writer = getFileWriter(getFile())) {
        if (writer == null) {
          return;
        }

        YAML_MAPPER.writeValue(writer, data);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not save options to " + getFile(), e);
    }
  }
}
