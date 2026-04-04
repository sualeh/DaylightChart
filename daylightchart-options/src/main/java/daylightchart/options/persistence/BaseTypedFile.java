/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Selected file.
 *
 * @param <T> File type
 */
public abstract class BaseTypedFile<T extends FileType> {

  private static final Logger LOGGER = Logger.getLogger(BaseTypedFile.class.getName());

  private final Path file;
  private final T fileType;

  protected BaseTypedFile() {
    this(null, null);
  }

  protected BaseTypedFile(final Path file, final T fileType) {
    this.file = file;
    this.fileType = fileType;
  }

  /**
   * Deletes the file if it exists.
   *
   * @return True if the file got deleted
   */
  public final boolean delete() {
    try {
      return Files.deleteIfExists(file);
    } catch (final IOException e) {
      LOGGER.log(Level.SEVERE, "Could not delete file, " + file, e);
      return false;
    }
  }

  /**
   * Checks whether the file exists, and is readable.
   *
   * @return Whether the file exists
   */
  public boolean exists() {
    if (!hasFile()) {
      return false;
    } else {
      return Files.isRegularFile(file) && Files.isReadable(file);
    }
  }

  /**
   * Directory of the selected file.
   *
   * @return Directory of the selected file.
   */
  public final Path getDirectory() {
    if (hasFile()) {
      return file.getParent();

    } else {
      return null;
    }
  }

  /**
   * The selected file.
   *
   * @return Selected file
   */
  public final Path getFile() {
    if (hasFile()) {
      return file;
    } else {
      throw new IllegalAccessError("No file");
    }
  }

  /**
   * The selected filename.
   *
   * @return Selected filename
   */
  public final String getFilename() {
    if (hasFile()) {
      return file.getFileName().toString();
    } else {
      throw new IllegalAccessError("No file");
    }
  }

  /**
   * Gets the file type.
   *
   * @return File type.
   */
  public final T getFileType() {
    return fileType;
  }

  /**
   * @return the isSelected
   */
  public final boolean hasFile() {
    return file != null;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (hasFile()) {
      return "%s%n%s".formatted(fileType, file);
    } else {
      return "No file [%d]".formatted(hashCode());
    }
  }
}
