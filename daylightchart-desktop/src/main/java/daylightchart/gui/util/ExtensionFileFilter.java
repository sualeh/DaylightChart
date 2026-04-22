/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.util;

import daylightchart.options.persistence.FileType;
import java.io.File;
import java.util.Locale;

/**
 * Filters files by extension.
 *
 * @param <T> A file type enumeration
 */
public class ExtensionFileFilter<T extends FileType> extends javax.swing.filechooser.FileFilter
    implements java.io.FileFilter, FileType {

  /**
   * Gets the extension for the given file.
   *
   * @param file File
   * @return Extension
   */
  public static String getExtension(final File file) {
    String extension = "";
    final String fileName = file.getName();
    final int i = fileName.lastIndexOf('.');
    if (i > 0 && i < fileName.length() - 1) {
      extension = fileName.substring(i + 1).toLowerCase(Locale.ENGLISH);
    }
    return "." + extension;
  }

  /** A description for the file type. */
  private final T fileType;

  /**
   * Constructor.
   *
   * @param fileType A description of the file type.
   */
  public ExtensionFileFilter(final T fileType) {
    this.fileType = fileType;
  }

  /** {@inheritDoc} */
  @Override
  public boolean accept(final File file) {
    boolean accept = false;
    if (file.isDirectory()) {
      accept = true;
    } else if (file.isHidden()) {
      accept = false;
    } else {
      final String extension = getExtension(file);
      accept = extension != null && extension.equals(fileType.getFileExtension());
    }
    return accept;
  }

  /**
   * {@inheritDoc}
   *
   * @see javax.swing.filechooser.FileFilter#getDescription()
   */
  @Override
  public String getDescription() {
    return fileType.getDescription();
  }

  /**
   * {@inheritDoc}
   *
   * @see daylightchart.options.persistence.FileType#getFileExtension()
   */
  @Override
  public String getFileExtension() {
    return fileType.getFileExtension();
  }

  /**
   * Gets the file type.
   *
   * @return File type.
   */
  public T getFileType() {
    return fileType;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return fileType.getDescription();
  }
}
