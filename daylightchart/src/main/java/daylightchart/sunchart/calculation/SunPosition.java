package daylightchart.sunchart.calculation;


import org.joda.time.LocalDateTime;
import org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides;

public class SunPosition
{

  private final LocalDateTime dateTime;
  private final double azimuth;
  private final double altitude;

  public SunPosition(final LocalDateTime dateTime,
                     final SolarEphemerides solarEphemerides)
  {
    this.dateTime = dateTime;
    azimuth = solarEphemerides.getAzimuth();
    altitude = solarEphemerides.getAltitude();
  }

  /**
   * @return the altitude
   */
  public double getAltitude()
  {
    return altitude;
  }

  /**
   * @return the azimuth
   */
  public double getAzimuth()
  {
    return azimuth;
  }

  /**
   * @return the dateTime
   */
  public LocalDateTime getDateTime()
  {
    return dateTime;
  }

}
