/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.util;

import daylightchart.options.persistence.BaseTypedFile;
import daylightchart.options.persistence.FileType;
import java.nio.file.Path;

/**
 * Selected file.
 *
 * @param <T> File type
 */
public final class SelectedFile<T extends FileType> extends BaseTypedFile<T> {

  SelectedFile() {
    super();
  }

  SelectedFile(final Path file, final ExtensionFileFilter<T> fileFilter) {
    super(file, fileFilter.getFileType());
  }

  /**
   * Whether the file was selected.
   *
   * @return Whether the file was selected
   */
  public boolean isSelected() {
    return hasFile();
  }
}
