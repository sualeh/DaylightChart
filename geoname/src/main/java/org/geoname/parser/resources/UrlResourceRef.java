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
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/** {@link ResourceRef} backed by a {@link java.net.URL}. */
final class UrlResourceRef implements ResourceRef {

  private final URL url;

  UrlResourceRef(final URL url) {
    this.url = Objects.requireNonNull(url, "URL is required");
  }

  @Override
  public boolean exists() {
    try {
      // Use Files.exists for file: URLs to avoid OS-level file locks (especially on
      // Windows)
      if ("file".equals(url.getProtocol())) {
        return Files.exists(Path.of(url.toURI()));
      }
      final URLConnection connection = url.openConnection();
      if (connection instanceof final HttpURLConnection http) {
        try {
          http.setRequestMethod("HEAD");
          http.connect();
          return http.getResponseCode() < 400;
        } finally {
          http.disconnect();
        }
      }
      connection.connect();
      return true;
    } catch (final IOException | URISyntaxException e) {
      return false;
    }
  }

  @Override
  public String location() {
    return "url:" + url.toExternalForm();
  }

  @Override
  public InputStream openStream() throws IOException {
    final URLConnection connection = url.openConnection();
    final InputStream stream = connection.getInputStream();
    // Wrap to ensure HTTP connections are disconnected when the stream is closed
    if (connection instanceof final HttpURLConnection http) {
      return new FilterInputStream(stream) {
        @Override
        public void close() throws IOException {
          try {
            super.close();
          } finally {
            http.disconnect();
          }
        }
      };
    }
    return stream;
  }
}
