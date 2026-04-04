/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.options;

import daylightchart.options.persistence.BaseDataFile;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoname.parser.resources.ResourceRef;
import org.geoname.parser.resources.ResourceRefs;
import tools.jackson.dataformat.yaml.YAMLMapper;

/** Persists chart options in a dedicated YAML file. */
final class ChartOptionsDataFile extends BaseDataFile<ChartOptionsFileType, ChartOptions> {

  private static final YAMLMapper YAML_MAPPER = new YAMLMapper();
  private static final Logger LOGGER = Logger.getLogger(ChartOptionsDataFile.class.getName());
  private static final ChartOptionsService CHART_OPTIONS_SERVICE = new ChartOptionsService();

  ChartOptionsDataFile(final Path settingsDirectory) {
    super(settingsDirectory, "chart-options.yaml", new ChartOptionsFileType());
  }

  @Override
  protected void load() {
    if (!exists()) {
      return;
    }
    load(ResourceRefs.ofFile(getFile()));
  }

  @Override
  protected void load(final ResourceRef... refs) {
    if (refs == null || refs.length == 0) {
      return;
    }

    try (Reader reader = new InputStreamReader(refs[0].openStream(), "UTF-8")) {
      data = YAML_MAPPER.readValue(reader, ChartOptions.class);
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not read chart options", e);
      data = null;
    }
  }

  @Override
  protected void loadWithFallback() {
    load();
    if (data == null) {
      data = CHART_OPTIONS_SERVICE.createDefaultChartOptions();
      save();
    }
  }

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
      LOGGER.log(Level.WARNING, "Could not save chart options to " + getFile(), e);
    }
  }
}
