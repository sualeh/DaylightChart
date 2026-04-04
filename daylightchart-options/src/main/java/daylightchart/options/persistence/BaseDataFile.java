/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoname.parser.resources.ResourceRef;

/**
 * Represents a data file, with data.
 *
 * @param <T> File type
 * @param <D> Data object type
 */
public abstract class BaseDataFile<T extends FileType, D> extends BaseTypedFile<T> {

  private static final Logger LOGGER = Logger.getLogger(BaseDataFile.class.getName());

  protected D data;

  /**
   * Constructor.
   *
   * @param typedFile File
   */
  public BaseDataFile(final BaseTypedFile<T> typedFile) {
    this(typedFile.getFile(), typedFile.getFileType());
  }

  /**
   * Constructor.
   *
   * @param file File
   * @param fileType File type
   */
  public BaseDataFile(final Path file, final T fileType) {
    super(file, fileType);

    if (file == null) {
      throw new IllegalArgumentException("No file provided");
    }
    if (fileType == null) {
      throw new IllegalArgumentException("No file type provided");
    }
  }

  /**
   * Constructor.
   *
   * @param settingsDirectory Settings directory
   * @param resource Resource
   * @param fileType File type
   */
  protected BaseDataFile(final Path settingsDirectory, final String resource, final T fileType) {
    this(Path.of(settingsDirectory.toString(), resource), fileType);
    if (settingsDirectory == null || !Files.isDirectory(settingsDirectory)) {
      throw new IllegalArgumentException("Settings directory is not a directory");
    }
    loadWithFallback();
  }

  /**
   * Gets data.
   *
   * @return Data
   */
  public final D getData() {
    return data;
  }

  /** Reloads data from the configured file without applying fallback behavior. */
  public final void loadData() {
    load();
  }

  /**
   * Sets data, and saves it.
   *
   * @param data Data
   */
  public final void save(final D data) {
    if (data != null) {
      this.data = data;
      save();
    }
  }

  protected final Writer getFileWriter(final Path file) {
    try {
      final OutputStream fileOutputStream =
          Files.newOutputStream(
              file,
              StandardOpenOption.CREATE,
              StandardOpenOption.WRITE,
              StandardOpenOption.TRUNCATE_EXISTING);
      final BufferedWriter writer =
          new BufferedWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
      return writer;
    } catch (final UnsupportedEncodingException e) {
      LOGGER.log(Level.WARNING, "Cannot write file " + file, e);
      return null;
    } catch (final IOException e) {
      LOGGER.log(Level.WARNING, "Cannot write file " + file, e);
      return null;
    }
  }

  /** Loads data from the file. */
  protected abstract void load();

  protected abstract void load(final ResourceRef... refs);

  /**
   * Loads data from the file, falling back to a default that is usually loaded from an internal
   * resource.
   */
  protected abstract void loadWithFallback();

  /** Saves data to a file. */
  protected abstract void save();
}
