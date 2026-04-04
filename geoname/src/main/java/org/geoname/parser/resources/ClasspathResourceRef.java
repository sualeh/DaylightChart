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
import java.util.Objects;

/** {@link ResourceRef} backed by a {@link ClassLoader} and a resource name. */
final class ClasspathResourceRef implements ResourceRef {

  private final ClassLoader classLoader;
  private final String name;

  ClasspathResourceRef(final ClassLoader classLoader, final String name) {
    this.classLoader = Objects.requireNonNull(classLoader, "ClassLoader is required");
    this.name = Objects.requireNonNull(name, "Resource name is required");
  }

  @Override
  public boolean exists() {
    return classLoader.getResource(name) != null;
  }

  @Override
  public String location() {
    return "classpath:" + name;
  }

  @Override
  public InputStream openStream() throws IOException {
    final InputStream stream = classLoader.getResourceAsStream(name);
    if (stream == null) {
      throw new IOException("Classpath resource not found: " + name);
    }
    return stream;
  }
}
