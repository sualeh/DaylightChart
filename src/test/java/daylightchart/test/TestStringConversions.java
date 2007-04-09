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
package daylightchart.test;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import daylightchart.iso6709.Angle;
import daylightchart.iso6709.Latitude;
import daylightchart.iso6709.Longitude;

/**
 * Location tests.
 */
public class TestStringConversions
{

  @Test
  public void negativeAngleFormat()
  {
    final Angle angle = Angle.fromDegrees(-15.45);
    final Latitude latitude = new Latitude(angle);
    final Longitude longitude = new Longitude(angle);

    assertEquals("-15� 27\' 0\"", angle.toString());
    assertEquals("15� 27\' 0\" S", latitude.toString());
    assertEquals("15� 27\' 0\" W", longitude.toString());
  }

  @Test
  public void positiveAngleFormat()
  {
    final Angle angle = Angle.fromDegrees(15.45);
    final Latitude latitude = new Latitude(angle);
    final Longitude longitude = new Longitude(angle);

    assertEquals("15� 27\' 0\"", angle.toString());
    assertEquals("15� 27\' 0\" N", latitude.toString());
    assertEquals("15� 27\' 0\" E", longitude.toString());
  }

}
