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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoname.Location;
import org.geoname.parser.GNISFilesParser;
import org.geoname.parser.GNSCountryFilesParser;
import org.geoname.parser.LocationParser;
import org.geoname.parser.ParserException;


/**
 * Loads locations from a file of any supported format.
 * 
 * @author Sualeh Fatehi
 */
public final class LocationsLoader
{

  private static final Logger LOGGER = Logger.getLogger(LocationsLoader.class
    .getName());

  public static final Reader getFileReader(final File file)
  {
    if (file == null || !file.exists() || !file.canRead())
    {
      LOGGER.log(Level.WARNING, "Cannot read file " + file);
      return null;
    }
    try
    {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),
                                                                             "UTF-8"));
      return reader;
    }
    catch (final UnsupportedEncodingException e)
    {
      LOGGER.log(Level.WARNING, "Cannot read file " + file);
      return null;
    }
    catch (final FileNotFoundException e)
    {
      LOGGER.log(Level.WARNING, "Cannot read file " + file);
      return null;
    }
  }

  /**
   * Attempts to load a locations file, trying each format in turn.
   * 
   * @param file
   *        File to load
   * @return List of locations, or null on error
   */
  public static List<Location> load(final File file)
  {
    if (file == null || !file.exists() || !file.canRead())
    {
      LOGGER.log(Level.WARNING, "Cannot read file " + file);
      return null;
    }

    Reader reader;
    List<Location> locations = null;

    // 1. Attempt to load as a Daylight Chart locations file
    reader = getFileReader(file);
    if (locations == null)
    {
      try
      {
        locations = LocationParser.parseLocations(reader);
      }
      catch (final ParserException e)
      {
        locations = null;
      }
      finally
      {
        try
        {
          reader.close();
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, "Could not close file " + file);
        }
      }
    }
    if (locations != null && locations.size() == 0)
    {
      locations = null;
    }

    // 2. Attempt to load as a GNS country file
    reader = getFileReader(file);
    if (locations == null)
    {
      try
      {
        locations = GNSCountryFilesParser.parseLocations(reader);
      }
      catch (final ParserException e)
      {
        locations = null;
      }
      finally
      {
        try
        {
          reader.close();
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, "Could not close file " + file);
        }
      }
    }
    if (locations != null && locations.size() == 0)
    {
      locations = null;
    }

    // 3. Attempt to load as a GNIS file
    reader = getFileReader(file);
    if (locations == null)
    {
      try
      {
        locations = GNISFilesParser.parseLocations(reader);
      }
      catch (final ParserException e)
      {
        locations = null;
      }
      finally
      {
        try
        {
          reader.close();
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, "Could not close file " + file);
        }
      }
    }
    if (locations != null && locations.size() == 0)
    {
      locations = null;
    }

    return locations;
  }

  private LocationsLoader()
  {
  }

}
