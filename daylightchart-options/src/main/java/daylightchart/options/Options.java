/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.options;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.geoname.data.LocationsSortOrder;

/** All Daylight Chart options. */
public class Options implements Serializable {

  @Serial private static final long serialVersionUID = 3195704386171200909L;

  private LocationsSortOrder locationsSortOrder;
  private TimeZoneOption timeZoneOption;
  private ChartOrientation chartOrientation;
  private TwilightType twilightType;
  private boolean showChartLegend;
  private Path workingDirectory;

  /** Default options. */
  public Options() {
    locationsSortOrder = LocationsSortOrder.BY_NAME;
    timeZoneOption = TimeZoneOption.USE_TIME_ZONE;
    chartOrientation = ChartOrientation.STANDARD;
    twilightType = TwilightType.CIVIL;
    showChartLegend = true;
  }

  /**
   * Gets the chart orientation.
   *
   * @return Chart orientation
   */
  public ChartOrientation getChartOrientation() {
    return chartOrientation;
  }

  /**
   * @return the locationsSortOrder
   */
  public final LocationsSortOrder getLocationsSortOrder() {
    return locationsSortOrder;
  }

  /**
   * @return the timeZoneOption
   */
  public final TimeZoneOption getTimeZoneOption() {
    return timeZoneOption;
  }

  /**
   * Gets the twilight display setting for the chart.
   *
   * @return TwilightType setting
   */
  public TwilightType getTwilightType() {
    return twilightType;
  }

  /**
   * Get the working directory.
   *
   * @return Working directory.
   */
  public Path getWorkingDirectory() {
    if (workingDirectory != null && Files.isDirectory(workingDirectory)) {
      return workingDirectory;
    } else {
      return Path.of(".");
    }
  }

  /**
   * Whether to show the chart legend.
   *
   * @return Whether to show the chart legend
   */
  public boolean isShowChartLegend() {
    return showChartLegend;
  }

  /**
   * Sets the chart orientation.
   *
   * @param chartOrientation Chart orientation
   */
  public void setChartOrientation(final ChartOrientation chartOrientation) {
    if (chartOrientation != null) {
      this.chartOrientation = chartOrientation;
    }
  }

  /**
   * @param locationsSortOrder the locationsSortOrder to set
   */
  public final void setLocationsSortOrder(final LocationsSortOrder locationsSortOrder) {
    if (locationsSortOrder != null) {
      this.locationsSortOrder = locationsSortOrder;
    }
  }

  /**
   * Whether to show the chart legend.
   *
   * @param showChartLegend Whether to show the chart legend
   */
  public void setShowChartLegend(final boolean showChartLegend) {
    this.showChartLegend = showChartLegend;
  }

  /**
   * @param timeZoneOption the timeZoneOption to set
   */
  public final void setTimeZoneOption(final TimeZoneOption timeZoneOption) {
    if (timeZoneOption != null) {
      this.timeZoneOption = timeZoneOption;
    }
  }

  /**
   * Sets the twilight display setting for the chart.
   *
   * @param twilight TwilightType setting
   */
  public void setTwilightType(final TwilightType twilight) {
    if (twilight != null) {
      twilightType = twilight;
    }
  }

  /**
   * Working directory.
   *
   * @param workingDirectory Working directory.
   */
  public void setWorkingDirectory(final Path workingDirectory) {
    if (workingDirectory != null && Files.isDirectory(workingDirectory)) {
      this.workingDirectory = workingDirectory;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
