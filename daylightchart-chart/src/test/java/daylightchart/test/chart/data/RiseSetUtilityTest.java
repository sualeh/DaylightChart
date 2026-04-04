/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.test.chart.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

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
