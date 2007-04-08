package daylightchart.test;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import daylightchart.location.Angle;
import daylightchart.location.Latitude;
import daylightchart.location.Longitude;

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

    assertEquals("-15° 27\'", angle.toString());
    assertEquals("15° 27\' S", latitude.toString());
    assertEquals("15° 27\' W", longitude.toString());
  }

  @Test
  public void positiveAngleFormat()
  {
    final Angle angle = Angle.fromDegrees(15.45);
    final Latitude latitude = new Latitude(angle);
    final Longitude longitude = new Longitude(angle);

    assertEquals("15° 27\'", angle.toString());
    assertEquals("15° 27\' N", latitude.toString());
    assertEquals("15° 27\' E", longitude.toString());
  }

}
