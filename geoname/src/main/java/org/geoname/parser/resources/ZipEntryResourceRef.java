/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.parser.resources;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 * {@link ResourceRef} backed by a zip or jar file on the file system and a relative entry path
 * within that archive.
 */
final class ZipEntryResourceRef implements ResourceRef {

  private static String normalizeEntryPath(final String path) {
    return path.startsWith("/") ? path : "/" + path;
  }

  private final Path jarPath;

  private final String entryPath;

  ZipEntryResourceRef(final Path jarPath, final String entryPath) {
    this.jarPath = Objects.requireNonNull(jarPath, "Jar path is required");
    this.entryPath =
        normalizeEntryPath(Objects.requireNonNull(entryPath, "Entry path is required"));
  }

  @Override
  public boolean exists() {
    try (FileSystem fs = FileSystems.newFileSystem(jarPath, Map.of())) {
      return Files.exists(fs.getPath(entryPath));
    } catch (final IOException e) {
      return false;
    }
  }

  @Override
  public String location() {
    return "jar:" + jarPath.toUri() + "!" + entryPath;
  }

  @Override
  public InputStream openStream() throws IOException {
    final FileSystem fs = FileSystems.newFileSystem(jarPath, Map.of());
    try {
      final InputStream stream = Files.newInputStream(fs.getPath(entryPath));
      // Wrap to close the FileSystem when the stream is closed
      return new FilterInputStream(stream) {
        @Override
        public void close() throws IOException {
          try {
            super.close();
          } finally {
            fs.close();
          }
        }
      };
    } catch (final IOException e) {
      fs.close();
      throw e;
    }
  }
}
