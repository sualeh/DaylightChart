/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2009, Sualeh Fatehi.
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import daylightchart.gui.actions.ReportDesignFileType;

/**
 * Represents a report file, with data.
 * 
 * @author sfatehi
 */
public class ReportDataFile
  extends BaseDataFile<ReportDesignFileType, JasperReport>
{

  private static final Logger LOGGER = Logger.getLogger(ReportDataFile.class
    .getName());

  /**
   * Constructor.
   * 
   * @param reportDataFile
   *        File
   */
  public ReportDataFile(final BaseTypedFile<ReportDesignFileType> reportDataFile)
  {
    this(reportDataFile.getFile(), reportDataFile.getFileType());
  }

  /**
   * Constructor.
   * 
   * @param settingsDirectory
   *        Settings directory
   */
  public ReportDataFile(final File settingsDirectory)
  {
    super(settingsDirectory,
          "DaylightChartReport.jasper",
          ReportDesignFileType.report_design);
  }

  /**
   * Constructor.
   * 
   * @param file
   *        File
   * @param fileType
   *        File type
   */
  public ReportDataFile(final File file, ReportDesignFileType fileType)
  {
    super(file, fileType);
  }

  /**
   * Loads a report from a file.
   */
  public void load()
  {
    if (!exists())
    {
      LOGGER.log(Level.WARNING, "No report file provided");
      return;
    }

    InputStream input;
    final File file = getFile();
    try
    {
      input = new FileInputStream(file);
    }
    catch (final FileNotFoundException e)
    {
      LOGGER.log(Level.WARNING, "Could not read report from " + file, e);
      return;
    }

    load(input);
  }

  protected void load(final InputStream... input)
  {
    if (input == null || input.length == 0)
    {
      return;
    }

    try
    {
      data = (JasperReport) JRLoader.loadObject(input[0]);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Cannot load report", e);
      return;
    }
    finally
    {
      if (input != null)
      {
        try
        {
          input[0].close();
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, "Cannot close input stream", e);
        }
      }
    }
  }

  /**
   * Loads a report from a file, falling back to an internal resource
   * with the same name.
   */
  protected void loadWithFallback()
  {
    // 1. Load from file
    load();
    // 2. Load from internal store
    if (data == null)
    {
      final InputStream input = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(getFilename());
      load(input);
    }
    // 3. If no report is loaded, fail
    if (data == null)
    {
      throw new RuntimeException("Cannot load report");
    }
  }

  /**
   * Saves report to a file.
   */
  protected void save()
  {
    final File file = getFile();
    try
    {
      delete();
      JRXmlWriter.writeReport(data, file.getAbsolutePath(), "UTF-8");
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not save report to " + file, e);
    }
  }

}
