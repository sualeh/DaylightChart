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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.pointlocation6709.Longitude;
import org.pointlocation6709.Utility;

import daylightchart.location.Countries;
import daylightchart.location.Country;

/**
 * In-memory database of locations.
 * 
 * @author Sualeh Fatehi
 */
public final class DefaultTimezones
{

  private static final long serialVersionUID = -2155899588206966572L;

  private static final Map<Country, Set<TimeZone>> defaultTimezones = new HashMap<Country, Set<TimeZone>>();

  /**
   * Loads data from internal database.
   */
  static
  {
    try
    {

      Set<Country> allCountries = Countries.getAllCountries();
      for (Country country: allCountries)
      {
        defaultTimezones.put(country, new HashSet<TimeZone>());
      }

      final BufferedReader reader = new BufferedReader(new InputStreamReader(DefaultTimezones.class
        .getClassLoader().getResourceAsStream("default.timezones.data")));

      String line;
      while ((line = reader.readLine()) != null)
      {

        final String[] fields = line.split(",");

        final boolean invalidNumberOfFields = fields.length != 2;
        final boolean invalidHasNulls = fields[0] == null || fields[1] == null;
        final boolean invalidLengths = fields[0].length() != 2
                                       || fields[1].length() == 0;
        if (invalidNumberOfFields || invalidHasNulls || invalidLengths)
        {
          throw new IllegalArgumentException("Invalid default timezone record: "
                                             + line);
        }
        String iso3166CountryCode2 = fields[0];
        String timezoneId = fields[1];

        // Add default timezone for the country
        TimeZone defaultTimezone = TimeZone.getTimeZone(timezoneId);
        Country country = Countries
          .lookupIso3166CountryCode2(iso3166CountryCode2);
        Set<TimeZone> defaultTimezonesForCountry = defaultTimezones
          .get(country);
        defaultTimezonesForCountry.add(defaultTimezone);
      }
      reader.close();

    }
    catch (final IOException e)
    {
      throw new IllegalStateException("Cannot read data from internal database",
                                      e);
    }
  }

  /**
   * Looks up a country from the provided string - whether a country
   * code or a country name.
   * 
   * @param country
   *        Country
   * @return Country ojbect, or null
   */
  public static TimeZone attemptTimeZoneMatch(Country country,
                                              final Longitude longitude)
  {

    if (country == null || longitude == null)
    {
      return null;
    }

    List<TimeZone> defaultTimezonesForCountry = new ArrayList<TimeZone>(defaultTimezones
      .get(country));
    if (defaultTimezonesForCountry == null
        || defaultTimezonesForCountry.size() == 0)
    {
      return null;
    }
    else if (defaultTimezonesForCountry.size() == 1)
    {
      return defaultTimezonesForCountry.get(0);
    }

    // More than one timezone found - try a match by longitude
    final double tzOffsetHours = longitude.getDegrees() / 15D;
    final int[] fields = Utility.sexagesimalSplit(tzOffsetHours);
    int absHours = Math.abs(fields[0]);
    int absMinutes = Math.abs(fields[1]);
    if (absMinutes >= 30)
    {
      absHours = absHours + 1;
      absMinutes = 0;
    }

    return null;

  }

}
