/* 
 * 
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
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


import java.io.InputStream;
import java.util.Map;

import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.Location;

/**
 * Parses data files.
 * 
 * @author Sualeh Fatehi
 */
public final class GNISFileParser
  extends BaseDelimitedLocationsFileParser
{

  private static final Country usa = Countries.lookupCountry("US");

  public GNISFileParser(final InputStream stream)
    throws ParserException
  {
    super(stream, "\\|");
  }

  @Override
  protected Location parseLocation(final Map<String, String> locationDataMap)
  {
    if (locationDataMap == null)
    {
      return null;
    }

    final String featureClass = locationDataMap.get("FEATURE_CLASS");
    if (featureClass != null && featureClass.equals("Populated Place"))
    {
      try
      {
        // City name is in the form: city, state
        final String city = locationDataMap.get("FEATURE_NAME") + ", "
                            + locationDataMap.get("STATE_ALPHA");

        return getLocation(locationDataMap,
                           city,
                           usa,
                           "PRIM_LAT_DEC",
                           "PRIM_LONG_DEC",
                           "ELEVATION");
      }
      catch (final ParserException e)
      {
        return null;
      }
    }
    else
    {
      return null;
    }
  }

}
