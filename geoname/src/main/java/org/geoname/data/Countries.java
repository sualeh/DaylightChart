/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2014, Sualeh Fatehi.
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
package org.geoname.data;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.geoname.parser.UnicodeReader;

/**
 * In-memory database of locations.
 * 
 * @author Sualeh Fatehi
 */
public final class Countries
{

  private static final Map<String, Country> iso3166CountryCodeMap;
  private static final Map<String, Country> fips10CountryCodeMap;
  private static final Map<String, Country> countryNameMap = new HashMap<String, Country>();

  /**
   * Loads data from internal database.
   */
  static
  {
    iso3166CountryCodeMap = readISOCountryData("iso_countries.data");
    fips10CountryCodeMap = readFips10CountryData("fips10_countries.data");

    final Collection<Country> countries = new HashSet<>();
    countries.addAll(iso3166CountryCodeMap.values());
    countries.addAll(fips10CountryCodeMap.values());

    for (final Country country: countries)
    {
      countryNameMap.put(country.getName(), country);
    }
  }

  /**
   * Gets a collection of all countries.
   * 
   * @return All countries.
   */
  public static List<Country> getAllCountries()
  {
    final ArrayList<Country> countries = new ArrayList<Country>(iso3166CountryCodeMap
      .values());
    Collections.sort(countries);
    return countries;
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
        if (country == null)
        {
          Collection<Country> countries = fips10CountryCodeMap.values();
          for (Country fips10Country: countries)
          {
            if (fips10Country.getCode().equals(countryString))
            {
              country = fips10Country;
              break;
            }
          }
        }
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

  private static Map<String, Country> readFips10CountryData(final String dataResource)
  {
    final Map<String, Country> countryCodeMap = new HashMap<>();
    try (final BufferedReader reader = new BufferedReader(new UnicodeReader(Countries.class
                                                                              .getClassLoader()
                                                                              .getResourceAsStream(dataResource),
                                                                            "UTF-8"));)
    {

      String line;
      while ((line = reader.readLine()) != null)
      {

        final String[] fields = line.split(",");

        final boolean invalidNumberOfFields = fields.length != 3;

        final String fips10CountryCode = fields[0];
        final String iso3166CountryCode = fields[1];

        final boolean invalidHasNulls = fips10CountryCode == null
                                        || iso3166CountryCode == null;
        final boolean invalidLengths = fips10CountryCode.length() > 2
                                       || fips10CountryCode.length() > 2
                                       || iso3166CountryCode.isEmpty();
        if (invalidNumberOfFields || invalidHasNulls || invalidLengths)
        {
          throw new IllegalArgumentException("Invalid country record: " + line);
        }

        final Country country;
        if (iso3166CountryCodeMap.containsKey(iso3166CountryCode))
        {
          country = iso3166CountryCodeMap.get(iso3166CountryCode);
        }
        else
        {
          final String countryName = fields[2];
          if (iso3166CountryCode.length() == 2)
          {
            country = new Country(iso3166CountryCode, countryName);
          }
          else if (fips10CountryCode.length() == 2)
          {
            country = new Country(fips10CountryCode, countryName);
          }
          else
          {
            continue;
          }
        }
        countryCodeMap.put(fips10CountryCode, country);
      }
    }
    catch (final IOException e)
    {
      throw new IllegalStateException("Cannot read data from internal database",
                                      e);
    }
    return countryCodeMap;
  }

  private static Map<String, Country> readISOCountryData(final String dataResource)
  {
    final Map<String, Country> countryCodeMap = new HashMap<>();
    try (final BufferedReader reader = new BufferedReader(new InputStreamReader(Countries.class
                                                                                  .getClassLoader()
                                                                                  .getResourceAsStream(dataResource),
                                                                                Charset
                                                                                  .forName("UTF8")));)
    {

      String line;
      while ((line = reader.readLine()) != null)
      {

        final String[] fields = line.split(",");

        final boolean invalidNumberOfFields = fields.length != 2;
        final boolean invalidHasNulls = fields[0] == null || fields[1] == null;
        final boolean invalidLengths = fields[0].length() != 2
                                       || fields[1].isEmpty();
        if (invalidNumberOfFields || invalidHasNulls || invalidLengths)
        {
          throw new IllegalArgumentException("Invalid country record: " + line);
        }

        final Country country = new Country(fields[0], fields[1]);
        countryCodeMap.put(country.getCode(), country);
      }
    }
    catch (final IOException e)
    {
      throw new IllegalStateException("Cannot read data from internal database",
                                      e);
    }
    return countryCodeMap;
  }
}
