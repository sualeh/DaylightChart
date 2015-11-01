/* 
 * 
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
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
package org.geoname.test;


import static org.junit.Assert.assertEquals;

import org.geoname.parser.DefaultTimezones;
import org.geoname.parser.ParserException;
import org.junit.Test;

public class TestRounding
{

  @Test
  public void rounding()
    throws ParserException
  {
    assertEquals(2.0, DefaultTimezones.roundToNearestFraction(1.9, 0.5), 1e-10);
    assertEquals(2.0, DefaultTimezones.roundToNearestFraction(2.0, 0.5), 1e-10);
    assertEquals(2.0, DefaultTimezones.roundToNearestFraction(2.1, 0.5), 1e-10);

    assertEquals(2.5, DefaultTimezones.roundToNearestFraction(2.4, 0.5), 1e-10);
    assertEquals(2.5, DefaultTimezones.roundToNearestFraction(2.5, 0.5), 1e-10);
    assertEquals(2.5, DefaultTimezones.roundToNearestFraction(2.6, 0.5), 1e-10);

// assertEquals(0.0, DefaultTimezones.roundToNearestFraction(-0.1, 0.5),
    // 1e-10);
    assertEquals(0.0, DefaultTimezones.roundToNearestFraction(0, 0.5), 1e-10);
// assertEquals(0.0, DefaultTimezones.roundToNearestFraction(0.1, 0.5),
    // 1e-10);

    assertEquals(-2.0,
                 DefaultTimezones.roundToNearestFraction(-1.9, 0.5),
                 1e-10);
    assertEquals(-2.0,
                 DefaultTimezones.roundToNearestFraction(-2.0, 0.5),
                 1e-10);
    assertEquals(-2.0,
                 DefaultTimezones.roundToNearestFraction(-2.1, 0.5),
                 1e-10);

    assertEquals(-2.5,
                 DefaultTimezones.roundToNearestFraction(-2.4, 0.5),
                 1e-10);
    assertEquals(-2.5,
                 DefaultTimezones.roundToNearestFraction(-2.5, 0.5),
                 1e-10);
    assertEquals(-2.5,
                 DefaultTimezones.roundToNearestFraction(-2.6, 0.5),
                 1e-10);
  }

}
