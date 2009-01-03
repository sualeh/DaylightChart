package daylightchart.sunchart.calculation;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.LocalDate;

/**
 * Sun positions on a given date.
 * 
 * @author sfatehi
 */
public class SunPositions
  implements Serializable, Comparable<SunPositions>, Iterable<SunPosition>
{

  private static final long serialVersionUID = -6448204793272141613L;

  private final LocalDate date;
  private final List<SunPosition> sunPositions;

  /**
   * Sun positions on a given date.
   * 
   * @param date
   *        Date
   */
  public SunPositions(final LocalDate date)
  {
    this.date = date;
    sunPositions = new ArrayList<SunPosition>();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(final SunPositions o)
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
   * @return the date
   */
  public LocalDate getDate()
  {
    return date;
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
   * @see java.lang.Iterable#iterator()
   */
  public Iterator<SunPosition> iterator()
  {
    return sunPositions.iterator();
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

  void add(final SunPosition sunPosition)
  {
    if (sunPosition == null || !sunPosition.getDate().equals(date))
    {
      throw new IllegalArgumentException("Cannot add " + sunPosition);
    }
    sunPositions.add(sunPosition);
    Collections.sort(sunPositions);
  }

}
