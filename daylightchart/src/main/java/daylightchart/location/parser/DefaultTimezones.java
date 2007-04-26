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
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.pointlocation6709.Longitude;

import daylightchart.location.Countries;
import daylightchart.location.Country;

/**
 * In-memory database of locations.
 * 
 * @author Sualeh Fatehi
 */
public final class DefaultTimezones
{

  private static final Logger LOGGER = Logger.getLogger(DefaultTimezones.class
    .getName());

  private static final Map<Country, Set<String>> defaultTimezones = new HashMap<Country, Set<String>>();

  /**
   * Loads data from internal database.
   */
  static
  {
    try
    {

      final Set<Country> allCountries = Countries.getAllCountries();
      for (final Country country: allCountries)
      {
        defaultTimezones.put(country, new HashSet<String>());
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
        final String iso3166CountryCode2 = fields[0];
        final String timezoneId = fields[1];

        // Add default timezone for the country
        final DateTimeZone defaultTimezone = DateTimeZone.forID(timezoneId);
        if (defaultTimezone.getID() != "GMT")
        {
          final Country country = Countries
            .lookupIso3166CountryCode2(iso3166CountryCode2);
          final Set<String> defaultTimezonesForCountry = defaultTimezones
            .get(country);
          defaultTimezonesForCountry.add(defaultTimezone.getID());
        }
        else
        {
          LOGGER.log(Level.WARNING, "Cannot find timezone " + timezoneId);
        }
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
   * @param longitude
   *        Longitude
   * @return Country ojbect, or null
   */
  public static String attemptTimeZoneMatch(final Country country,
                                            final Longitude longitude)
  {

    if (longitude == null)
    {
      return null;
    }

    // Timezone offset in hours
    double tzOffsetHours;
    tzOffsetHours = longitude.getDegrees() / 15D;
    tzOffsetHours = roundToNearestHalf(tzOffsetHours);

    final List<String> defaultTimezonesForCountry = new ArrayList<String>(defaultTimezones
      .get(country));
    if (defaultTimezonesForCountry == null
        || defaultTimezonesForCountry.size() == 0)
    {
      return createTimeZoneId(tzOffsetHours);
    }
    else if (defaultTimezonesForCountry.size() == 1)
    {
      return defaultTimezonesForCountry.get(0);
    }

    // More than one timezone found - try a match by longitude
    String timeZoneId = null;
    double leastDifference = Double.MAX_VALUE;
    for (final String defaultTimeZoneId: defaultTimezonesForCountry)
    {
      final double difference = Math
        .abs(getStandardTimeZoneOffsetHours(defaultTimeZoneId) - tzOffsetHours);
      if (difference < leastDifference)
      {
        leastDifference = difference;
        timeZoneId = defaultTimeZoneId;
      }
    }

    return timeZoneId;

  }

  public static String createTimeZoneId(final double tzOffsetHours)
  {

    String timeZoneId = "GMT";
    if (tzOffsetHours < 0)
    {
      timeZoneId = timeZoneId + "-";
    }
    else
    {
      timeZoneId = timeZoneId + "+";
    }

    final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(2);
    timeZoneId = timeZoneId
                 + numberFormat.format((int) Math.abs(tzOffsetHours));

    return timeZoneId;

  }

  public static double getStandardTimeZoneOffsetHours(final String timeZoneId)
  {
    if (timeZoneId == null)
    {
      return 0D;
    }
    final DateTimeZone timeZone = DateTimeZone.forID(timeZoneId);
    final long now = new DateTime().getMillis();
    final double tzOffsetHours = timeZone.getStandardOffset(now)
                                 / (60D * 60D * 1000D);
    return tzOffsetHours;
  }

  public static double roundToNearestHalf(final double number)
  {
    return new BigDecimal(number * 2D)
      .round(new MathContext(1, RoundingMode.HALF_UP)).doubleValue() / 2D;
  }

}
