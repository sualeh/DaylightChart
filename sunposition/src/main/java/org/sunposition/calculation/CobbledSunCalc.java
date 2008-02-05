/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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
package org.sunposition.calculation;


/**
 * <p>
 * Computes the times of sunrise and sunset for a specified date and
 * location. Also finds the sun's co-ordinates and ephemerides for a
 * given hour.
 * </p>
 * <p>
 * Algorithms from "Astronomy on the Personal Computer" by Oliver
 * Montenbruck and Thomas Pfleger. Springer Verlag 1994. ISBN
 * 3-540-57700-9.
 * </p>
 * <p>
 * This is a reasonably accurate and very robust procedure for sunrise
 * that will handle unusual cases, such as the one day in the year in
 * arctic latitudes that the sun rises, but does not set.
 * </p>
 * 
 * @author Sualeh Fatehi
 */
class CobbledSunCalc
  extends BaseSunPositionAlgorithm
  implements ExtendedSunPositionAlgorithm
{

  private static class SolarEphemerides
    implements ExtendedSunPositionAlgorithm.SolarEphemerides
  {

    private double declination;
    private double rightAscension;
    private double hourAngle;
    private double azimuth;
    private double altitude;
    private double equationOfTime;

    /**
     * {@inheritDoc}
     * 
     * @see org.sunposition.calculation.SolarEphemerides#getAltitude()
     */
    public double getAltitude()
    {
      return altitude;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sunposition.calculation.SolarEphemerides#getAzimuth()
     */
    public double getAzimuth()
    {
      return azimuth;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sunposition.calculation.SolarEphemerides#getDeclination()
     */
    public double getDeclination()
    {
      return declination;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sunposition.calculation.SolarEphemerides#getEquationOfTime()
     */
    public double getEquationOfTime()
    {
      return equationOfTime;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sunposition.calculation.SolarEphemerides#getHourAngle()
     */
    public double getHourAngle()
    {
      return hourAngle;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sunposition.calculation.SolarEphemerides#getRightAscension()
     */
    public double getRightAscension()
    {
      return rightAscension;
    }

    void setAltitude(final double altitude)
    {
      this.altitude = altitude;
    }

    void setAzimuth(final double azimuth)
    {
      this.azimuth = azimuth;
    }

    void setDeclination(final double declination)
    {
      this.declination = declination;
    }

    void setEquationOfTime(final double equationOfTime)
    {
      this.equationOfTime = equationOfTime;
    }

    void setHourAngle(final double hourAngle)
    {
      this.hourAngle = hourAngle;
    }

    void setRightAscension(final double rightAscension)
    {
      this.rightAscension = rightAscension;
    }

  }

  /**
   * <p>
   * Computes the time of sunrise and sunset for this date. This uses an
   * exhaustive search algorithm described in "Astronomy on the Personal
   * Computer" by Montenbruck and Pfleger. Consequently, it is rather
   * slow.
   * </p>
   * <p>
   * This interpolation-based method finds the times when the quadratic
   * crosses the X axis - has zero altitude - and these times are
   * candidates for a rising or setting event. This system can also deal
   * with the situation when the rising and setting occur within the
   * hour - the quadratic then has two zeros. Finally, if the two zeros
   * are the same, then there must be a 'grazing' event in that
   * interval, and the algorithm can detect the event.
   * </p>
   * The steps in the procedure are:
   * <ol>
   * <li>Calculate the sine of the altitude (corrected for refraction,
   * parallax and the limb radius). Using the sine of the altitude saves
   * some calculation and means that the values will always be within
   * the range +1 to -1.</li>
   * <li>Save sign of the altitude at 0h</li>
   * <li>Take the values of the sine of the altitude for each set of
   * three consecutive hours, correct for the refraction, parallax and
   * fit a quadratic curve through these three points.</li>
   * <li>Calculate the 'discriminant' for the quadratic, and classify
   * as:</li>
   * <ul>
   * <li>No roots within the 2 hour interval - move onto the next 2
   * hour range and start from step 1</li>
   * <li>Two roots, one root within the interval - classify as rising
   * or setting event and appropriate flag</li>
   * <ul>
   * <li>If two flags set, i.e. rising and setting events both found,
   * go to step 5</li>
   * <li>If one flag set, then return to step 1 for next two hour
   * interval, to search for remaining event</li>
   * </ul>
   * <li>Two roots, both within the interval - set the rising and
   * setting flags and go to step 5</li>
   * <li>One root (i.e. both roots same) within interval - set both
   * rising and setting flags and go to step 5.
   * </ul>
   * <li>If neither rise nor set flag is set, then warn user that
   * object is 'always above' or 'always below' horizon depending on the
   * sign of the altitude in step 2, and move onto next object or quit.
   * </ol>
   * 
   * @param horizon
   *        The adopted true altitude of the horizon in degrees. Use one
   *        of the following values. <br>
   *        <ul>
   *        <li>SUNRISE_SUNSET</li>
   *        <li>CIVIL_TWILIGHT</li>
   *        <li>NAUTICAL_TWILIGHT</li>
   *        <li>ASTRONOMICAL_TWILIGHT</li>
   *        </ul>
   * @return Array for sunrise and sunset times. Use RISE and SET as
   *         indices into this array.
   * @see <a
   *      href="http://www.merrymeet.com/minow/sunclock/Sun.java">Sun.java</a>
   * @see <a
   *      href="http://www.btinternet.com/~kburnett/kepler/moonrise.html">Moon
   *      and Sun rise and set for any latitude</a>
   */
  public double[] calcRiseSet(final double horizon)
  {

    double timeRise;
    double timeSet;
    double hour;
    double YMinus;
    double YThis;
    double YPlus;
    double XExtreme;
    double YExtreme;
    double A, B, C;
    double discriminant;
    double root1;
    double root2;
    int numRoots;
    double DX;

    final double sinHorizon = sinD(horizon);
    YMinus = sinD(calcSolarEphemerides(0).getAltitude()) - sinHorizon;

    if (YMinus > 0)
    {
      timeRise = SunPositionAlgorithm.ABOVE_HORIZON;
      timeSet = SunPositionAlgorithm.ABOVE_HORIZON;
    }
    else
    {
      timeRise = SunPositionAlgorithm.BELOW_HORIZON;
      timeSet = SunPositionAlgorithm.BELOW_HORIZON;
    }

    for (hour = -timezoneOffset; hour <= -timezoneOffset + 24; hour += 1)
    {
      YThis = sinD(calcSolarEphemerides(hour).getAltitude()) - sinHorizon;
      YPlus = sinD(calcSolarEphemerides(hour + 1).getAltitude()) - sinHorizon;
      YMinus = sinD(calcSolarEphemerides(hour - 1).getAltitude()) - sinHorizon;

      /*
       * Quadratic interpolation through the three points: [-1, YMinus],
       * [0, YThis], [+1, yNext] (These must not lie on a straight
       * line.)
       */
      root1 = 0;
      root2 = 0;
      numRoots = 0;
      A = 0.5 * (YMinus + YPlus) - YThis;
      B = 0.5 * (YPlus - YMinus);
      C = YThis;
      XExtreme = -B / (2D * A);
      YExtreme = (A * XExtreme + B) * XExtreme + C;
      discriminant = B * B - 4D * A * C;
      if (discriminant >= 0)
      { /* intersects x-axis? */
        DX = 0.5 * Math.sqrt(discriminant) / Math.abs(A);
        root1 = XExtreme - DX;
        root2 = XExtreme + DX;
        if (Math.abs(root1) <= 1)
        {
          numRoots++;
        }
        if (Math.abs(root2) <= 1)
        {
          numRoots++;
        }
        if (root1 < -1)
        {
          root1 = root2;
        }
      }

      /*
       * Quadratic interpolation result: numroots Number of roots found
       * (0, 1, or 2). If numroots == 0, there is no event in this
       * range. root1 First root. (numroots >= 1) root2 Second root.
       * (numroots == 2) YMinus Y-value at interpolation start. If < 0,
       * root1 is a rise event. YExtreme Maximum value of y (numroots ==
       * 2) - this determines whether a 2-root event is a rise-set or a
       * set-rise.
       */
      switch (numRoots)
      {
        case 0: /* No root at this hour */
          break;

        case 1: /* Found either a rise or a set */
          if (YMinus < 0)
          {
            timeRise = hour + root1;
          }
          else
          {
            timeSet = hour + root1;
          }
          break;

        case 2: /* Found both a rise and a set */
          if (YExtreme < 0)
          {
            timeRise = hour + root2;
            timeSet = hour + root1;
          }
          else
          {
            timeRise = hour + root1;
            timeSet = hour + root2;
          }
          break;

        default:
          break;
      }

      // exit condition
      if (isEvent(timeRise) && isEvent(timeSet))
      {
        break;
      }

    }

    if (isEvent(timeRise))
    {
      timeRise = modPositive(timeRise, 24);
    }
    if (isEvent(timeSet))
    {
      timeSet = modPositive(timeSet, 24);
    }

    return new double[] {
        timeRise, timeSet
    };
  }

  /**
   * Calculate solar ephemeris.
   * 
   * @param hour
   *        Hour past midnight (for the current day).
   * @return Solar ephemerides, as an array. Use one of the following
   *         values to index elements of the array. <br>
   *         &#8729; DECLINATION <br>
   *         &#8729; RIGHTASCENSION <br>
   *         &#8729; HOURANGLE <br>
   *         &#8729; AZIMUTH <br>
   *         &#8729; ALTITUDE <br>
   *         &#8729; EQUATIONOFTIME
   *         </p>
   *         See also: <a
   *         href="http://www.srrb.noaa.gov/highlights/sunrise/program.txt">
   *         NOAA calculations </a>
   */
  public ExtendedSunPositionAlgorithm.SolarEphemerides calcSolarEphemerides(final double hour)
  {

    final double J2000_OFFSET = 2451545.5;

    final double JD;
    final double J2000;
    final double t;
    final double t0;
    final double UT;
    final double GMST;
    final double LMST;
    final double L;
    final double g;
    final double C;
    final double q;
    final double alpha;
    final double e;
    final double EqT;
    double declination;
    double RA;
    final double tau;
    double altitude;
    double azimuth;
    final SolarEphemerides epherimides = new SolarEphemerides();

    // Universal Time
    UT = hour - timezoneOffset;
    // Julian Day Number
    final int monthx = (month - 14) / 12; // Relies on integer math
    JD = 1461 * (year + 4800 + monthx) / 4 + 367 * (month - 2 - 12 * monthx)
         / 12 - 3 * (year + 4900 + monthx) / 100 / 4 + day - 32075;
    // Julian Day 2000.00
    J2000 = JD - J2000_OFFSET;
    // Number of Julian Centuries since J2000.0
    t = (J2000 + UT / 24D) / 36525D;
    t0 = J2000 / 36525D;

    // Greenwich Mean Sidereal Time (GMST)
    GMST = 6.697374558 + 1.0027379093 * UT
           + (8640184.812866 + (0.093104 - 6.2E-6 * t0) * t0) * t0 / 3600D;
    // Local Mean Sidereal Time (LMST)
    LMST = 24D * frac((GMST + longitude / 15D) / 24D);

    // Geometric Mean Anomaly of the Sun (degrees)
    g = range360(357.52910 + (35999.05030 - (0.0001559 + 0.00000048 * t) * t)
                 * t);

    // Geometric Mean Longitude of the Sun corrected for aberration
    // (degrees)
    q = range360(280.46645 + (36000.76983 + 0.0003032 * t) * t);

    // Equation of Center for the Sun
    C = (1.914602 - (0.004817 - 0.000014 * t) * t) * sinD(g)
        + (0.019993 - 0.000101 * t) * sinD(2D * g) + 0.000289 * sinD(3D * g);

    // Sun's Geocentric Apparent Ecliptic Longitude adjusted for
    // aberration (degrees)
    L = q + C;

    alpha = L - 2.466 * sinD(2D * L) + 0.053 * sinD(4D * L);

    /* Mean Obliquity of the Ecliptic (epsilon) (degrees) */
    e = 23.0 + (26.0 + (21.448 - t * (46.8150 + t * (0.00059 - t * 0.001813))) / 60.0) / 60.0;

    /*
     * Declination is one element of the astronomical coordinate system,
     * and can be thought of as latitude on the earth projected onto the
     * sky. It is usually denoted by the lower-case Greek letter delta
     * and is measured north (+) and south (-) of the celestial equator
     * in degrees, minutes, and seconds of arc. The celestial equator is
     * defined as being at declination zero degrees; the north and south
     * celestial poles are defined as being at +90 and -90 degrees,
     * respectively.
     */
    // Declination (theta) (degrees)
    declination = Math.atan(tanD(e) * sinD(alpha));
    declination = Math.toDegrees(declination);
    epherimides.setDeclination(declination);

    /*
     * Right ascension is one element of the astronomical coordinate
     * system, and can be though of as longitude on the earth projected
     * onto the sky. It is usually denoted by the lower-case Greek
     * letter alpha and is measured eastward in hours, minutes, and
     * seconds of time from the vernal equinox. There are 24 hours of
     * right ascension, though the 24-hour line is always taken as 0
     * hours. More rarely, one sometimes sees right ascension in
     * degrees, in which case there are 360 degrees of right ascension
     * to make a complete circuit of the sky.
     */
    // Right Ascension (hours)
    // ra is always in the same quadrant as ecliptic longitude, so we
    // use atan2
    RA = Math.atan2(cosD(e) * sinD(L), cosD(L));
    RA = Math.toDegrees(RA);
    RA = range360(RA) / 15D; // convert degrees to hours
    epherimides.setRightAscension(RA);

    /*
     * Hour angle is the sidereal time that has elapsed since the object
     * was on the meridian (hour angle west, positive) or until it will
     * again be on the meridian (hour angle east, negative).
     */
    // Hour Angle (degrees)
    tau = 15D * (LMST - RA);
    epherimides.setHourAngle(tau);

    /*
     * Altitude is the angular distance from the observer's horizon,
     * usually taken to be that horizon that is unobstructed by natural
     * or artificial features (such as mountains or buildings), measured
     * directly up from the horizon toward the zenith; positive numbers
     * indicate values of altitude above the horizon, and negative
     * numbers indicate below the horizon. Negative numbers are usually
     * used in terms of how far below the horizon the sun is situated at
     * a given time (for example, the boundary between civil twilight
     * and nautical twilight is when the sun is at altitude -6 degrees).
     */
    // Altitude, or elevation (degrees)
    altitude = Math.asin(sinD(latitude) * sinD(declination) + cosD(latitude)
                         * cosD(declination) * cosD(tau));
    altitude = Math.toDegrees(altitude);
    epherimides.setAltitude(altitude);

    /*
     * Azimuth is the angular distance measured clockwise around the
     * observer's horizon in units of degrees; astronomers usually take
     * north to be 0 degrees, east to be 90 degrees, south to be 180
     * degrees, and west to be 270 degrees.
     */
    // Azimuth (degrees)
    azimuth = Math.acos((sinD(altitude) * sinD(latitude) - sinD(declination))
                        / (cosD(altitude) * cosD(latitude)));
    azimuth = Math.toDegrees(azimuth);
    if (azimuth * tau < 0)
    {
      azimuth *= -1;
    }
    epherimides.setAzimuth(azimuth);

    /*
     * Equation of time is the correction, in minutes and seconds, to be
     * applied to local time apparent time (sundial time) for deriving
     * Local Mean Time (LMT), or Local Solar Time (LST).
     */
    // Equation of Time (minutes)
    EqT = (q / 15D - RA) * 60D;
    epherimides.setEquationOfTime(EqT);

    // The Sun's ecliptic latitude, b, can be approximated by
    // b = 0.
    //
    // The distance of the Sun from the Earth, R, in astronomical units
    // (AU),
    // can be approximated by
    // R = 1.00014 - 0.01671 cos g - 0.00014 cos 2g
    //
    // The angular semidiameter of the Sun, SD, in degrees, is simply
    // SD = 0.2666 / R

    return epherimides;
  }

  /**
   * Rounds towards zero.
   * 
   * @param number
   *        Value to round.
   * @return Value rounded towards zero (returned as double).
   */
  private double frac(final double number)
  {
    double result;

    result = number - trunc(number);
    if (result < 0.0)
    {
      result += 1.0;
    }

    return result;
  }

  /**
   * Checks if an event is in the valid range.
   * 
   * @param eventOccurence
   *        The event to check.
   * @return Boolean for valid.
   */
  private boolean isEvent(final double eventOccurence)
  {
    return eventOccurence != SunPositionAlgorithm.ABOVE_HORIZON
           && eventOccurence != SunPositionAlgorithm.BELOW_HORIZON;
  }

  /**
   * Modulus function that always returns a positive value. For example,
   * mod(-3, 24) is 21.
   * 
   * @param numerator
   *        Numerator.
   * @param denominator
   *        Denominator.
   * @return Modulus of the numerator and denominator.
   */
  private double modPositive(final double numerator, final double denominator)
  {
    double result = Math.IEEEremainder(numerator, denominator);
    if (result < 0)
    {
      result += denominator;
    }
    return result;
  }

  /**
   * Angle to convert to within a range of 0 to 360 degrees. Modulus
   * function that always returns a positive value.
   * 
   * @param angle
   *        Angle to convert.
   * @return Angle within a range of 0 to 360 degrees.
   */
  private double range360(final double angle)
  {
    return modPositive(angle, 360D);
  }

  /**
   * Returns the integer nearest to zero. (This behaves differently than
   * Math.floor() for negative values.)
   * 
   * @param number
   *        The number to convert
   * @return Integer number nearest zero (returned as double).
   */
  private int trunc(final double number)
  {
    int result = (int) Math.floor(Math.abs(number));
    if (number < 0.0)
    {
      result = -result;
    }
    return result;
  }

  private static void printE(int year)
  {
    double m, ve, ss, ae, ws;
    m = ((double) year - 2000) / 1000;
    ve = 2451623.80984 + 365242.37404 * m + 0.05169 * m * m - 0.00411 * m * m
         * m - 0.00057 * m * m * m * m;
    display_date(ve);
    ss = 2451716.56767 + 365241.62603 * m + 0.00325 * m * m + 0.00888 * m * m
         * m - 0.00030 * m * m * m * m;
    display_date(ss);
    ae = 2451810.21715 + 365242.01767 * m - 0.11575 * m * m + 0.00337 * m * m
         * m + 0.00078 * m * m * m * m;
    display_date(ae);
    ws = 2451900.05952 + 365242.74049 * m - 0.06223 * m * m - 0.00823 * m * m
         * m + 0.00032 * m * m * m * m;
    display_date(ws);
  }

  private static void display_date(double jdn)
  {
    double p = Math.floor(jdn + 0.5);
    double s1 = p + 68569;
    double n = Math.floor(4 * s1 / 146097);
    double s2 = s1 - Math.floor((146097 * n + 3) / 4);
    double i = Math.floor(4000 * (s2 + 1) / 1461001);
    double s3 = s2 - Math.floor(1461 * i / 4) + 31;
    double q = Math.floor(80 * s3 / 2447);
    double e = s3 - Math.floor(2447 * q / 80);
    double s4 = Math.floor(q / 11);
    double mm = q + 2 - 12 * s4;
    double yy = 100 * (n - 49) + i + s4;
    double dd = e + jdn - p + 0.5;

    double hrs, min, sec, tm = dd;

    dd = Math.floor(tm);
    tm = 24 * (tm - dd);
    hrs = Math.floor(tm);
    tm = 60 * (tm - hrs);
    min = Math.floor(tm);
    tm = 60 * (tm - min);
    sec = Math.round(tm);

    System.out.print((int) yy + "/" + (int) mm + "/" + (int) dd + " "
                     + (int) hrs + ":" + (int) min + ":" + (int) sec + "\t");
  }

}
