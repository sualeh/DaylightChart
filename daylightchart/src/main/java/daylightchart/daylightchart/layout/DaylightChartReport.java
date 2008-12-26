/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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


import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.renderers.JFreeChartRenderer;

import org.geoname.Location;
import org.jfree.chart.ChartUtilities;
import org.joda.time.LocalDateTime;

import sf.util.ui.FileType;
import daylightchart.daylightchart.calculation.RiseSetUtility;
import daylightchart.daylightchart.calculation.RiseSetYearData;
import daylightchart.daylightchart.chart.DaylightChart;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;

public class DaylightChartReport
{

  private static final Logger LOGGER = Logger
    .getLogger(DaylightChartReport.class.getName());

  private final Location location;
  private final JasperPrint jasperPrint;
  private final DaylightChart chart;

  public DaylightChartReport(final Location location, final Options options)
  {
    this.location = location;
    //
    // Calculate rise and set timings for the whole year, and generate
    // chart
    final RiseSetYearData riseSetData = RiseSetUtility
      .createRiseSetYear(location,
                         Calendar.getInstance().get(Calendar.YEAR),
                         options);
    chart = new DaylightChart(riseSetData, options);
    options.getChartOptions().updateChart(chart);
    //
    jasperPrint = renderDaylightChartReport(riseSetData, options);
  }

  /**
   * @return the chart
   */
  public DaylightChart getChart()
  {
    return chart;
  }

  /**
   * @return the location
   */
  public Location getLocation()
  {
    return location;
  }

  public String getReportFileName(final FileType chartFileType)
  {
    return location.getDescription() + "."
           + new LocalDateTime().toString("yyyyMMddhhmm")
           + chartFileType.getFileExtension();
  }

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
      final String filePath = file.getAbsolutePath();
      switch (chartFileType)
      {
        case pdf:
          JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
          break;
        case png:
          ChartUtilities.saveChartAsPNG(file, chart, 842, 595);
          break;
        case jpg:
          ChartUtilities.saveChartAsJPEG(file, chart, 842, 595);
          break;
        case html:
          JasperExportManager.exportReportToHtmlFile(jasperPrint, filePath);
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

  private JasperPrint renderDaylightChartReport(final RiseSetYearData riseSetData,
                                                final Options options)
  {
    try
    {

      // Generate JasperReport for the chart
      // 1. Load compiled report
      final JasperReport jasperReport = UserPreferences.getReport();
      // 2. Prepare parameters
      final Map<String, Object> parameters = new HashMap<String, Object>();
      parameters.put("location", location);
      parameters.put("daylight_chart", new JFreeChartRenderer(chart));
      // 3. Create data set
      final JRDataSource dataSource = new JRBeanCollectionDataSource(riseSetData
        .getRiseSetData());

      // Render the report into PDF
      return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }
    catch (final JRException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }

}
