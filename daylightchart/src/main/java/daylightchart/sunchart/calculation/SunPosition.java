package daylightchart.sunchart.calculation;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import us.fatehi.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides;

/**
 * Solar ephemerides at a given date and time.
 *
 * @author sfatehi
 */
public class SunPosition
  implements SolarEphemerides, Serializable, Comparable<SunPosition>
{

  private static final long serialVersionUID = -7394558865293598834L;

  private final LocalDateTime dateTime;
  private final SolarEphemerides solarEphemerides;

  /**
   * Solar ephemerides at a given date and time.
   *
   * @param dateTime
   * @param solarEphemerides
   */
  public SunPosition(final LocalDateTime dateTime,
                     final SolarEphemerides solarEphemerides)
  {
    this.dateTime = dateTime;
    this.solarEphemerides = solarEphemerides;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final SunPosition o)
  {
    return CompareToBuilder.reflectionCompare(this, o);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  /**
   * @return the altitude
   */
  @Override
  public double getAltitude()
  {
    return solarEphemerides.getAltitude();
  }

  /**
   * @return the azimuth
   */
  @Override
  public double getAzimuth()
  {
    return solarEphemerides.getAzimuth();
  }

  /**
   * @return the date
   */
  public LocalDate getDate()
  {
    return dateTime.toLocalDate();
  }

  /**
   * @return the dateTime
   */
  public LocalDateTime getDateTime()
  {
    return dateTime;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides#getDeclination()
   */
  @Override
  public double getDeclination()
  {
    return solarEphemerides.getDeclination();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides#getEquationOfTime()
   */
  @Override
  public double getEquationOfTime()
  {
    return solarEphemerides.getEquationOfTime();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides#getHourAngle()
   */
  @Override
  public double getHourAngle()
  {
    return solarEphemerides.getHourAngle();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides#getRightAscension()
   */
  @Override
  public double getRightAscension()
  {
    return solarEphemerides.getRightAscension();
  }

  /**
   * @return the time
   */
  public LocalTime getTime()
  {
    return dateTime.toLocalTime();
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return ToStringBuilder.reflectionToString(this,
                                              ToStringStyle.MULTI_LINE_STYLE);
  }

}
