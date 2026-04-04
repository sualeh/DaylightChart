/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.options;

import daylightchart.options.persistence.FileType;

/** Chart options file. */
class ChartOptionsFileType implements FileType {

  @Override
  public String getDescription() {
    return "Chart options file";
  }

  @Override
  public String getFileExtension() {
    return ".yaml";
  }
}
