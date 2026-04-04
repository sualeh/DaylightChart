/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.parser.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/** {@link ResourceRef} backed by a {@link java.nio.file.Path} on the default file system. */
final class FileResourceRef implements ResourceRef {

  private final Path path;

  FileResourceRef(final Path path) {
    this.path = Objects.requireNonNull(path, "Path is required");
  }

  @Override
  public boolean exists() {
    return Files.exists(path);
  }

  @Override
  public String location() {
    return path.toUri().toString();
  }

  @Override
  public InputStream openStream() throws IOException {
    return Files.newInputStream(path);
  }
}
