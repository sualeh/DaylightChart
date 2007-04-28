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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import daylightchart.location.Country;

/**
 * In-memory database of locations.
 * 
 * @author Sualeh Fatehi
 */
public final class DefaultTimezonesUtility
{

  /**
   * Compares time zone ids.
   * 
   * @author sfatehi
   */
  private static final class TimeZoneIdComparator
    implements Serializable, Comparator<String>
  {

    private static final long serialVersionUID = -1376900272123395097L;

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Comparator#compare(java.lang.Object,
     *      java.lang.Object)
     */
    public int compare(final String timeZoneId1, final String timeZoneId2)
    {
      int comparison = 0;

      if (timeZoneId1 == null || timeZoneId2 == null)
      {
        return comparison;
      }

      // 1. Compare time zone offset
      if (comparison == 0)
      {
        comparison = (int) (DefaultTimezones
          .getStandardTimeZoneOffsetHours(timeZoneId1) - DefaultTimezones
          .getStandardTimeZoneOffsetHours(timeZoneId2));
      }
      // 2. Compare time zone parts
      if (comparison == 0)
      {
        comparison = timeZoneId1.split("/").length
                     - timeZoneId2.split("/").length;
      }
      // 3. Do a basic string comparison
      if (comparison == 0)
      {
        comparison = timeZoneId1.compareTo(timeZoneId2);
      }

      return comparison;
    }

  }

  private static StringBuffer logBuffer;

  /**
   * Recreate the default time zones file.
   * 
   * @param args
   * @throws IOException
   */
  public static final void main(final String args[])
    throws IOException
  {

    logBuffer = new StringBuffer();

    final Map<Country, List<String>> defaultTimezones = DefaultTimezones
      .getMap();

    // Sort all time zones lists
    final TimeZoneIdComparator timeZoneIdComparator = new TimeZoneIdComparator();
    final Set<Entry<Country, List<String>>> entrySet = defaultTimezones
      .entrySet();
    for (final Entry<Country, List<String>> entry: entrySet)
    {
      final List<String> defaultTimeZonesList = entry.getValue();
      if (defaultTimeZonesList.size() > 1)
      {
        Collections.sort(defaultTimeZonesList, timeZoneIdComparator);
        log(entry);
      }
    }

    // Write default time zones file
    final List<Country> countries = new ArrayList<Country>(defaultTimezones
      .keySet());
    Collections.sort(countries);
    BufferedWriter writer;

    final String defaultTimeZonesFileName = args[0];
    writer = new BufferedWriter(new FileWriter(new File(defaultTimeZonesFileName)));
    for (final Country country: countries)
    {
      final List<String> defaultTimeZonesList = defaultTimezones.get(country);
      for (final String timeZoneId: defaultTimeZonesList)
      {
        writer.write(country.getIso3166Code2());
        writer.write(",");
        writer.write(timeZoneId);
        writer.newLine();
      }
    }
    writer.close();

    final String logFileName = args[1];
    writer = new BufferedWriter(new FileWriter(new File(logFileName)));
    writer.write(logBuffer.toString());
    writer.close();

  }

  @SuppressWarnings("boxing")
  private static void log(final Entry<Country, List<String>> entry)
  {
    final List<String> timeZones = entry.getValue();
    final Set<Double> timeZoneOffsetHoursSet = new HashSet<Double>();
    for (final String timeZoneId: timeZones)
    {
      timeZoneOffsetHoursSet.add(DefaultTimezones
        .getStandardTimeZoneOffsetHours(timeZoneId));
    }
    if (timeZones.size() == timeZoneOffsetHoursSet.size())
    {
      return;
    }

    final NumberFormat numberFormat = NumberFormat.getInstance();
    numberFormat.setMinimumIntegerDigits(2);
    numberFormat.setMinimumFractionDigits(1);

    logBuffer.append(entry.getKey() + ", " + entry.getKey().getIso3166Code2()
                     + "\n");
    logBuffer.append("[");
    for (final String timeZoneId: timeZones)
    {
      final double standardTimeZoneOffsetHours = DefaultTimezones
        .getStandardTimeZoneOffsetHours(timeZoneId);
      logBuffer.append("\n  "
                       + numberFormat.format(standardTimeZoneOffsetHours)
                       + "  " + timeZoneId + "");
    }
    logBuffer.append("\n]\n\n");
  }

}
