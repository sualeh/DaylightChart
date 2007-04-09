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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import daylightchart.location.$Formatter;
import daylightchart.location.$Parser;
import daylightchart.location.Angle;
import daylightchart.location.Coordinates;
import daylightchart.location.Location;
import daylightchart.location.ParserException;

/**
 * Location tests.
 */
public class TestLocation
{

  @Test
  public void location()
    throws ParserException
  {

    final String locationString = "Aberdeen;UK;Europe/London;+5710-00204/";
    final Location location = $Parser.parseLocation(locationString);

    assertEquals(locationString, $Formatter.formatLocation(location));

  }

  @Test
  public void locations()
    throws ParserException
  {
    final InputStream dataStream = this.getClass().getClassLoader()
      .getResourceAsStream("locations.data");
    final InputStreamReader reader = new InputStreamReader(dataStream);
    List<Location> locations = $Parser.parseLocations(reader);
    assertEquals(99, locations.size());
  }

  @Test
  public void coordinates1()
    throws ParserException
  {

    final String coordinatesString = "+40-075/";
    final Coordinates coordinates = $Parser.parseCoordinates(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(0, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.SECONDS));
  }

  @Test
  public void coordinates2()
    throws ParserException
  {

    final String coordinatesString = "+4012-07500/";
    final Coordinates coordinates = $Parser.parseCoordinates(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(12, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(0, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.SECONDS));

  }

  @Test
  public void coordinates3()
    throws ParserException
  {

    final String coordinatesString = "+401213-0750015/";
    final Coordinates coordinates = $Parser.parseCoordinates(coordinatesString);

    assertEquals(40, coordinates.getLatitude().getField(Angle.Field.DEGREES));
    assertEquals(12, coordinates.getLatitude().getField(Angle.Field.MINUTES));
    assertEquals(13, coordinates.getLatitude().getField(Angle.Field.SECONDS));

    assertEquals(-75, coordinates.getLongitude().getField(Angle.Field.DEGREES));
    assertEquals(0, coordinates.getLongitude().getField(Angle.Field.MINUTES));
    assertEquals(-15, coordinates.getLongitude().getField(Angle.Field.SECONDS));

  }

  @Test(expected = ParserException.class)
  @Ignore
  public void badCoordinates1()
    throws ParserException
  {
    $Parser.parseCoordinates("+40/");
  }

  @Test(expected = ParserException.class)
  @Ignore
  public void badCoordinates2()
    throws ParserException
  {
    $Parser.parseCoordinates("4000/");
  }

  @Test(expected = ParserException.class)
  @Ignore
  public void badCoordinates3()
    throws ParserException
  {
    $Parser.parseCoordinates("+40121300-075001500/");
  }

  @Test(expected = ParserException.class)
  @Ignore
  public void badCoordinates4()
    throws ParserException
  {
    $Parser.parseCoordinates("+40121-075001/");
  }

}
