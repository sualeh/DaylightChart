/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
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
package daylightchart.location;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory database of locations.
 * 
 * @author Sualeh Fatehi
 */
public final class Countries
{

  private static final long serialVersionUID = -2155899588206966572L;

  private static final Map<String, Country> iso3166CountryCodeMap = new HashMap<String, Country>();
  private static final Map<String, Country> fips10CountryCodeMap = new HashMap<String, Country>();
  private static final Map<String, Country> countryNameMap = new HashMap<String, Country>();

  /**
   * Loads data from internal database.
   */
  static
  {
    try
    {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(Countries.class
        .getClassLoader().getResourceAsStream("countries.data")));

      String line;
      while ((line = reader.readLine()) != null)
      {
        Country country = new Country(line);
        iso3166CountryCodeMap.put(country.getIso3166CountryCode2(), country);
        fips10CountryCodeMap.put(country.getFips10CountryCode(), country);
        countryNameMap.put(country.getCountryName(), country);
      }
      reader.close();

    }
    catch (final IOException e)
    {
      throw new IllegalStateException("Cannot read data from internal database",
                                      e);
    }
  }

  public static Country lookupIso3166CountryCode2(String iso3166CountryCode2)
  {
    return iso3166CountryCodeMap.get(iso3166CountryCode2);
  }

  public static Country lookupFips10CountryCode(String fips10CountryCode)
  {
    return fips10CountryCodeMap.get(fips10CountryCode);
  }

  public static Country lookupCountryName(String countryName)
  {
    return countryNameMap.get(countryName);
  }

  public static Country lookupCountry(String countryString)
  {
    Country country;
    if (countryString == null)
    {
      country = null;
    }
    else if (countryString.length() == 2)
    {
      if (iso3166CountryCodeMap.containsKey(countryString))
      {
        country = lookupIso3166CountryCode2(countryString);
      }
      else
      {
        country = lookupFips10CountryCode(countryString);
      }
    }
    else
    {
      country = lookupCountryName(countryString);
    }
    return country;
  }

}
