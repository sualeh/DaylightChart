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
package daylightchart.locationparser;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Maps country names.
 * 
 * @author sfatehi
 */
public final class Iso3166Country
{

  private final static Map<String, String> iso3166CountryCodeMap;
  private final static Map<String, String> countryNameMap;

  static
  {
    iso3166CountryCodeMap = new HashMap<String, String>();
    countryNameMap = new HashMap<String, String>();
    final Locale[] availableLocales = Locale.getAvailableLocales();
    for (final Locale locale: availableLocales)
    {
      final String iso3166CountryCode = locale.getCountry();
      final String countryName = locale.getDisplayCountry();
      boolean hasIso3166CountryCode = iso3166CountryCode != null
                                      && iso3166CountryCode.length() > 0;
      boolean hasCountryName = countryName != null && countryName.length() > 0;
      if (hasIso3166CountryCode && hasCountryName)
      {
        iso3166CountryCodeMap.put(iso3166CountryCode, countryName);
        countryNameMap.put(countryName, iso3166CountryCode);
      }
    }
  }

  /**
   * Checks whether a country name is valid.
   * 
   * @param countryName
   *        Country name
   * @return Whether a ISO 3166 country code is valid
   */
  public static boolean isCountryName(final String countryName)
  {
    return iso3166CountryCodeMap.containsKey(countryName);
  }

  /**
   * Checks whether a ISO 3166 country code is valid.
   * 
   * @param iso3166CountryCode
   *        ISO 3166 country code
   * @return Whether a ISO 3166 country code is valid
   */
  public static boolean isIso3166CountryCode(final String iso3166CountryCode)
  {
    return countryNameMap.containsKey(iso3166CountryCode);
  }

  /**
   * Looks up a given country name for a given ISO 3166 country code.
   * 
   * @param iso3166CountryCode
   *        ISO 3166 country code
   * @return Country name
   */
  public static String lookupCountryName(final String iso3166CountryCode)
  {
    return iso3166CountryCodeMap.get(iso3166CountryCode);
  }

  /**
   * Looks up a ISO 3166 country code for a given country name.
   * 
   * @param countryName
   *        Country name.
   * @return ISO 3166 country code
   */
  public static String lookupIso3166CountryCode(final String countryName)
  {
    return countryNameMap.get(countryName);
  }

  private Iso3166Country()
  {

  }

}
