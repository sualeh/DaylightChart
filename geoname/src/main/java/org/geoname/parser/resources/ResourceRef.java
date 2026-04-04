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

/**
 * Lightweight abstraction for a readable resource. Implementations hold only a reference to the
 * resource location; no {@link InputStream} is opened until {@link #openStream()} is called.
 */
public interface ResourceRef {

  /**
   * Returns {@code true} if the underlying resource exists and can be read. Does not open an {@link
   * InputStream}.
   *
   * @return {@code true} if the resource exists
   */
  boolean exists();

  /**
   * Returns a stable string identifier for this resource, for example {@code classpath:foo.txt},
   * {@code file:/path/to/file}, {@code url:https://...}, or {@code jar:file:/path/to/jar!/entry}.
   *
   * @return stable location string; never {@code null}
   */
  String location();

  /**
   * Opens and returns a new {@link InputStream} for the resource. The caller is responsible for
   * closing the stream.
   *
   * @return a fresh {@link InputStream}; never {@code null}
   * @throws IOException if the resource does not exist or cannot be opened
   */
  InputStream openStream() throws IOException;
}
