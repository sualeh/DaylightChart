/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.test.desktop.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import daylightchart.gui.util.ExtensionFileFilter;
import daylightchart.options.persistence.LocationFileType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExtensionFileFilterTest {

  @TempDir Path tempDir;

  @Test
  void shouldExtractExtensionFromFileWithExtension() {
    assertThat(ExtensionFileFilter.getExtension(new File("report.data")), is(".data"));
  }

  @Test
  void shouldNormalizeExtensionToLowerCase() {
    assertThat(ExtensionFileFilter.getExtension(new File("report.DATA")), is(".data"));
  }

  @Test
  void shouldReturnDotOnlyForFileWithNoExtension() {
    assertThat(ExtensionFileFilter.getExtension(new File("report")), is("."));
  }

  @Test
  void shouldReturnDotOnlyForHiddenFileStartingWithDot() {
    assertThat(ExtensionFileFilter.getExtension(new File(".hidden")), is("."));
  }

  @Test
  void shouldAcceptDirectory() throws IOException {
    final ExtensionFileFilter<LocationFileType> filter =
        new ExtensionFileFilter<>(LocationFileType.data);
    final File dir = Files.createTempDirectory(tempDir, "testdir").toFile();

    assertThat(filter.accept(dir), is(true));
  }

  @Test
  void shouldAcceptFileWithMatchingExtension() throws IOException {
    final ExtensionFileFilter<LocationFileType> filter =
        new ExtensionFileFilter<>(LocationFileType.data);
    final File file = Files.createTempFile(tempDir, "locations", ".data").toFile();

    assertThat(filter.accept(file), is(true));
  }

  @Test
  void shouldRejectFileWithNonMatchingExtension() throws IOException {
    final ExtensionFileFilter<LocationFileType> filter =
        new ExtensionFileFilter<>(LocationFileType.data);
    final File file = Files.createTempFile(tempDir, "locations", ".txt").toFile();

    assertThat(filter.accept(file), is(false));
  }

  @Test
  void shouldProvideDescriptionFromFileType() {
    final ExtensionFileFilter<LocationFileType> filter =
        new ExtensionFileFilter<>(LocationFileType.data);

    assertThat(filter.getDescription(), is(LocationFileType.data.getDescription()));
  }

  @Test
  void shouldProvideFileExtensionFromFileType() {
    final ExtensionFileFilter<LocationFileType> filter =
        new ExtensionFileFilter<>(LocationFileType.data);

    assertThat(filter.getFileExtension(), is(".data"));
  }
}
