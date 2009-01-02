package daylightchart.options;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoname.parser.UnicodeReader;

import com.thoughtworks.xstream.XStream;

import daylightchart.daylightchart.chart.DaylightChart;
import daylightchart.options.chart.ChartOptions;

/**
 * Represents a location file, with data.
 * 
 * @author sfatehi
 */
public final class OptionsDataFile
  extends BaseDataFile<OptionsFileType, Options>
{

  private static final Logger LOGGER = Logger.getLogger(OptionsDataFile.class
    .getName());

  /**
   * Constructor.
   */
  public OptionsDataFile()
  {
    this(new File("."));

    createDefaultOptions();
  }

  private void createDefaultOptions()
  {
    final ChartOptions chartOptions = new ChartOptions();
    chartOptions.copyFromChart(new DaylightChart());

    data = new Options();
    data.setChartOptions(chartOptions);
  }

  /**
   * Constructor.
   * 
   * @param settingsDirectory
   *        Settings directory
   */
  public OptionsDataFile(final File settingsDirectory)
  {
    super(new File(settingsDirectory, "options.xml"), new OptionsFileType());
    // Validation
    if (!getFile().isDirectory() || !getFile().exists())
    {
      throw new IllegalArgumentException("Settings directory is not a directory");
    }
  }

  /**
   * Loads options from a file.
   */
  public void load()
  {
    if (!exists())
    {
      LOGGER.log(Level.WARNING, "No options file provided");
      return;
    }
    FileInputStream input;
    final File file = getFile();
    try
    {
      input = new FileInputStream(file);
    }
    catch (final FileNotFoundException e)
    {
      LOGGER.log(Level.WARNING, "Could not open options file, " + file, e);
      return;
    }

    load(input);
  }

  public void load(final InputStream... input)
  {
    if (input == null || input.length == 0)
    {
      return;
    }

    Reader reader = null;
    try
    {
      reader = new UnicodeReader(input[0], "UTF-8");
      final XStream xStream = new XStream();
      data = (Options) xStream.fromXML(reader);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not read options", e);
      data = null;
    }
    finally
    {
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, "Could not close stream", e);
        }
      }
    }
  }

  public void loadWithFallback()
  {
    // 1. Load from file
    load();
    // 2. Create default options
    if (data == null)
    {
      createDefaultOptions();
    }
  }

  /**
   * Saves options to a file.
   * 
   * @param file
   *        File to write
   * @param options
   *        Options
   */
  public void save()
  {
    try
    {
      delete();
      final Writer writer = getFileWriter(getFile());
      if (writer == null)
      {
        return;
      }

      final XStream xStream = new XStream();
      xStream.toXML(data, writer);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could save options to " + getFile(), e);
    }
  }

}
