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
package daylightchart.options;


import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import daylightchart.daylightchart.calculation.TwilightType;
import daylightchart.daylightchart.chart.ChartOrientation;
import daylightchart.daylightchart.chart.TimeZoneOption;
import daylightchart.daylightchart.layout.DaylightChartReport;
import daylightchart.location.LocationsSortOrder;
import daylightchart.options.chart.ChartOptions;

/**
 * All Daylight Chart options.
 * 
 * @author Sualeh Fatehi
 */
public class Options
  implements Serializable
{

  private static final long serialVersionUID = 3195704386171200909L;

  private LocationsSortOrder locationsSortOrder;
  private TimeZoneOption timeZoneOption;
  private ChartOrientation chartOrientation;
  private TwilightType twilight;
  private boolean showChartLegend;
  //
  private ChartOptions chartOptions;
  //
  private File workingDirectory;
  //
  private JasperReport jasperReport;
  // 
  private boolean slimUi;

  /**
   * Default options.
   */
  public Options()
  {
    locationsSortOrder = LocationsSortOrder.BY_NAME;
    timeZoneOption = TimeZoneOption.USE_TIME_ZONE;
    chartOptions = new ChartOptions();
    chartOrientation = ChartOrientation.STANDARD;
    twilight = TwilightType.CIVIL;
    showChartLegend = true;

    loadDefaultJasperReport();
  }

  public final ChartOptions getChartOptions()
  {
    return chartOptions;
  }

  /**
   * Gets the chart orientation.
   * 
   * @return Chart orientation
   */
  public ChartOrientation getChartOrientation()
  {
    return chartOrientation;
  }

  public JasperReport getJasperReport()
  {
    return jasperReport;
  }

  /**
   * @return the locationsSortOrder
   */
  public final LocationsSortOrder getLocationsSortOrder()
  {
    return locationsSortOrder;
  }

  /**
   * @return the timeZoneOption
   */
  public final TimeZoneOption getTimeZoneOption()
  {
    return timeZoneOption;
  }

  /**
   * Gets the twilight display setting for the chart.
   * 
   * @return TwilightType setting
   */
  public TwilightType getTwilightType()
  {
    return twilight;
  }

  public File getWorkingDirectory()
  {
    if (isDirectoryValid(workingDirectory))
    {
      return workingDirectory;
    }
    else
    {
      return new File(".");
    }
  }

  /**
   * Whether to show the chart legend.
   * 
   * @return Whether to show the chart legend
   */
  public boolean isShowChartLegend()
  {
    return showChartLegend;
  }

  /**
   * @return the slimUi
   */
  public boolean isSlimUi()
  {
    return slimUi;
  }

  /**
   * @param chartOptions
   *        the chartOptions to set
   */
  public final void setChartOptions(final ChartOptions chartOptions)
  {
    if (chartOptions != null)
    {
      this.chartOptions = chartOptions;
    }
  }

  /**
   * Sets the chart orientation.
   * 
   * @param chartOrientation
   *        Chart orientation
   */
  public void setChartOrientation(final ChartOrientation chartOrientation)
  {
    if (chartOrientation != null)
    {
      this.chartOrientation = chartOrientation;
    }
  }

  /**
   * @param locationsSortOrder
   *        the locationsSortOrder to set
   */
  public final void setLocationsSortOrder(final LocationsSortOrder locationsSortOrder)
  {
    if (locationsSortOrder != null)
    {
      this.locationsSortOrder = locationsSortOrder;
    }
  }

  /**
   * Whether to show the chart legend.
   * 
   * @param showChartLegend
   *        Whether to show the chart legend
   */
  public void setShowChartLegend(final boolean showChartLegend)
  {
    this.showChartLegend = showChartLegend;
  }

  /**
   * @param timeZoneOption
   *        the timeZoneOption to set
   */
  public final void setTimeZoneOption(final TimeZoneOption timeZoneOption)
  {
    if (timeZoneOption != null)
    {
      this.timeZoneOption = timeZoneOption;
    }
  }

  /**
   * Sets the twilight display setting for the chart.
   * 
   * @param twilight
   *        TwilightType setting
   */
  public void setTwilightType(final TwilightType twilight)
  {
    if (twilight != null)
    {
      this.twilight = twilight;
    }
  }

  @Override
  public String toString()
  {
    return ReflectionToStringBuilder.toString(this,
                                              ToStringStyle.MULTI_LINE_STYLE);
  }

  /**
   * @param slimUi
   *        the slimUi to set
   */
  void setSlimUi(final boolean slimUi)
  {
    this.slimUi = slimUi;
  }

  void setWorkingDirectory(final File workingDirectory)
  {
    if (isDirectoryValid(workingDirectory))
    {
      this.workingDirectory = workingDirectory;
    }
  }

  private boolean isDirectoryValid(final File directory)
  {
    return directory != null && directory.exists() && directory.isDirectory()
           && directory.canWrite();
  }

  private void loadDefaultJasperReport()
  {
    try
    {
      // Generate JasperReport for the chart
      // 1. Load report
      final InputStream reportStream = DaylightChartReport.class
        .getResourceAsStream("/DaylightChartReport.jrxml");
      final JasperDesign jasperDesign = JRXmlLoader.load(reportStream);
      // 2. Compile report
      jasperReport = JasperCompileManager.compileReport(jasperDesign);
    }
    catch (final JRException e)
    {
      throw new IllegalArgumentException("Cannot compile JRXML file");
    }
  }
}
