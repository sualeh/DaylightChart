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

import org.junit.Ignore;
import org.junit.Test;

import daylightchart.iso6709.LocationPointParser;
import daylightchart.iso6709.Angle;
import daylightchart.iso6709.LocationPoint;
import daylightchart.iso6709.ParserException;

/**
 * Location tests.
 */
public class TestLocationPoint
{

  @Test
  public void coordinates_a()
    throws ParserException
  {

    final String coordinatesString = "+40-075/";
    final LocationPoint coordinates = LocationPointParser
      .parseLocationPoint(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(0, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.SECONDS));
  }

  @Test
  public void coordinates_b()
    throws ParserException
  {

    final String coordinatesString = "+40.20361-075.00417/";
    final LocationPoint coordinates = LocationPointParser
      .parseLocationPoint(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(12, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(13, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(-15, coordinates.getLongitude().getField(Angle.Field.SECONDS));
  }

  @Test
  public void coordinates_c()
    throws ParserException
  {

    final String coordinatesString = "+4012-07500/";
    final LocationPoint coordinates = LocationPointParser
      .parseLocationPoint(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(12, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(0, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.SECONDS));

  }

  @Test
  public void coordinates_d()
    throws ParserException
  {

    final String coordinatesString = "+4012.22-07500.25/";
    final LocationPoint coordinates = LocationPointParser
      .parseLocationPoint(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(12, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(13, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(-15, coordinates.getLongitude().getField(Angle.Field.SECONDS));

  }

  @Test
  public void coordinates_e()
    throws ParserException
  {

    final String coordinatesString = "+401213-0750015/";
    final LocationPoint coordinates = LocationPointParser
      .parseLocationPoint(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(12, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(13, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(-15, coordinates.getLongitude().getField(Angle.Field.SECONDS));

  }

  @Test
  public void coordinates_f()
    throws ParserException
  {

    final String coordinatesString = "+401213.1-0750015.1/";
    final LocationPoint coordinates = LocationPointParser
      .parseLocationPoint(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(12, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(13, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(-15, coordinates.getLongitude().getField(Angle.Field.SECONDS));

  }

  @Test
  public void coordinates_g()
    throws ParserException
  {

    final String coordinatesString = "+40-075+350/";
    final LocationPoint coordinates = LocationPointParser
      .parseLocationPoint(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(0, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.SECONDS));

    assertEquals(350D, coordinates.getAltitude());
  }

  @Test
  public void coordinates_h()
    throws ParserException
  {

    final String coordinatesString = "+40.20361-075.00417+350.517/";
    final LocationPoint coordinates = LocationPointParser
      .parseLocationPoint(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(12, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(13, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(-15, coordinates.getLongitude().getField(Angle.Field.SECONDS));

    assertEquals(350.517, coordinates.getAltitude());
  }

  @Test
  public void coordinates_j()
    throws ParserException
  {

    final String coordinatesString = "+4012-07500-169.2/";
    final LocationPoint coordinates = LocationPointParser
      .parseLocationPoint(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(12, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(0, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.SECONDS));

    assertEquals(-169.2, coordinates.getAltitude());
  }

  @Test
  public void coordinates_k()
    throws ParserException
  {

    final String coordinatesString = "+4012.22-07500.25-169.2/";
    final LocationPoint coordinates = LocationPointParser
      .parseLocationPoint(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(12, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(13, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(-15, coordinates.getLongitude().getField(Angle.Field.SECONDS));

    assertEquals(-169.2, coordinates.getAltitude());
  }

  @Test
  public void coordinates_m()
    throws ParserException
  {

    final String coordinatesString = "+401213-0750015+2.79/";
    final LocationPoint coordinates = LocationPointParser
      .parseLocationPoint(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(12, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(13, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(-15, coordinates.getLongitude().getField(Angle.Field.SECONDS));

    assertEquals(2.79, coordinates.getAltitude());
  }

  @Test
  public void coordinates_n()
    throws ParserException
  {

    final String coordinatesString = "+401213.1-0750015.1+2.79/";
    final LocationPoint coordinates = LocationPointParser
      .parseLocationPoint(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(12, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(13, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(-15, coordinates.getLongitude().getField(Angle.Field.SECONDS));

    assertEquals(2.79, coordinates.getAltitude());
  }

  @Test(expected = ParserException.class)
  @Ignore
  public void badCoordinates1()
    throws ParserException
  {
    LocationPointParser.parseLocationPoint("+40/");
  }

  @Test(expected = ParserException.class)
  @Ignore
  public void badCoordinates2()
    throws ParserException
  {
    LocationPointParser.parseLocationPoint("4000/");
  }

  @Test(expected = ParserException.class)
  @Ignore
  public void badCoordinates3()
    throws ParserException
  {
    LocationPointParser.parseLocationPoint("+40121300-075001500/");
  }

  @Test(expected = ParserException.class)
  @Ignore
  public void badCoordinates4()
    throws ParserException
  {
    LocationPointParser.parseLocationPoint("+40121-075001/");
  }

}
