/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.test.desktop;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import daylightchart.Version;
import org.junit.jupiter.api.Test;

class VersionTest {

  @Test
  void shouldReturnProductName() {
    assertThat(Version.getProductName(), is("Daylight Chart"));
  }

  @Test
  void shouldReturnVersion() {
    final String version = Version.getVersion();
    assertThat(version, is(notNullValue()));
    assertThat(version.isEmpty(), is(false));
  }

  @Test
  void shouldReturnAboutContainingProductNameAndVersion() {
    final String about = Version.about();
    assertThat(about, containsString(Version.getProductName()));
    assertThat(about, containsString(Version.getVersion()));
  }
}
