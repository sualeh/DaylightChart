/*
 * Copyright (c) 2004-2007 Sualeh Fatehi. All Rights Reserved.
 */
package daylightchart.chart;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import daylightchart.location.Location;

/**
 * A full year's sunrise and sunset times for a location.
 * 
 * @author Sualeh Fatehi
 */
public class RiseSetYear
{

  private final Location location;
  private final int year;
  private boolean usesDaylightTime;
  private LocalDate dstStart;
  private LocalDate dstEnd;
  private final List<RiseSet> riseSets;

  RiseSetYear(final Location location, final int year)
  {
    this.location = location;
    this.year = year;
    usesDaylightTime = location.getTimeZone().useDaylightTime();
    riseSets = new ArrayList<RiseSet>();
  }

  /**
   * Gets the end of DST.
   * 
   * @return End of DST.
   */
  public Date getDstEndDate()
  {
    Date dstEndDate = null;
    if (usesDaylightTime)
    {
      dstEndDate = dstEnd.toDateTime((LocalTime) null).toGregorianCalendar()
        .getTime();
    }
    return dstEndDate;
  }

  /**
   * Gets the start of DST.
   * 
   * @return Start of DST.
   */
  public Date getDstStartDate()
  {
    Date dstStartDate = null;
    if (usesDaylightTime)
    {
      dstStartDate = dstStart.toDateTime((LocalTime) null)
        .toGregorianCalendar().getTime();
    }
    return dstStartDate;
  }

  /**
   * Location.
   * 
   * @return Location.
   */
  public Location getLocation()
  {
    return location;
  }

  /**
   * Gets a list of rise/ set timings.
   * 
   * @return List of rise/ set timings.
   */
  public List<RiseSet> getRiseSets()
  {
    return riseSets;
  }

  /**
   * Gets the year.
   * 
   * @return Year.
   */
  public int getYear()
  {
    return year;
  }

  /**
   * Whether the location uses DST rules.
   * 
   * @param usesDaylightTime
   *        Whether the location uses DST rules.
   */
  public void setUsesDaylightTime(final boolean usesDaylightTime)
  {
    this.usesDaylightTime = usesDaylightTime;
    for (final RiseSet riseSet: riseSets)
    {
      riseSet.setUsesDaylightTime(usesDaylightTime);
    }
  }

  /**
   * Whether the location uses DST rules.
   * 
   * @return Whether the location uses DST rules.
   */
  public boolean usesDaylightTime()
  {
    return usesDaylightTime;
  }

  void addRiseSet(final RiseSet riseSet)
  {
    riseSets.add(riseSet);
  }

  void setDstEnd(final LocalDate dstEnd)
  {
    this.dstEnd = dstEnd;
  }

  void setDstStart(final LocalDate dstStart)
  {
    this.dstStart = dstStart;
  }

}
