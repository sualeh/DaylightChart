/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options.persistence;

/** Options file. */
class OptionsFileType implements FileType {

  /**
   * {@inheritDoc}
   *
   * @see daylightchart.options.persistence.FileType#getDescription()
   */
  @Override
  public String getDescription() {
    return "Options file";
  }

  /**
   * {@inheritDoc}
   *
   * @see daylightchart.options.persistence.FileType#getFileExtension()
   */
  @Override
  public String getFileExtension() {
    return ".yaml";
  }
}
