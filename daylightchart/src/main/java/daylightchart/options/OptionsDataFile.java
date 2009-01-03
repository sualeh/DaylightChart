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
    if (!settingsDirectory.isDirectory() || !settingsDirectory.exists())
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

  private void createDefaultOptions()
  {
    final ChartOptions chartOptions = new ChartOptions();
    chartOptions.copyFromChart(new DaylightChart());

    data = new Options();
    data.setChartOptions(chartOptions);
  }

}
