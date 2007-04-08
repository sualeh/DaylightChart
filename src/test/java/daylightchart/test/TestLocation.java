package daylightchart.test;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import daylightchart.location.$Formatter;
import daylightchart.location.$Parser;
import daylightchart.location.Latitude;
import daylightchart.location.Location;
import daylightchart.location.Longitude;
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

    final String strLoc = "Aberdeen;UK;Europe/London;+5710-00204;-0507";
    final Location location = $Parser.parseLocation(strLoc);

    assertEquals(strLoc, $Formatter.formatLocation(location));

  }

}
