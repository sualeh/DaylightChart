package daylightchart.daylightchart.calculation;


import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public final class RiseSetData
{
  private final RiseSet riseSet;
  private final RiseSet twilight;

  RiseSetData(final RiseSet riseSet, final RiseSet twilight)
  {
    this.riseSet = riseSet;
    this.twilight = twilight;
  }

  public LocalDate getDate()
  {
    return riseSet.getDate();
  }

  public LocalDateTime getSunrise()
  {
    return riseSet.getSunrise();
  }

  public LocalDateTime getSunset()
  {
    return riseSet.getSunset();
  }

  public LocalDateTime getTwilightRise()
  {
    return twilight.getSunrise();
  }

  public LocalDateTime getTwilightSet()
  {
    return twilight.getSunset();
  }

}
