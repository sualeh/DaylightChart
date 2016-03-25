/*
 *
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2016, Sualeh Fatehi.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package daylightchart.daylightchart.layout;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.geoname.data.Location;

import daylightchart.daylightchart.calculation.RiseSetUtility;
import daylightchart.daylightchart.calculation.RiseSetYearData;
import daylightchart.daylightchart.chart.DaylightChart;
import daylightchart.options.FileType;
import daylightchart.options.Options;

/**
 * The Daylight Chart report that can be written to a file.
 *
 * @author sfatehi
 */
public class DaylightChartReport
{

  private static final Logger LOGGER = Logger
    .getLogger(DaylightChartReport.class.getName());

  private final Location location;
  private final DaylightChart chart;

  /**
   * Constructor.
   *
   * @param location
   *        Location for the report.
   * @param options
   *        Report options.
   */
  public DaylightChartReport(final Location location, final Options options)
  {
    this.location = location;
    // Calculate rise and set timings for the whole year, and generate
    // chart
    final int year = Year.now().getValue();
    final RiseSetYearData riseSetData = RiseSetUtility
      .createRiseSetYear(location, year, options);
    chart = new DaylightChart(riseSetData, options);
    options.getChartOptions().updateChart(chart);
  }

  /**
   * Daylight Chart chart.
   *
   * @return Chart
   */
  public DaylightChart getChart()
  {
    return chart;
  }

  /**
   * The location for the chart.
   *
   * @return Location
   */
  public Location getLocation()
  {
    return location;
  }

  /**
   * Filename for the generated report.
   *
   * @param chartFileType
   *        Type of chart file.
   * @return Report file name
   */
  public String getReportFileName(final FileType chartFileType)
  {
    final String timeStamp = LocalDateTime.now()
      .format(DateTimeFormatter.ofPattern("yyyy_MMM_dd_hh_mm_ss"));
    String locationDescription = "";
    try
    {
      locationDescription = new String(location.getDescription()
        .getBytes("ASCII"), "ASCII");
      locationDescription = locationDescription.replaceAll("\\?", "~");
      locationDescription = locationDescription.replaceAll(", ", "_");
      locationDescription = locationDescription.replaceAll(" ", "_");
    }
    catch (final UnsupportedEncodingException e)
    {
      locationDescription = "";
    }
    return locationDescription + "_" + timeStamp
           + chartFileType.getFileExtension();
  }

  /**
   * Write the Daylight Chart report to a file.
   *
   * @param file
   *        File to write to.
   * @param chartFileType
   *        Type of chart file.
   */
  public void write(final Path file, final ChartFileType chartFileType)
  {
    if (chartFileType == null)
    {
      LOGGER
        .warning("Cannot write report file, since no chart file type was specified");
      return;
    }
    if (file == null)
    {
      LOGGER.warning("Cannot write report file, since no file was specified");
      return;
    }

    try
    {
      switch (chartFileType)
      {
        case png:
        case jpg:
          final BufferedImage image = chart
            .createBufferedImage(842, 595, BufferedImage.TYPE_INT_RGB, null);
          ImageIO.write(image, chartFileType.name(), file.toFile());
          break;
        default:
          throw new IllegalArgumentException("Unknown chart file type");
      }
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING,
                 "Error generating a report of type " + chartFileType,
                 e);
    }
  }

}
