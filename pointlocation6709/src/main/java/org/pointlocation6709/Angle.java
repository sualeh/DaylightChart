/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package org.pointlocation6709;


import java.io.Serializable;

/**
 * Represents an angle in degrees or radians. Has convinience methods to
 * do trigonometric operations, and normalizations.
 * 
 * @author Sualeh Fatehi
 */
public class Angle
  implements Serializable, Comparable<Angle>
{
  /** Angle fields. */
  public enum Field
  {
    /** Degrees. */
    DEGREES,
    /** Minutes. */
    MINUTES,
    /** Seconds. */
    SECONDS,
  }

  private static final long serialVersionUID = -6330836471692225095L;

  /**
   * Static contruction method, contructs an angle from the degree value
   * provided.
   * 
   * @param degrees
   *        Value of the angle in degrees.
   * @return A new Angle.
   */
  public static Angle fromDegrees(final double degrees)
  {
    return fromRadians(degrees * Math.PI / 180D);
  }

  /**
   * Static contruction method, contructs an angle from the radian value
   * provided.
   * 
   * @param radians
   *        Value of the angle in radians.
   * @return A new Angle.
   */
  public static Angle fromRadians(final double radians)
  {
    return new Angle(radians);
  }

  /**
   * Modulus function that always returns a positive value. For example,
   * mod(-3, 24) is 21.
   * 
   * @param numerator
   *        Numerator for the modulus.
   * @param denominator
   *        Denomintor for the modulus.
   * @return Modulus of the operands.
   */
  private static double mod(final double numerator, final double denominator)
  {
    double result = Math.IEEEremainder(numerator, denominator);
    if (result < 0)
    {
      result = result + denominator;
    }
    return result;
  }

  private final double radians;
  private final int[] sexagesimalDegreeParts;

  /**
   * Default constructor. Initializes the angle to a value of 0.
   */
  private Angle(final double radians)
  {
    this.radians = radians;
    this.sexagesimalDegreeParts = Utility.sexagesimalSplit(getDegrees());
  }

  /**
   * Copy constructor. Copies the value of a provided angle.
   * 
   * @param angle
   *        Angle to copy the value from.
   */
  public Angle(final Angle angle)
  {
    this(angle.radians);
  }

  /**
   * {@inheritDoc}
   */
  public final int compareTo(final Angle angle)
  {
    int comparison;
    comparison = getField(Field.DEGREES) - angle.getField(Field.DEGREES);
    if (comparison == 0)
    {
      comparison = getField(Field.MINUTES) - angle.getField(Field.MINUTES);
    }
    if (comparison == 0)
    {
      comparison = getField(Field.SECONDS) - angle.getField(Field.SECONDS);
    }
    return comparison;
  }

  /**
   * Calculates the cosine of the angle.
   * 
   * @return Cosine.
   */
  public final double cos()
  {
    return Math.cos(radians);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final Angle other = (Angle) obj;
    if (Double.doubleToLongBits(radians) != Double
      .doubleToLongBits(other.radians))
    {
      return false;
    }
    return true;
  }

  /**
   * Degrees value of the angle.
   * 
   * @return Value in degrees.
   */
  public final double getDegrees()
  {
    return radians * 180D / Math.PI;
  }

  /**
   * <p>
   * Gets an angle field - such as degrees, minutes, or seconds. Signs
   * will be consistent.
   * </p>
   * <p>
   * Throws NullPointerException if field is not provided.
   * </p>
   * 
   * @param field
   *        One of the field constants specifying the field to be
   *        retrieved.
   * @return Value of the specified field.
   */
  public final int getField(final Field field)
  {
    return sexagesimalDegreeParts[field.ordinal()];
  }

  /**
   * Radians value of the angle.
   * 
   * @return Value in radians.
   */
  public final double getRadians()
  {
    return radians;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(radians);
    result = prime * result + (int) (temp ^ temp >>> 32);
    return result;
  }

  /**
   * Normalizes the angle so that it is within the range 0 - 360
   * degrees.
   * 
   * @return Normalized angle
   */
  public final Angle normalize()
  {
    return Angle.fromRadians(Angle.mod(radians, 2 * Math.PI));
  }

  /**
   * Calculates the sine of the angle.
   * 
   * @return Sine.
   */
  public final double sin()
  {
    return Math.sin(radians);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuffer representation = new StringBuffer();
    final String direction = getDirection();

    final int absIntDegrees = Math.abs(getField(Field.DEGREES));
    final int absIntMinutes = Math.abs(getField(Field.MINUTES));
    final int absIntSeconds = Math.abs(getField(Field.SECONDS));

    representation.append(absIntDegrees).append("°").append(" ").append(Math
      .abs(absIntMinutes)).append("'");
    if (absIntSeconds > 0)
    {
      representation.append(" ").append(absIntSeconds).append("\"");
    }
    if (direction == null)
    {
      if (radians < 0)
      {
        representation.insert(0, "-");
      }
    }
    else
    {
      representation.append(" ").append(direction);
    }

    return new String(representation);

  }

  /**
   * Get direction.
   * 
   * @return Direction.
   */
  protected String getDirection()
  {
    return null;
  }

  /**
   * Validates the angle.
   * 
   * @param degreesRange
   *        The +/- range to validate.
   */
  protected final void validateDegreesRange(final int degreesRange)
  {
    if (Math.abs(getDegrees()) > degreesRange)
    {
      throw new IllegalArgumentException("Validation error: " + radians
                                         + " radians");
    }
  }

}
