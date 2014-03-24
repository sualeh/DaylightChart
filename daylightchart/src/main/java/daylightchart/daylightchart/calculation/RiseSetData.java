package daylightchart.daylightchart.calculation;


import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contains a data point for the sunrise and sunset, and twilight times
 * for single day.
 * 
 * @author sfatehi
 */
public final class RiseSetData
{
  private final RiseSet riseSet;
  private final RiseSet twilight;

  RiseSetData(final RiseSet riseSet, final RiseSet twilight)
  {
    this.riseSet = riseSet;
    this.twilight = twilight;
  }

  /**
   * Date for this data point.
   * 
   * @return Date for this data point
   */
  public LocalDate getDate()
  {
    return riseSet.getDate();
  }

  /**
   * Sunrise.
   * 
   * @return Sunrise.
   */
  public LocalDateTime getSunrise()
  {
    return riseSet.getSunrise();
  }

  /**
   * Sunset.
   * 
   * @return Sunset.
   */
  public LocalDateTime getSunset()
  {
    return riseSet.getSunset();
  }

  /**
   * Twilight, before sunrise.
   * 
   * @return Twilight, before sunrise.
   */
  public LocalDateTime getTwilightRise()
  {
    return twilight.getSunrise();
  }

  /**
   * Twilight, after sunset.
   * 
   * @return Twilight, after sunset.
   */
  public LocalDateTime getTwilightSet()
  {
    return twilight.getSunset();
  }

}
