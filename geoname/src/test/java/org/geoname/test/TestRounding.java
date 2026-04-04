/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

import org.geoname.parser.ParserException;
import org.geoname.timezones.DefaultTimezones;
import org.junit.jupiter.api.Test;

public class TestRounding {

  @Test
  public void rounding() throws ParserException {
    assertThat(DefaultTimezones.roundToNearestFraction(1.9, 0.5), closeTo(2.0, 1e-10));
    assertThat(DefaultTimezones.roundToNearestFraction(2.0, 0.5), closeTo(2.0, 1e-10));
    assertThat(DefaultTimezones.roundToNearestFraction(2.1, 0.5), closeTo(2.0, 1e-10));

    assertThat(DefaultTimezones.roundToNearestFraction(2.4, 0.5), closeTo(2.5, 1e-10));
    assertThat(DefaultTimezones.roundToNearestFraction(2.5, 0.5), closeTo(2.5, 1e-10));
    assertThat(DefaultTimezones.roundToNearestFraction(2.6, 0.5), closeTo(2.5, 1e-10));

    // assertEquals(0.0, DefaultTimezones.roundToNearestFraction(-0.1, 0.5),
    // 1e-10);
    assertThat(DefaultTimezones.roundToNearestFraction(0, 0.5), closeTo(0.0, 1e-10));
    // assertEquals(0.0, DefaultTimezones.roundToNearestFraction(0.1, 0.5),
    // 1e-10);

    assertThat(DefaultTimezones.roundToNearestFraction(-1.9, 0.5), closeTo(-2.0, 1e-10));
    assertThat(DefaultTimezones.roundToNearestFraction(-2.0, 0.5), closeTo(-2.0, 1e-10));
    assertThat(DefaultTimezones.roundToNearestFraction(-2.1, 0.5), closeTo(-2.0, 1e-10));

    assertThat(DefaultTimezones.roundToNearestFraction(-2.4, 0.5), closeTo(-2.5, 1e-10));
    assertThat(DefaultTimezones.roundToNearestFraction(-2.5, 0.5), closeTo(-2.5, 1e-10));
    assertThat(DefaultTimezones.roundToNearestFraction(-2.6, 0.5), closeTo(-2.5, 1e-10));
  }
}
