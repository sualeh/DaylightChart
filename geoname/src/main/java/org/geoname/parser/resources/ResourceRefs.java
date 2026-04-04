/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.parser.resources;

import java.net.URL;
import java.nio.file.Path;

/** Factory methods for creating {@link ResourceRef} instances. */
public final class ResourceRefs {

  /**
   * Creates a {@link ResourceRef} for a classpath resource using the given class loader.
   *
   * @param classLoader class loader to use for lookup; must not be {@code null}
   * @param name resource name (e.g. {@code "data/foo.txt"}); must not be {@code null}
   * @return a {@link ResourceRef} for the classpath resource
   */
  public static ResourceRef ofClasspath(final ClassLoader classLoader, final String name) {
    return new ClasspathResourceRef(classLoader, name);
  }

  /**
   * Creates a {@link ResourceRef} for a classpath resource using the current thread's context class
   * loader, falling back to this class's class loader.
   *
   * @param name resource name (e.g. {@code "data/foo.txt"}); must not be {@code null}
   * @return a {@link ResourceRef} for the classpath resource
   */
  public static ResourceRef ofClasspath(final String name) {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if (cl == null) {
      cl = ResourceRefs.class.getClassLoader();
    }
    return new ClasspathResourceRef(cl, name);
  }

  /**
   * Creates a {@link ResourceRef} for a file on the default file system.
   *
   * @param path path to the file; must not be {@code null}
   * @return a {@link ResourceRef} for the given path
   */
  public static ResourceRef ofFile(final Path path) {
    return new FileResourceRef(path);
  }

  /**
   * Creates a {@link ResourceRef} for a single entry inside a zip or jar archive.
   *
   * @param zipPath path to the zip file on the file system; must not be {@code null}
   * @param entryPath path of the entry within the archive, e.g. {@code "data/foo.txt"} or {@code
   *     "/data/foo.txt"}; must not be {@code null}
   * @return a {@link ResourceRef} for the zip entry
   */
  public static ResourceRef ofZipEntry(final Path zipPath, final String entryPath) {
    return new ZipEntryResourceRef(zipPath, entryPath);
  }

  /**
   * Creates a {@link ResourceRef} backed by a {@link URL}.
   *
   * @param url the URL; must not be {@code null}
   * @return a {@link ResourceRef} for the given URL
   */
  public static ResourceRef ofUrl(final URL url) {
    return new UrlResourceRef(url);
  }

  private ResourceRefs() {}
}
