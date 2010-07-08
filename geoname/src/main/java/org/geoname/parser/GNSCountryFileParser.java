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
package org.geoname.parser;


import java.io.InputStream;
import java.util.Map;

import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.FIPS10AdministrationDivisions;
import org.geoname.data.Location;

/**
 * Parses objects from strings.
 * 
 * @author Sualeh Fatehi
 */
public final class GNSCountryFileParser
  extends BaseDelimitedLocationsFileParser
{

  public GNSCountryFileParser(final InputStream stream)
    throws ParserException
  {
    super(stream, "\t");
  }

  @Override
  protected Location parseLocation(final Map<String, String> locationDataMap)
  {
    if (locationDataMap == null)
    {
      return null;
    }

    final String featureClassification = locationDataMap.get("FC");
    final String nameType = locationDataMap.get("NT");
    if (featureClassification.equals("P")
        && (nameType.equals("C") || nameType.equals("N")))
    {
      try
      {
        final Country country = Countries
          .lookupFips10CountryCode(locationDataMap.get("CC1"));

        final int fips10AdministrationDivisionCode = getInteger(locationDataMap,
                                                                "ADM1",
                                                                0);
        final String fips10AdministrationDivisionName;
        if (fips10AdministrationDivisionCode > 0)
        {
          fips10AdministrationDivisionName = FIPS10AdministrationDivisions
            .lookupFips10AdministrationDivisionName(country, String
              .format("%2d", fips10AdministrationDivisionCode));
        }
        else
        {
          fips10AdministrationDivisionName = null;
        }

        String city;
        if (locationDataMap.containsKey("FULL_NAME_RO"))
        {
          city = locationDataMap.get("FULL_NAME_RO");
        }
        else if (locationDataMap.containsKey("FULL_NAME"))
        {
          city = locationDataMap.get("FULL_NAME");
        }
        else
        {
          return null;
        }
        if (fips10AdministrationDivisionName != null)
        {
          city = city + ", " + fips10AdministrationDivisionName;
        }

        return getLocation(locationDataMap,
                           city,
                           country,
                           "LAT",
                           "LONG",
                           "ELEV");
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
