/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2010, Sualeh Fatehi.
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.geoname.data.Location;
import org.geoname.parser.GNISFileParser;
import org.geoname.parser.GNSCountryFileParser;
import org.geoname.parser.LocationFormatter;
import org.geoname.parser.LocationParser;
import org.geoname.parser.UnicodeReader;

import daylightchart.gui.actions.LocationFileType;

/**
 * Represents a location file, with data.
 * 
 * @author sfatehi
 */
abstract class BaseLocationsDataFile
  extends BaseDataFile<LocationFileType, List<Location>>
{

  private static final Logger LOGGER = Logger
    .getLogger(BaseLocationsDataFile.class.getName());

  /**
   * Constructor.
   * 
   * @param file
   *        File
   * @param fileType
   *        Location file type
   */
  protected BaseLocationsDataFile(final File file,
                                  final LocationFileType fileType)
  {
    super(file, fileType);
  }

  /**
   * Constructor.
   * 
   * @param settingsDirectory
   *        Settings directory
   * @param resource
   *        Resource
   * @param fileType
   *        File type
   */
  protected BaseLocationsDataFile(File settingsDirectory,
                                  String resource,
                                  LocationFileType fileType)
  {
    super(settingsDirectory, resource, fileType);
  }

  /**
   * Loads a list of locations from a file of a given format.
   */
  public final void load()
  {
    if (!exists())
    {
      LOGGER.log(Level.WARNING, "No locations file provided");
      data = null;
      return;
    }

    final List<InputStream> inputs = new ArrayList<InputStream>();
    final File file = getFile();
    try
    {
      switch (getFileType())
      {
        case data:
        case gns_country_file:
        case gnis_state_file:
          inputs.add(new FileInputStream(file));
          break;
        case gns_country_file_zipped:
        case gnis_state_file_zipped:
          final ZipFile zipFile = new ZipFile(file);
          final List<ZipEntry> zippedFiles = (List<ZipEntry>) Collections
            .list(zipFile.entries());
          for (final ZipEntry zipEntry: zippedFiles)
          {
            inputs.add(zipFile.getInputStream(zipEntry));
          }
          break;
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not read locations from " + file, e);
      data = null;
    }

    load(inputs.toArray(new InputStream[inputs.size()]));
  }

  protected final void load(final InputStream... inputs)
  {
    data = new ArrayList<Location>();
    try
    {
      for (final InputStream inputStream: inputs)
      {
        Reader reader = null;
        switch (getFileType())
        {
          case data:
            reader = new InputStreamReader(inputStream, "UTF-8");
            data.addAll(LocationParser.parseLocations(reader));
            break;
          case gns_country_file:
          case gns_country_file_zipped:
            reader = new UnicodeReader(inputStream, "UTF-8");
            final GNSCountryFileParser countryFileParser = new GNSCountryFileParser(reader);
            data.addAll(countryFileParser.parseLocations());
            break;
          case gnis_state_file:
          case gnis_state_file_zipped:
            reader = new UnicodeReader(inputStream, "UTF-8");
            final GNISFileParser gnisFileParser = new GNISFileParser(reader);
            data.addAll(gnisFileParser.parseLocations());
            break;
        }
        reader.close();
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not read locations", e);
      data = null;
    }
    finally
    {
      for (final InputStream inputStream: inputs)
      {
        if (inputStream != null)
        {
          try
          {
            inputStream.close();
          }
          catch (final IOException e)
          {
            LOGGER.log(Level.WARNING, "Could not close input stream", e);
          }
        }
      }
    }

    if (data.size() == 0)
    {
      data = null;
    }
  }

  /**
   * Saves locations to a file.
   */
  protected final void save()
  {
    final File file = getFile();
    if (file == null)
    {
      LOGGER.log(Level.WARNING, "No locations file provided");
      return;
    }

    try
    {
      file.delete();

      final Writer writer = getFileWriter(file);
      if (writer == null)
      {
        return;
      }

      LocationFormatter.formatLocations(data, writer);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not save locations to " + file, e);
    }
  }

}
