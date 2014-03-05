/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2014, Sualeh Fatehi.
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
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;

import org.geoname.data.Location;
import org.joda.time.LocalDateTime;

import daylightchart.Version;
import daylightchart.daylightchart.calculation.RiseSetUtility;
import daylightchart.daylightchart.calculation.RiseSetYearData;
import daylightchart.daylightchart.chart.DaylightChart;
import daylightchart.options.FileType;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;

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
  private final JasperPrint jasperPrint;
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
    final RiseSetYearData riseSetData = RiseSetUtility
      .createRiseSetYear(location,
                         Calendar.getInstance().get(Calendar.YEAR),
                         options);
    chart = new DaylightChart(riseSetData, options);
    options.getChartOptions().updateChart(chart);
    //
    jasperPrint = renderDaylightChartReport(riseSetData);
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
    final String timeStamp = new LocalDateTime()
      .toString("yyyy_MMM_dd_hh_mm_ss");
    String locationDescription = "";
    try
    {
      locationDescription = new String(location.getDescription()
        .getBytes("ASCII"), "ASCII");
      locationDescription = locationDescription.replaceAll("\\?", "~");
      locationDescription = locationDescription.replaceAll(", ", "_");
      locationDescription = locationDescription.replaceAll(" ", "_");
    }
    catch (UnsupportedEncodingException e)
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
  public void write(final File file, final ChartFileType chartFileType)
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
      JRExporter exporter;
      switch (chartFileType)
      {
        case pdf:
          exporter = new JRPdfExporter();

          exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
          exporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);

          exporter.setParameter(JRPdfExporterParameter.PDF_VERSION,
                                JRPdfExporterParameter.PDF_VERSION_1_6);
          exporter.setParameter(JRPdfExporterParameter.METADATA_CREATOR,
                                Version.about());
          exporter.setParameter(JRPdfExporterParameter.METADATA_TITLE, location
            .getDescription());
          exporter.setParameter(JRPdfExporterParameter.METADATA_SUBJECT,
                                Version.getProductName());
          exporter.setParameter(JRPdfExporterParameter.METADATA_AUTHOR, System
            .getProperty("user.name"));

          exporter.exportReport();
          break;
        case html:
          exporter = new JRHtmlExporter();

          exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
          exporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);
          exporter
            .setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN,
                          Boolean.FALSE);

          exporter.exportReport();
          break;
        case png:
        case jpg:
          final BufferedImage image = chart
            .createBufferedImage(842, 595, BufferedImage.TYPE_INT_RGB, null);
          ImageIO.write(image, chartFileType.name(), file);
          break;
        default:
          throw new IllegalArgumentException("Unknown chart file type");
      }
    }
    catch (final JRException e)
    {
      LOGGER.log(Level.WARNING, "Error generating a report of type "
                                + chartFileType, e);
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Error generating a report of type "
                                + chartFileType, e);
    }
  }

  private JasperPrint renderDaylightChartReport(final RiseSetYearData riseSetData)
  {
    try
    {

      // Generate JasperReport for the chart
      // 1. Load compiled report
      final JasperReport jasperReport = UserPreferences.reportFile().getData();
      // 2. Prepare parameters
      final Map<String, Object> parameters = new HashMap<String, Object>();
      parameters.put("location", location);
      parameters.put("daylight_chart", new JCommonDrawableRenderer(chart));
      // 3. Create data set
      final JRDataSource dataSource = new JRBeanCollectionDataSource(riseSetData
        .getRiseSetData());

      // 4. Fill the report
      return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }
    catch (final JRException e)
    {
      LOGGER.log(Level.SEVERE, "Error generating a report for location "
                               + location, e);
      return null;
    }
  }

}
