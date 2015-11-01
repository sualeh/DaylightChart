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
package org.geoname.parser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoname.data.Country;
import org.geoname.data.Location;

import us.fatehi.pointlocation6709.Angle;
import us.fatehi.pointlocation6709.Latitude;
import us.fatehi.pointlocation6709.Longitude;
import us.fatehi.pointlocation6709.PointLocation;

abstract class BaseDelimitedLocationsFileParser
  implements LocationsParser
{
  private static final Logger LOGGER = Logger
    .getLogger(BaseDelimitedLocationsFileParser.class.getName());

  private final BufferedReader reader;
  private final String delimiter;
  private final String[] header;

  protected BaseDelimitedLocationsFileParser(final InputStream stream,
                                             final String delimiter)
                                               throws ParserException
  {
    if (stream == null)
    {
      throw new ParserException("Cannot read locations");
    }
    reader = new BufferedReader(new UnicodeReader(stream, "UTF-8"));

    if (delimiter == null)
    {
      throw new ParserException("No delimiter provided");
    }
    this.delimiter = delimiter;

    String[] header = null;
    try
    {
      String line;
      if ((line = reader.readLine()) != null)
      {
        header = line.split(delimiter);
      }
      if (header == null || header.length == 0)
      {
        throw new ParserException("No header row provided");
      }
      this.header = header;
      LOGGER.log(Level.FINE, "Loaded header");
    }
    catch (final IOException e)
    {
      throw new ParserException("Could not load locations", e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.geoname.parser.LocationsParser#parseLocations()
   */
  public final Collection<Location> parseLocations()
    throws ParserException
  {
    final Set<Location> locations = new HashSet<Location>();
    try
    {
      final Map<String, String> locationDataMap = new HashMap<String, String>();
      String line;
      while ((line = reader.readLine()) != null)
      {
        final String[] fields = line.split(delimiter);
        locationDataMap.clear();
        for (int i = 0; i < header.length; i++)
        {
          final String data;
          if (fields.length > i)
          {
            data = fields[i];
          }
          else
          {
            data = null;
          }
          locationDataMap.put(header[i], data);
        }
        final Location location = parseLocation(locationDataMap);
        if (location != null)
        {
          locations.add(location);
        }
      }
    }
    catch (final IOException e)
    {
      throw new ParserException("Invalid locations", e);
    }
    finally
    {
      try
      {
        reader.close();
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Could not close locations reader");
      }
    }

    LOGGER.log(Level.INFO, "Loaded " + locations.size() + " locations");
    return locations;
  }

  private final double getDouble(final Map<String, String> locationDataMap,
                                 final String key)
                                   throws ParserException
  {
    if (locationDataMap == null || !locationDataMap.containsKey(key))
    {
      throw new ParserException("No value for key " + key);
    }

    try
    {
      return Double.parseDouble(locationDataMap.get(key));
    }
    catch (final NumberFormatException e)
    {
      throw new ParserException("Bad value for key " + key, e);
    }
  }

  protected final double getDouble(final Map<String, String> locationDataMap,
                                   final String key,
                                   final double defaultValue)
  {
    double doubleValue = defaultValue;
    try
    {
      doubleValue = getDouble(locationDataMap, key);
    }
    catch (final Exception e)
    {
      doubleValue = defaultValue;
    }
    return doubleValue;
  }

  protected final int getInteger(final Map<String, String> locationDataMap,
                                 final String key,
                                 final int defaultValue)
  {
    int integerValue = defaultValue;
    if (locationDataMap != null && locationDataMap.containsKey(key))
    {
      try
      {
        integerValue = Integer.parseInt(locationDataMap.get(key));
      }
      catch (final NumberFormatException e)
      {
        integerValue = defaultValue;
      }
    }
    return integerValue;
  }

  protected final Location getLocation(final Map<String, String> locationDataMap,
                                       final String city,
                                       final Country country,
                                       final String latitudeKey,
                                       final String longitudeKey,
                                       final String altitudeKey)
                                         throws ParserException
  {
    final Latitude latitude = new Latitude(Angle
      .fromDegrees(getDouble(locationDataMap, latitudeKey)));
    final Longitude longitude = new Longitude(Angle
      .fromDegrees(getDouble(locationDataMap, longitudeKey)));
    final double altitude = getDouble(locationDataMap, altitudeKey, 0D);

    final PointLocation pointLocation = new PointLocation(latitude,
                                                          longitude,
                                                          altitude,
                                                          "");

    final String timeZoneId = DefaultTimezones
      .attemptTimeZoneMatch(city, country, pointLocation.getLongitude());

    try
    {
      return new Location(city, country, timeZoneId, pointLocation);
    }
    catch (final IllegalArgumentException e)
    {
      throw new ParserException("Could not get location", e);
    }
  }

  protected abstract Location parseLocation(final Map<String, String> locationDataMap);

}
