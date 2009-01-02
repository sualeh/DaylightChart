package daylightchart.options;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

/**
 * Represents a report file, with data.
 * 
 * @author sfatehi
 */
public class ReportDataFile
  extends BaseDataFile<ReportFileType, JasperReport>
{

  private static final Logger LOGGER = Logger.getLogger(ReportDataFile.class
    .getName());

  /**
   * Constructor.
   * 
   * @param reportDataFile
   *        File
   */
  public ReportDataFile(final BaseTypedFile<ReportFileType> reportDataFile)
  {
    this(reportDataFile.getFile());
  }

  /**
   * Constructor.
   * 
   * @param file
   *        File
   */
  public ReportDataFile(final File file)
  {
    super(file, new ReportFileType());
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

  /**
   * Saves report to a file.
   */
  public void save()
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

  /**
   * Loads a report from a file, falling back to an internal resource
   * with the same name.
   */
  public void loadWithFallback()
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

  public void load(final InputStream... input)
  {
    if (input == null || input.length == 0)
    {
      return;
    }

    try
    {
      final JasperDesign jasperDesign = JRXmlLoader.load(input[0]);
      data = JasperCompileManager.compileReport(jasperDesign);
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

}
