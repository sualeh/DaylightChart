/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.test.chart.report;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import daylightchart.chart.options.ChartOptionsService;
import daylightchart.chart.report.ChartFileType;
import daylightchart.chart.report.DaylightChartReport;
import daylightchart.options.Options;
import java.nio.file.Files;
import java.nio.file.Path;
import org.geoname.data.Location;
import org.geoname.parser.LocationsListParser;
import org.junit.jupiter.api.Test;

class DaylightChartReportTest {

  @Test
  void shouldCreateReportFileNameAndImage() throws Exception {
    final ChartOptionsService chartOptionsService = new ChartOptionsService();
    final Location location =
        LocationsListParser.parseLocation("Boston;US-MA;US;America/New_York;+4232-07104/");
    final DaylightChartReport report =
        new DaylightChartReport(
            location, new Options(), chartOptionsService.createDefaultChartOptions());
    final Path imageFile = Files.createTempFile("daylight-report", ".png");

    assertThat(report.getChart(), is(notNullValue()));
    assertThat(report.getReportFileName(ChartFileType.png), endsWith(".png"));

    report.write(imageFile, ChartFileType.png);

    assertThat(Files.exists(imageFile), is(true));
    assertThat(Files.size(imageFile), is(greaterThan(0L)));
  }
}
