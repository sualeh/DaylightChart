/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.test.options;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import daylightchart.options.ChartOrientation;
import daylightchart.options.Options;
import daylightchart.options.TimeZoneOption;
import daylightchart.options.TwilightType;
import java.nio.file.Files;
import java.nio.file.Path;
import org.geoname.data.LocationsSortOrder;
import org.junit.jupiter.api.Test;

class OptionsTest {

  @Test
  void shouldProvideStableDefaultOptions() {
    final Options options = new Options();

    assertThat(options.getLocationsSortOrder(), is(LocationsSortOrder.BY_NAME));
    assertThat(options.getTimeZoneOption(), is(TimeZoneOption.USE_TIME_ZONE));
    assertThat(options.getChartOrientation(), is(ChartOrientation.STANDARD));
    assertThat(options.getTwilightType(), is(TwilightType.CIVIL));
    assertThat(options.isShowChartLegend(), is(true));
  }

  @Test
  void shouldOnlyAcceptExistingWorkingDirectories() throws Exception {
    final Options options = new Options();
    final Path workingDirectory = Files.createTempDirectory("daylight-core-options");

    options.setWorkingDirectory(workingDirectory);
    assertThat(options.getWorkingDirectory(), is(workingDirectory));

    options.setWorkingDirectory(workingDirectory.resolve("missing"));
    assertThat(options.getWorkingDirectory(), is(workingDirectory));
  }
}
