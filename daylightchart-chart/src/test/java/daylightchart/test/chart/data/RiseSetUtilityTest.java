/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.test.chart.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

import daylightchart.chart.data.DaylightBandType;
import daylightchart.chart.data.RiseSet;
import daylightchart.chart.data.RiseSetData;
import daylightchart.chart.data.RiseSetUtility;
import daylightchart.chart.data.RiseSetYearData;
import daylightchart.options.Options;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.geoname.data.Location;
import org.geoname.parser.LocationsListParser;
import org.geoname.parser.ParserException;
import org.junit.jupiter.api.Test;

public class RiseSetUtilityTest {

  private static final int TOLERANCE_MINUTES = 5;

  @Test
  public void shouldPreserveCairoClockTimesAcrossDstStart() throws ParserException {
    final RiseSetYearData year = createCairoYear();
    final List<RiseSet> days = getTransitionDays(year, DaylightBandType.with_clock_shift);
    final LocalTime[] expectedSunrises = {
      LocalTime.of(5, 20), LocalTime.of(6, 19), LocalTime.of(6, 18)
    };
    final LocalTime[] expectedSunsets = {
      LocalTime.of(18, 27), LocalTime.of(19, 28), LocalTime.of(19, 29)
    };
    assertClockTimes(days, expectedSunrises, expectedSunsets);
  }

  private void assertClockTimes(
      final List<RiseSet> days,
      final LocalTime[] expectedSunrises,
      final LocalTime[] expectedSunsets) {
    for (int i = 0; i < days.size(); i++) {
      final RiseSet day = days.get(i);
      assertThat(
          day.getDate() + " sunrise",
          Math.abs(
              Duration.between(expectedSunrises[i], day.getSunrise().toLocalTime()).getSeconds()),
          lessThanOrEqualTo(60L));
      assertThat(
          day.getDate() + " sunset",
          Math.abs(
              Duration.between(expectedSunsets[i], day.getSunset().toLocalTime()).getSeconds()),
          lessThanOrEqualTo(60L));
    }
  }

  @Test
  public void shouldRetainCairoDstClockShift() throws ParserException {
    final List<RiseSet> days =
        getTransitionDays(createCairoYear(), DaylightBandType.with_clock_shift);
    assertThat(
        Duration.between(
                days.get(0).getSunrise().toLocalTime(), days.get(1).getSunrise().toLocalTime())
            .getSeconds(),
        allOf(greaterThanOrEqualTo(55 * 60L), lessThanOrEqualTo(65 * 60L)));
    assertThat(
        Duration.between(
                days.get(0).getSunset().toLocalTime(), days.get(1).getSunset().toLocalTime())
            .getSeconds(),
        allOf(greaterThanOrEqualTo(55 * 60L), lessThanOrEqualTo(65 * 60L)));
  }

  @Test
  public void shouldKeepCairoReferenceBandsSmoothAcrossDstStart() throws ParserException {
    final List<RiseSet> days =
        getTransitionDays(createCairoYear(), DaylightBandType.without_clock_shift);
    assertSmoothReferenceBands(days);
  }

  private void assertSmoothReferenceBands(final List<RiseSet> days) {
    for (int i = 1; i < days.size(); i++) {
      assertThat(
          days.get(i).getDate() + " reference sunrise change",
          Math.abs(
              Duration.between(
                      days.get(i - 1).getSunrise().toLocalTime(),
                      days.get(i).getSunrise().toLocalTime())
                  .getSeconds()),
          lessThan(5 * 60L));
      assertThat(
          days.get(i).getDate() + " reference sunset change",
          Math.abs(
              Duration.between(
                      days.get(i - 1).getSunset().toLocalTime(),
                      days.get(i).getSunset().toLocalTime())
                  .getSeconds()),
          lessThan(5 * 60L));
    }
  }

  private RiseSetYearData createCairoYear() throws ParserException {
    final Location location =
        LocationsListParser.parseLocation("Cairo;;EG;Africa/Cairo;+30.0444+031.2357/");
    return RiseSetUtility.createRiseSetYear(location, 2026, new Options());
  }

  private List<RiseSet> getTransitionDays(
      final RiseSetYearData year, final DaylightBandType bandType) {
    return getTransitionDays(year, bandType, LocalDate.of(2026, 4, 23));
  }

  private List<RiseSet> getTransitionDays(
      final RiseSetYearData year, final DaylightBandType bandType, final LocalDate firstDate) {
    final List<RiseSet> days =
        year.getBands().stream()
            .filter(band -> band.getDaylightBandType() == bandType)
            .flatMap(band -> band.getRiseSets().stream())
            .filter(
                day ->
                    !day.getDate().isBefore(firstDate)
                        && !day.getDate().isAfter(firstDate.plusDays(2)))
            .sorted()
            .toList();
    assertThat(
        days.stream().map(RiseSet::getDate).toList(),
        is(List.of(firstDate, firstDate.plusDays(1), firstDate.plusDays(2))));
    return days;
  }

  @Test
  public void shouldPreserveBostonBandsAcrossDstStart() throws ParserException {
    assertBostonTransition(
        LocalDate.of(2026, 3, 7),
        new LocalTime[] {LocalTime.of(6, 10), LocalTime.of(7, 8), LocalTime.of(7, 6)},
        new LocalTime[] {LocalTime.of(17, 41), LocalTime.of(18, 43), LocalTime.of(18, 44)},
        1);
  }

  @Test
  public void shouldPreserveBostonBandsAcrossDstEnd() throws ParserException {
    assertBostonTransition(
        LocalDate.of(2026, 10, 31),
        new LocalTime[] {LocalTime.of(7, 16), LocalTime.of(6, 18), LocalTime.of(6, 19)},
        new LocalTime[] {LocalTime.of(17, 39), LocalTime.of(16, 37), LocalTime.of(16, 36)},
        -1);
  }

  @Test
  public void shouldMarkBostonDstOnTransitionDates() throws ParserException {
    final RiseSetYearData year = createBostonYear();
    assertThat(year.getDstStartDate(), is(LocalDate.of(2026, 3, 8)));
    assertThat(year.getDstEndDate(), is(LocalDate.of(2026, 11, 1)));
  }

  private RiseSetYearData createBostonYear() throws ParserException {
    final Location location =
        LocationsListParser.parseLocation("Boston;US-MA;US;America/New_York;+42.357-071.064/");
    return RiseSetUtility.createRiseSetYear(location, 2026, new Options());
  }

  private void assertBostonTransition(
      final LocalDate firstDate,
      final LocalTime[] expectedSunrises,
      final LocalTime[] expectedSunsets,
      final int shiftDirection)
      throws ParserException {
    final RiseSetYearData year = createBostonYear();
    final List<RiseSet> days =
        getTransitionDays(year, DaylightBandType.with_clock_shift, firstDate);
    assertClockTimes(days, expectedSunrises, expectedSunsets);
    assertThat(
        shiftDirection
            * Duration.between(
                    days.get(0).getSunrise().toLocalTime(), days.get(1).getSunrise().toLocalTime())
                .getSeconds(),
        allOf(greaterThanOrEqualTo(55 * 60L), lessThanOrEqualTo(65 * 60L)));
    assertThat(
        shiftDirection
            * Duration.between(
                    days.get(0).getSunset().toLocalTime(), days.get(1).getSunset().toLocalTime())
                .getSeconds(),
        allOf(greaterThanOrEqualTo(55 * 60L), lessThanOrEqualTo(65 * 60L)));
    assertSmoothReferenceBands(
        getTransitionDays(year, DaylightBandType.without_clock_shift, firstDate));
  }

  @Test
  public void shouldMatchBaselineRiseAndSetAcrossWorldLocations() throws ParserException {
    assertRiseAndSet(
        "Boston;US-MA;US;America/New_York;+4232-07104/",
        LocalDate.of(2024, 3, 20),
        LocalTime.of(6, 46, 28),
        LocalTime.of(18, 57, 16));
    assertRiseAndSet(
        "Boston;US-MA;US;America/New_York;+4232-07104/",
        LocalDate.of(2024, 6, 21),
        LocalTime.of(5, 6, 55),
        LocalTime.of(20, 25, 41));
    assertRiseAndSet(
        "Boston;US-MA;US;America/New_York;+4232-07104/",
        LocalDate.of(2024, 12, 21),
        LocalTime.of(7, 10, 49),
        LocalTime.of(16, 14, 34));

    assertRiseAndSet(
        "London;;GB;Europe/London;+5130-00010/",
        LocalDate.of(2024, 3, 20),
        LocalTime.of(6, 2, 25),
        LocalTime.of(18, 14, 43));
    assertRiseAndSet(
        "London;;GB;Europe/London;+5130-00010/",
        LocalDate.of(2024, 6, 21),
        LocalTime.of(4, 43, 15),
        LocalTime.of(21, 22, 1));
    assertRiseAndSet(
        "London;;GB;Europe/London;+5130-00010/",
        LocalDate.of(2024, 12, 21),
        LocalTime.of(8, 4, 3),
        LocalTime.of(15, 53, 59));

    assertRiseAndSet(
        "Sydney;;AU;Australia/Sydney;-3352+15113/",
        LocalDate.of(2024, 3, 20),
        LocalTime.of(6, 58, 23),
        LocalTime.of(19, 7, 31));
    assertRiseAndSet(
        "Sydney;;AU;Australia/Sydney;-3352+15113/",
        LocalDate.of(2024, 6, 21),
        LocalTime.of(7, 0, 3),
        LocalTime.of(16, 53, 40));
    assertRiseAndSet(
        "Sydney;;AU;Australia/Sydney;-3352+15113/",
        LocalDate.of(2024, 12, 21),
        LocalTime.of(5, 41, 7),
        LocalTime.of(20, 5, 3));

    assertRiseAndSet(
        "Nairobi;;KE;Africa/Nairobi;-0117+03649/",
        LocalDate.of(2024, 3, 20),
        LocalTime.of(6, 36, 37),
        LocalTime.of(18, 43, 34));
    assertRiseAndSet(
        "Nairobi;;KE;Africa/Nairobi;-0117+03649/",
        LocalDate.of(2024, 6, 21),
        LocalTime.of(6, 32, 57),
        LocalTime.of(18, 36, 23));
    assertRiseAndSet(
        "Nairobi;;KE;Africa/Nairobi;-0117+03649/",
        LocalDate.of(2024, 12, 21),
        LocalTime.of(6, 24, 46),
        LocalTime.of(18, 37, 16));

    assertRiseAndSet(
        "Reykjavik;;IS;Atlantic/Reykjavik;+6409-02158/",
        LocalDate.of(2024, 3, 20),
        LocalTime.of(7, 26, 44),
        LocalTime.of(19, 45, 16));
    assertRiseAndSet(
        "Reykjavik;;IS;Atlantic/Reykjavik;+6409-02158/",
        LocalDate.of(2024, 6, 21),
        LocalTime.of(2, 55, 23),
        LocalTime.of(0, 4, 5));
    assertRiseAndSet(
        "Reykjavik;;IS;Atlantic/Reykjavik;+6409-02158/",
        LocalDate.of(2024, 12, 21),
        LocalTime.of(11, 22, 31),
        LocalTime.of(15, 29, 58));
  }

  @Test
  public void shouldHandlePolarDayAndNight() throws ParserException {
    final Location location =
        LocationsListParser.parseLocation("Longyearbyen;;NO;Arctic/Longyearbyen;+7813+01533/");
    final RiseSetYearData riseSetYear =
        RiseSetUtility.createRiseSetYear(location, 2024, new Options());

    assertThat(hasDaylightBandFor(riseSetYear, LocalDate.of(2024, 6, 21)), is(true));
    assertThat(hasDaylightBandFor(riseSetYear, LocalDate.of(2024, 12, 21)), is(false));
  }

  private boolean hasDaylightBandFor(final RiseSetYearData riseSetYear, final LocalDate date) {
    return riseSetYear.getBands().stream()
        .flatMap(band -> band.getRiseSets().stream())
        .anyMatch(riseSet -> date.equals(riseSet.getDate()));
  }

  private void assertRiseAndSet(
      final String locationString,
      final LocalDate date,
      final LocalTime expectedSunrise,
      final LocalTime expectedSunset)
      throws ParserException {
    final Location location = LocationsListParser.parseLocation(locationString);
    final RiseSetData riseSetData = getRiseSetData(location, date);

    assertThat(riseSetData, is(notNullValue()));
    assertWithinTolerance(
        location, date, "sunrise", expectedSunrise, riseSetData.getSunrise().toLocalTime());
    assertWithinTolerance(
        location, date, "sunset", expectedSunset, riseSetData.getSunset().toLocalTime());
  }

  private void assertWithinTolerance(
      final Location location,
      final LocalDate date,
      final String label,
      final LocalTime expected,
      final LocalTime actual) {
    final long differenceInSeconds = Math.abs(Duration.between(expected, actual).getSeconds());
    assertThat(
        location + " " + date + " " + label + " expected " + expected + " but was " + actual,
        differenceInSeconds,
        lessThanOrEqualTo(TOLERANCE_MINUTES * 60L));
  }

  private RiseSetData getRiseSetData(final Location location, final LocalDate date) {
    final Options options = new Options();
    final RiseSetYearData riseSetYear =
        RiseSetUtility.createRiseSetYear(location, date.getYear(), options);
    final List<RiseSetData> riseSetData = riseSetYear.getRiseSetData();
    for (final RiseSetData data : riseSetData) {
      if (date.equals(data.getDate())) {
        return data;
      }
    }
    return null;
  }
}
