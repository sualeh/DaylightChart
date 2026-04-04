/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options.persistence;

/** Types of locations specifications files. */
public enum LocationFileType implements FileType {

  /** Daylight Chart data file */
  data("Daylight Chart data file", ".data"),
  /** GNS Country File */
  gns_country_file("GNS Country File", ".txt"),
  /** GNS Country File, zipped */
  gns_country_file_zipped("GNS Country File, zipped", ".zip"),
  /** GNIS states file */
  gnis_state_file("GNIS states file", ".txt"),
  /** GNIS states file, zipped */
  gnis_state_file_zipped("GNIS states file, zipped", ".zip"),
  ;

  private final String description;
  private final String fileExtension;

  private LocationFileType(final String description, final String fileExtension) {
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
