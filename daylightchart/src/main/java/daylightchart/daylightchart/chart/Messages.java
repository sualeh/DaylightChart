/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
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
package daylightchart.daylightchart.chart;


import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Message bundle accessor.
 * 
 * @author Sualeh Fatehi
 */
public class Messages
{
  private static final String BUNDLE_NAME = "daylightchart.chart.messages"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
    .getBundle(BUNDLE_NAME);

  /**
   * Gets a string from a resource bundle.
   * 
   * @param key
   *        Key to look up.
   * @return Strings
   */
  public static String getString(final String key)
  {
    try
    {
      return RESOURCE_BUNDLE.getString(key);
    }
    catch (final MissingResourceException e)
    {
      return '!' + key + '!';
    }
  }

  private Messages()
  {
  }
}
