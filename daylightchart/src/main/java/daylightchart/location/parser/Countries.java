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
package daylightchart.location.parser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import daylightchart.location.Country;

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
    BufferedReader reader = null;
    try
    {
      reader = new BufferedReader(new InputStreamReader(Countries.class
        .getClassLoader().getResourceAsStream("countries.data")));

      String line;
      while ((line = reader.readLine()) != null)
      {

        final String[] fields = line.split(",");

        final boolean invalidNumberOfFields = fields.length != 3;
        final boolean invalidHasNulls = fields[0] == null || fields[1] == null
                                        || fields[2] == null;
        final boolean invalidLengths = fields[0].length() != 2
                                       || fields[2].length() == 0;
        if (invalidNumberOfFields || invalidHasNulls || invalidLengths)
        {
          throw new IllegalArgumentException("Invalid country record: " + line);
        }

        final Country country = new Country(fields[2], fields[0], fields[1]);
        iso3166CountryCodeMap.put(country.getIso3166Code2(), country);
        fips10CountryCodeMap.put(country.getFips10Code(), country);
        countryNameMap.put(country.getName(), country);
      }
    }
    catch (final IOException e)
    {
      throw new IllegalStateException("Cannot read data from internal database",
                                      e);
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
          throw new IllegalStateException("Cannot read data from internal database",
                                          e);
        }
      }
    }
  }

  /**
   * Gets a collection of all countries.
   * 
   * @return All countries.
   */
  public static Set<Country> getAllCountries()
  {
    return new HashSet<Country>(iso3166CountryCodeMap.values());
  }

  /**
   * Looks up a country from the provided string - whether a country
   * code or a country name.
   * 
   * @param countryString
   *        Country
   * @return Country ojbect, or null
   */
  public static Country lookupCountry(final String countryString)
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

  /**
   * Looks up a country from the country name.
   * 
   * @param countryName
   *        Country name
   * @return Country ojbect, or null
   */
  public static Country lookupCountryName(final String countryName)
  {
    return countryNameMap.get(countryName);
  }

  /**
   * Looks up a country from the FIPS-10 country code.
   * 
   * @param fips10CountryCode
   *        FIPS-10 country code
   * @return Country ojbect, or null
   */
  public static Country lookupFips10CountryCode(final String fips10CountryCode)
  {
    return fips10CountryCodeMap.get(fips10CountryCode);
  }

  /**
   * Looks up a country from the two-letter ISO 3166 country code.
   * 
   * @param iso3166CountryCode2
   *        ISO 3166 country code
   * @return Country ojbect, or null
   */
  public static Country lookupIso3166CountryCode2(final String iso3166CountryCode2)
  {
    return iso3166CountryCodeMap.get(iso3166CountryCode2);
  }

}
