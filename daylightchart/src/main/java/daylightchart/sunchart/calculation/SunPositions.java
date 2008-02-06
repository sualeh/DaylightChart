package daylightchart.sunchart.calculation;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.LocalDate;

public class SunPositions
  implements Serializable, Comparable<SunPositions>
{

  private static final long serialVersionUID = -6448204793272141613L;

  private final LocalDate date;
  private final List<SunPosition> sunPositions;

  public SunPositions(final LocalDate date)
  {
    this.date = date;
    sunPositions = new ArrayList<SunPosition>();
  }

  public void add(final SunPosition sunPosition)
  {
    if (sunPosition == null || !sunPosition.getDate().equals(date))
    {
      throw new IllegalArgumentException("Cannot add " + sunPosition);
    }
    sunPositions.add(sunPosition);
    Collections.sort(sunPositions);
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
   * @return the sunPositions
   */
  public List<SunPosition> getSunPositions()
  {
    return sunPositions;
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
