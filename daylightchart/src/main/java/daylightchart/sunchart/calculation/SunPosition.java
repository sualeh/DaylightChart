package daylightchart.sunchart.calculation;


import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.sunposition.calculation.ExtendedSunPositionAlgorithm.SolarEphemerides;

public class SunPosition
  implements Serializable, Comparable<SunPosition>
{

  private static final long serialVersionUID = -7394558865293598834L;

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
   * {@inheritDoc}
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
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
