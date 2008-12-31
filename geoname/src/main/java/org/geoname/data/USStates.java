/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2009, Sualeh Fatehi.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * In-memory database of US states.
 * 
 * @author Sualeh Fatehi
 */
@SuppressWarnings("boxing")
public final class USStates
{

  private static final long serialVersionUID = -2155899588206966572L;

  private static final Map<String, USState> alphaCodeMap = new HashMap<String, USState>();
  private static final Map<Integer, USState> numericCodeMap = new HashMap<Integer, USState>();
  private static final Map<String, USState> stateNameMap = new HashMap<String, USState>();

  /**
   * Loads data from internal database.
   */
  static
  {
    BufferedReader reader = null;
    try
    {
      reader = new BufferedReader(new InputStreamReader(USStates.class
        .getClassLoader().getResourceAsStream("us.states.data")));

      String line;
      while ((line = reader.readLine()) != null)
      {

        final String[] fields = line.split(",");

        final boolean invalidNumberOfFields = fields.length != 3;
        final boolean invalidHasNulls = fields[0] == null || fields[1] == null
                                        || fields[2] == null;
        final boolean invalidLengths = fields[1].length() != 2
                                       && fields[1].length() != 0
                                       || fields[2].length() == 0;
        if (invalidNumberOfFields || invalidHasNulls || invalidLengths)
        {
          throw new IllegalArgumentException("Invalid US state record: " + line);
        }

        final USState state = new USState(fields[2], fields[1], Integer
          .parseInt(fields[0]));
        alphaCodeMap.put(state.getFips5_2AlphaCode(), state);
        numericCodeMap.put(state.getFips5_2NumericCode(), state);
        stateNameMap.put(state.getName(), state);
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
   * Gets a collection of all US states.
   * 
   * @return All US states.
   */
  public static Set<USState> getAllUSStates()
  {
    return new HashSet<USState>(alphaCodeMap.values());
  }

  /**
   * Looks up a state from the provided string - whether a state code or
   * a state name.
   * 
   * @param stateString
   *        USState
   * @return USState ojbect, or null
   */
  public static USState lookupUSState(final String stateString)
  {
    USState state = null;
    if (stateString != null)
    {
      if (stateString.length() == 2)
      {
        if (alphaCodeMap.containsKey(stateString))
        {
          state = lookupUSStateAlphaCode(stateString);
        }
      }
      else
      {
        state = lookupUSStateName(stateString);
      }
    }
    return state;
  }

  /**
   * Looks up a state from the two-letter ISO 3166 state code.
   * 
   * @param iso3166USStateCode2
   *        ISO 3166 state code
   * @return USState ojbect, or null
   */
  public static USState lookupUSStateAlphaCode(final String iso3166USStateCode2)
  {
    return alphaCodeMap.get(iso3166USStateCode2);
  }

  /**
   * Looks up a state from the state name.
   * 
   * @param stateName
   *        US state name
   * @return USState ojbect, or null
   */
  public static USState lookupUSStateName(final String stateName)
  {
    return stateNameMap.get(stateName);
  }

  /**
   * Looks up a state from the FIPS-10 state code.
   * 
   * @param usStateNumericCode
   *        FIPS-10 state code
   * @return USState ojbect, or null
   */
  public static USState lookupUSStateNumericCode(final int usStateNumericCode)
  {
    return numericCodeMap.get(usStateNumericCode);
  }

}
