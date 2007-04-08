package daylightchart.test;


import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;
import org.junit.Test;

import daylightchart.astronomical.SunAlgorithm;
import daylightchart.astronomical.SunAlgorithmFactory;
import daylightchart.location.$Parser;
import daylightchart.location.Location;
import daylightchart.location.ParserException;

/**
 * Location tests.
 */
public class TestSunAlgorithm
{

  private static final double ONE_MINUTE = 1 / 60D;
  private static final double DELTA = 0.5 * ONE_MINUTE;

  private final SunAlgorithm mSunAlgorithm = SunAlgorithmFactory.getInstance();

  /**
   * Aberdeen
   * <ol>
   * <li>High latitude</li>
   * <li>N/W quadrant of the globe</li>
   * <li>Time zone 0</li>
   * </ol>
   * 
   * @throws ParserException
   */
  @Test
  public void aberdeen()
    throws ParserException
  {

    final String strLoc = "Aberdeen;UK;Europe/London;+5710-00204;-0507";
    final double riseset[] = calcRiseSet(new LocalDate(2001, 12, 2), $Parser
      .parseLocation(strLoc));

    assertEquals(8 + 24 * ONE_MINUTE, riseset[SunAlgorithm.RISE], DELTA);
    assertEquals(12 + 3 + 31 * ONE_MINUTE, riseset[SunAlgorithm.SET], DELTA);

  }

  /**
   * Bakersfield
   * <ol>
   * <li>Time zone -8</li>
   * </ol>
   * 
   * @throws ParserException
   */
  @Test
  public void bakersfield()
    throws ParserException
  {

    final String strLoc = "Bakersfield, CA;USA;America/Los_Angeles;+3523-11901;+1336";
    final double riseset[] = calcRiseSet(new LocalDate(2003, 6, 24), $Parser
      .parseLocation(strLoc));

    assertEquals(4 + 42 * ONE_MINUTE, riseset[SunAlgorithm.RISE], DELTA);
    assertEquals(12 + 7 + 15 * ONE_MINUTE, riseset[SunAlgorithm.SET], DELTA);

  }

  /**
   * Geneva
   * <ol>
   * <li>N/E quadrant of the globe</li>
   * </ol>
   * 
   * @throws ParserException
   */
  @Test
  public void geneva()
    throws ParserException
  {

    final String strLoc = "Geneva;Switzerland;Europe/Zurich;+4612+00609;+0014";
    final double riseset[] = calcRiseSet(new LocalDate(2001, 11, 28), $Parser
      .parseLocation(strLoc));

    assertEquals(7 + 54 * ONE_MINUTE, riseset[SunAlgorithm.RISE], DELTA);
    assertEquals(12 + 4 + 53 * ONE_MINUTE, riseset[SunAlgorithm.SET], DELTA);

  }

  /**
   * Nairobi
   * <ol>
   * <li>Latitude close to equator</li>
   * </ol>
   * 
   * @throws ParserException
   */
  @Test
  public void nairobi()
    throws ParserException
  {

    final String strLoc = "Nairobi;Kenya;Africa/Nairobi;-0117+03649;+0003";
    final double riseset[] = calcRiseSet(new LocalDate(2003, 6, 24), $Parser
      .parseLocation(strLoc));

    assertEquals(6 + 33 * ONE_MINUTE, riseset[SunAlgorithm.RISE], DELTA);
    assertEquals(12 + 6 + 36 * ONE_MINUTE, riseset[SunAlgorithm.SET], DELTA);

  }

  /**
   * Sydney
   * <ol>
   * <li>S/W quadrant of the globe</li>
   * <li>Time zone +10</li>
   * </ol>
   * 
   * @throws ParserException
   */
  @Test
  public void sydney()
    throws ParserException
  {

    final String strLoc = "Sydney;Australia;Australia/Sydney;-3352+15113;+1237";
    final double riseset[] = calcRiseSet(new LocalDate(2001, 12, 2), $Parser
      .parseLocation(strLoc));

    assertEquals(4 + 37 * ONE_MINUTE, riseset[SunAlgorithm.RISE], DELTA);
    assertEquals(18 + 52 * ONE_MINUTE, riseset[SunAlgorithm.SET], DELTA);

  }

  private double[] calcRiseSet(final LocalDate date, final Location location)
  {
    mSunAlgorithm.setLatitude(location.getCoordinates().getLatitude()
      .getDegrees());
    mSunAlgorithm.setLongitude(location.getCoordinates().getLongitude()
      .getDegrees());
    mSunAlgorithm.setTimeZoneOffset(location.getTimeZone().getRawOffset()
                                    / (1000.0 * 60.0 * 60.0));
    mSunAlgorithm.setYear(date.getYear());
    mSunAlgorithm.setMonth(date.getMonthOfYear());
    mSunAlgorithm.setDay(date.getDayOfMonth());

    return mSunAlgorithm.calcRiseSet(SunAlgorithm.SUNRISE_SUNSET);
  }

}
