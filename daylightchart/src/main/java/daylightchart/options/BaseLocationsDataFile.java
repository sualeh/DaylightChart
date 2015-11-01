/*
 *
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2015, Sualeh Fatehi.
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


import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.geoname.data.Location;
import org.geoname.parser.GNISFileParser;
import org.geoname.parser.GNSCountryFileParser;
import org.geoname.parser.LocationFormatter;
import org.geoname.parser.LocationsListParser;
import org.geoname.parser.LocationsParser;

import daylightchart.gui.actions.LocationFileType;

/**
 * Represents a location file, with data.
 *
 * @author sfatehi
 */
abstract class BaseLocationsDataFile
  extends BaseDataFile<LocationFileType, Collection<Location>>
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
  protected BaseLocationsDataFile(final Path file,
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
  protected BaseLocationsDataFile(final Path settingsDirectory,
                                  final String resource,
                                  final LocationFileType fileType)
  {
    super(settingsDirectory, resource, fileType);
  }

  /**
   * Loads a list of locations from a file of a given format.
   */
  @Override
  public final void load()
  {
    if (!exists())
    {
      LOGGER.log(Level.WARNING, "No locations file provided");
      data = null;
      return;
    }

    final List<InputStream> inputs = new ArrayList<InputStream>();
    final Path file = getFile();
    try
    {
      switch (getFileType())
      {
        case data:
        case gns_country_file:
        case gnis_state_file:
          inputs.add(Files.newInputStream(file));
          break;
        case gns_country_file_zipped:
        case gnis_state_file_zipped:
          final ZipFile zipFile = new ZipFile(file.toFile());
          for (final ZipEntry zipEntry: Collections.list(zipFile.entries()))
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

  @Override
  protected final void load(final InputStream... inputs)
  {
    data = new HashSet<Location>();
    try
    {
      for (final InputStream inputStream: inputs)
      {
        final LocationsParser locationsFileParser;
        switch (getFileType())
        {
          case data:
            locationsFileParser = new LocationsListParser(inputStream);
            break;
          case gns_country_file:
          case gns_country_file_zipped:
            locationsFileParser = new GNSCountryFileParser(inputStream);
            break;
          case gnis_state_file:
          case gnis_state_file_zipped:
            locationsFileParser = new GNISFileParser(inputStream);
            break;
          default:
            locationsFileParser = null;
            break;
        }
        if (locationsFileParser != null)
        {
          data.addAll(locationsFileParser.parseLocations());
        }
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

    if (data.isEmpty())
    {
      data = null;
    }
  }

  /**
   * Saves locations to a file.
   */
  @Override
  protected final void save()
  {
    final Path file = getFile();
    if (file == null)
    {
      LOGGER.log(Level.WARNING, "No locations file provided");
      return;
    }

    try
    {
      Files.deleteIfExists(file);

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
