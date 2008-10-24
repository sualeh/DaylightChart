package org.pointlocation6709;


public class Utility
{

  /**
   * Splits a double value into it's sexagesimal parts. Each part has
   * the same sign as the provided value.
   * 
   * @param value
   *        Value to split
   * @return Split parts
   */
  public static int[] sexagesimalSplit(final double degrees)
  {
    final double value = degrees;
    final double absValue = Math.abs(value);

    int units;
    int minutes;
    int seconds;
    final int sign = value < 0? -1: 1;

    // Calculate absolute integer units
    units = (int) Math.floor(absValue);
    seconds = (int) Math.round((absValue - units) * 3600D);

    // Calculate absolute integer minutes
    minutes = seconds / 60; // Integer arithmetic
    if (minutes == 60)
    {
      minutes = 0;
      units++;
    }

    // Calculate absolute integer seconds
    seconds = seconds % 60;

    // Correct for sign
    units = units * sign;
    minutes = minutes * sign;
    seconds = seconds * sign;

    return new int[] {
        units, minutes, seconds
    };
  }

  private Utility()
  {
    // Prevent instantiation
  }

}
