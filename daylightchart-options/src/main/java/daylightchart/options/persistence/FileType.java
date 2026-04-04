/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options.persistence;

/** Defines a type of file, with a given file extension. */
public interface FileType {

  /**
   * The description of the file type.
   *
   * @return the description
   */
  String getDescription();

  /**
   * Gets the file extension.
   *
   * @return File extension
   */
  String getFileExtension();
}
