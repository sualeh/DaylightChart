package org.pointlocation6709.test;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.pointlocation6709.Angle;
import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.Angle.Field;
import org.pointlocation6709.parser.CoordinateParser;
import org.pointlocation6709.parser.ParserException;

public class TestCoordinateParser
{

  @Test
  public void latitude_1()
    throws ParserException
  {
    parseAndCheckLatitude("+483612.20", 48, 36, 12);
    parseAndCheckLatitude("-483612.20", -48, -36, -12);
    parseAndCheckLatitude("-48° 36\" 12.20\'", -48, -36, -12);
    parseAndCheckLatitude("48° 36\" 12.20\' N", 48, 36, 12);
    parseAndCheckLatitude("48° 36\" 12.20\' S", -48, -36, -12);
  }

  @Test
  public void latitude_2()
    throws ParserException
  {
    parseAndCheckLatitude("+003612.20", 0, 36, 12);
    parseAndCheckLatitude("-003612.20", 0, -36, -12);
    parseAndCheckLatitude("0° 36\" 12.20\'", 0, 36, 12);
    parseAndCheckLatitude("-0° 36\" 12.20\'", 0, -36, -12);
    parseAndCheckLatitude("0° 36\" 12.20\' N", 0, 36, 12);
    parseAndCheckLatitude("0° 36\" 12.20\' S", 0, -36, -12);
  }

  @Test
  public void longitude_1()
    throws ParserException
  {
    parseAndCheckLongitude("+0483612.20", 48, 36, 12);
    parseAndCheckLongitude("-0483612.20", -48, -36, -12);
    parseAndCheckLongitude("48° 36\" 12.20\'", 48, 36, 12);
    parseAndCheckLongitude("-48° 36\" 12.20\'", -48, -36, -12);
    parseAndCheckLongitude("48° 36\" 12.20\' E", 48, 36, 12);
    parseAndCheckLongitude("48° 36\" 12.20\' W", -48, -36, -12);
  }

  @Test
  public void longitude_2()
    throws ParserException
  {
    parseAndCheckLongitude("+0003612.20", 0, 36, 12);
    parseAndCheckLongitude("-0003612.20", 0, -36, -12);
    parseAndCheckLongitude("0° 36\" 12.20\'", 0, 36, 12);
    parseAndCheckLongitude("-0° 36\" 12.20\'", 0, -36, -12);
    parseAndCheckLongitude("0° 36\" 12.20\' N", 0, 36, 12);
    parseAndCheckLongitude("0° 36\" 12.20\' S", 0, -36, -12);
  }

  private void checkAngle(Angle angle,
                          String string,
                          int degrees,
                          int minutes,
                          int seconds)
  {
    assertEquals(string + " - degrees do not match", degrees, angle
      .getField(Field.DEGREES));
    assertEquals(string + " - minutes do not match", minutes, angle
      .getField(Field.MINUTES));
    assertEquals(string + " - seconds do not match", seconds, angle
      .getField(Field.SECONDS));
  }

  private void parseAndCheckLatitude(String string,
                                     int degrees,
                                     int minutes,
                                     int seconds)
    throws ParserException
  {
    Latitude latitude = CoordinateParser.parseLatitude(string);
    checkAngle(latitude, string, degrees, minutes, seconds);
  }

  private void parseAndCheckLongitude(String string,
                                      int degrees,
                                      int minutes,
                                      int seconds)
    throws ParserException
  {
    Longitude longitude = CoordinateParser.parseLongitude(string);
    checkAngle(longitude, string, degrees, minutes, seconds);
  }

}
