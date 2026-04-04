/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.chart.data;

import daylightchart.options.TwilightType;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.geoname.data.Location;

/** A full year's sunrise and sunset times for a location. */
public final class RiseSetYearData implements Serializable {

  @Serial private static final long serialVersionUID = -7055404819725658424L;

  private final Location location;
  private final int year;
  private boolean usesDaylightTime;
  private LocalDate dstStart;
  private LocalDate dstEnd;
  private final TwilightType twilight;
  private final List<RawRiseSet> riseSets;
  private final List<RawRiseSet> twilights;
  private final List<DaylightBand> bands;

  RiseSetYearData(final Location location, final TwilightType twilight, final int year) {
    this.location = location;
    this.year = year;
    this.twilight = twilight;
    riseSets = new ArrayList<>();
    twilights = new ArrayList<>();
    bands = new ArrayList<>();
  }

  /**
   * Gets all the daylight bands.
   *
   * @return Daylight bands
   */
  public List<DaylightBand> getBands() {
    return new ArrayList<>(bands);
  }

  /**
   * Gets the end of DST.
   *
   * @return End of DST.
   */
  public LocalDate getDstEndDate() {
    if (usesDaylightTime && dstEnd != null) {
      return dstEnd;
    }
    return null;
  }

  /**
   * Gets the start of DST.
   *
   * @return Start of DST.
   */
  public LocalDate getDstStartDate() {
    if (usesDaylightTime && dstStart != null) {
      return dstStart;
    }
    return null;
  }

  /**
   * Location.
   *
   * @return Location.
   */
  public Location getLocation() {
    return location;
  }

  /**
   * Sunrise and sunset data for the year.
   *
   * @return Sunrise and sunset data for the year
   */
  public List<RiseSetData> getRiseSetData() {
    final List<RiseSetData> riseSetData = new ArrayList<>();
    final List<RiseSet> riseSets = getRiseSets(true);
    final List<RiseSet> twilights = getTwilights();
    for (int i = 0; i < riseSets.size(); i++) {
      final RiseSet riseSet = riseSets.get(i);
      final RiseSet twilight = twilights.get(i);
      riseSetData.add(new RiseSetData(riseSet, twilight));
    }
    return riseSetData;
  }

  /**
   * Gets the twilight type.
   *
   * @return TwilightType type.
   */
  public TwilightType getTwilight() {
    return twilight;
  }

  /**
   * Gets the year.
   *
   * @return Year.
   */
  public int getYear() {
    return year;
  }

  /**
   * Whether the location uses DST rules.
   *
   * @return Whether the location uses DST rules.
   */
  public boolean usesDaylightTime() {
    return usesDaylightTime;
  }

  void addDaylightBands(final List<DaylightBand> bands) {
    this.bands.addAll(bands);
  }

  void addRiseSet(final RawRiseSet riseSet) {
    riseSets.add(riseSet);
  }

  void addTwilight(final RawRiseSet riseSet) {
    twilights.add(riseSet);
  }

  /**
   * Gets a list of rise/ set timings.
   *
   * @param adjustedForDaylightSavings Whether the times need to be adjusted for daylight savings
   *     time
   * @return List of rise/ set timings.
   */
  List<RiseSet> getRiseSets(final boolean adjustedForDaylightSavings) {
    List<RiseSet> copiedRiseSets;
    if (!adjustedForDaylightSavings) {
      copiedRiseSets = new ArrayList<>();
      for (final RawRiseSet riseSetTuple : riseSets) {
        final RiseSet riseSet = new RiseSet(riseSetTuple);
        copiedRiseSets.add(riseSet.withAdjustmentForDaylightSavings(adjustedForDaylightSavings));
      }
    } else {
      copiedRiseSets = new ArrayList<>();
      for (final RawRiseSet riseSetTuple : riseSets) {
        final RiseSet riseSet = new RiseSet(riseSetTuple);
        copiedRiseSets.add(riseSet);
      }
    }
    return Collections.unmodifiableList(copiedRiseSets);
  }

  /**
   * Gets a list of twilight timings.
   *
   * @return List of rise/ set timings.
   */
  List<RiseSet> getTwilights() {
    List<RiseSet> copiedRiseSets;
    copiedRiseSets = new ArrayList<>();
    for (final RawRiseSet riseSetTuple : twilights) {
      final RiseSet riseSet = new RiseSet(riseSetTuple);
      copiedRiseSets.add(riseSet);
    }
    return Collections.unmodifiableList(copiedRiseSets);
  }

  void setDstEnd(final LocalDate dstEnd) {
    this.dstEnd = dstEnd;
  }

  void setDstStart(final LocalDate dstStart) {
    this.dstStart = dstStart;
  }

  void setUsesDaylightTime(final boolean usesDaylightTime) {
    this.usesDaylightTime = usesDaylightTime;
  }
}
