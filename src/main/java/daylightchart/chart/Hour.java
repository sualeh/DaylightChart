/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
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
package daylightchart.chart;


import java.io.Serializable;

/**
 * Represents an hour. An Hour can be converted to a string in standard
 * format, and in 24 hour time format. This object also adjusts the time
 * for daylight savings time.
 * 
 * @author Sualeh Fatehi
 */
public final class Hour
  implements Serializable, Comparable<Hour>
{

  /** Hour fields. */
  public enum Field
  {
    /** Hours. */
    HOURS,
    /** Minutes. */
    MINUTES,
    /** Seconds. */
    SECONDS
  }

  private static final long serialVersionUID = 6973647622518973008L;

  /**
   * Internal storage for hour.
   */
  private double hour;

  /**
   * Internal storage for daylight savings time option. Contols the
   * output of the time format.
   */
  private boolean inDaylightSavings;

  /**
   * Internal storage for 24 hour time format option. Contols the output
   * of the time format.
   */
  private boolean time24;

  /**
   * Constructor for the hour, given the value of the hour.
   * 
   * @param hour
   *        The value of the hour.
   */
  public Hour(final double hour)
  {
    setHour(hour);
  }

  /**
   * Copy constructor. Copies the value of a provided hour.
   * 
   * @param hour
   *        Hour to copy the value from.
   */
  public Hour(final Hour hour)
  {
    if (hour == null)
    {
      throw new IllegalArgumentException("Null argument");
    }

    setHour(hour.hour);
    time24 = hour.time24;
    inDaylightSavings = hour.inDaylightSavings;
  }

  /**
   * Constructor for the hour, given the value of the hour, and output
   * specifications.
   * 
   * @param hours
   *        The value of the hours.
   * @param minutes
   *        The value of the minutes.
   */
  public Hour(final int hours, final int minutes)
  {
    // check range of minutes
    if (minutes < 0 || minutes > 60)
    {
      throw new IllegalArgumentException("Minutes out of range.");
    }
    setHour(hours + minutes / 60D);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(final Hour hour)
  {
    int comparison;

    comparison = getField(Field.HOURS) - hour.getField(Field.HOURS);
    if (comparison == 0)
    {
      comparison = getField(Field.MINUTES) - hour.getField(Field.MINUTES);
    }

    return comparison;
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
    final Hour other = (Hour) obj;
    if (Double.doubleToLongBits(hour) != Double.doubleToLongBits(other.hour))
    {
      return false;
    }
    if (inDaylightSavings != other.inDaylightSavings)
    {
      return false;
    }
    if (time24 != other.time24)
    {
      return false;
    }
    return true;
  }

  /**
   * Gets an hour field - such as hours, minutes, or seconds. Signs will
   * be consistent.
   * 
   * @param field
   *        One of the field constants specifying the field to be
   *        retrieved.
   * @return Value of the hour field.
   */
  public int getField(final Field field)
  {
    final double hour;
    int hours;
    int minutes;
    final int seconds;
    int returnField = 0;

    hour = getHour();

    // calculate integer hours
    hours = (int) Math.floor(hour);

    // calculate integer minutes
    minutes = (int) Math.round((hour - Math.floor(hour)) * 60D);

    // calculate integer seconds
    seconds = 0; // NOT CALCULATED!

    // adjust values
    if (minutes == 60)
    {
      minutes = 0;
      hours++;
    }

    // decide which field to return
    if (field == Field.HOURS)
    {
      returnField = hours;
    }
    else if (field == Field.MINUTES)
    {
      returnField = minutes;
    }
    else if (field == Field.SECONDS)
    {
      returnField = seconds;
    }

    return returnField;
  }

  /**
   * The value of the hour.
   * 
   * @return Value for Hour.
   */
  public double getHour()
  {
    double hour = this.hour;
    if (inDaylightSavings)
    {
      hour++;
    }
    return hour;
  }

  /**
   * Whether the time should be output in 24-hour time format.
   * 
   * @return Whether output should be in 24-hour time format.
   */
  public boolean getTime24()
  {
    return time24;
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
    temp = Double.doubleToLongBits(hour);
    result = prime * result + (int) (temp ^ temp >>> 32);
    result = prime * result + (inDaylightSavings? 1231: 1237);
    result = prime * result + (time24? 1231: 1237);
    return result;
  }

  /**
   * Whether daylight savings time is in effect.
   * 
   * @return Whether in DST.
   */
  public boolean isInDaylightSavings()
  {
    return inDaylightSavings;
  }

  /**
   * Set whether this hour uses daylight savings time.
   * 
   * @param inDaylightSavings
   *        the inDaylightSavings to set
   */
  public void setInDaylightSavings(final boolean inDaylightSavings)
  {
    this.inDaylightSavings = inDaylightSavings;
  }

  /**
   * Set whether the time should be reported in 24 hour format.
   * 
   * @param time24
   *        Whether the time should be reported in 24 hour format
   */
  public void setTime24(final boolean time24)
  {
    this.time24 = time24;
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
    int hours;
    final int minutes;
    String padder = "0";
    String modifier = "   ";

    // get hours and minutes
    hours = getField(Field.HOURS);
    minutes = getField(Field.MINUTES);

    // convert to 12 hour clock
    if (!time24)
    {
      boolean isPM = false;
      if (hours >= 12)
      {
        isPM = true;
        hours -= 12;
      } // end if
      if (hours == 0)
      {
        hours = 12;
      }
      modifier = isPM? " pm": " am";
      padder = " ";
    }

    representation.append(hours > 9? "": padder).append(hours).append(":")
      .append(minutes > 9? "": "0").append(minutes).append(modifier);

    return new String(representation);
  }

  /**
   * Sets the hour. Converts to be within the 24 hour range.
   * 
   * @param hour
   *        Hour to set.
   */
  private void setHour(final double hour)
  {
    double dayHour;

    dayHour = hour % 24D;
    if (dayHour < 0)
    {
      dayHour += hour;
    }
    this.hour = dayHour;
  }

}
