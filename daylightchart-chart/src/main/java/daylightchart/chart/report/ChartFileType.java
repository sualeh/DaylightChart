/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.report;

import daylightchart.options.persistence.FileType;

/** Chart file type, when exported. */
public enum ChartFileType implements FileType {
  /** Portable Network Graphics */
  png("Portable Network Graphics", ".png"),
  /** JPEG */
  jpg("JPEG", ".jpg");

  private final String description;
  private final String fileExtension;

  ChartFileType(final String description, final String fileExtension) {
    this.description = description;
    this.fileExtension = fileExtension;
  }

  /**
   * {@inheritDoc}
   *
   * @see daylightchart.options.persistence.FileType#getDescription()
   */
  @Override
  public String getDescription() {
    return description;
  }

  /**
   * {@inheritDoc}
   *
   * @see daylightchart.options.persistence.FileType#getFileExtension()
   */
  @Override
  public String getFileExtension() {
    return fileExtension;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return description + " (*" + fileExtension + ")";
  }
}
