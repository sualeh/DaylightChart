/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.geoname.data.Location;
import org.geoname.parser.FormatterException;
import org.geoname.parser.GNISFileParser;
import org.geoname.parser.LocationFormatter;
import org.geoname.parser.LocationsListParser;
import org.geoname.parser.ParserException;
import org.geoname.parser.resources.ResourceRef;
import org.geoname.parser.resources.ResourceRefs;
import org.junit.jupiter.api.Test;

public class TestLocation {

  private static final class CloseTrackingInputStream extends ByteArrayInputStream {

    private boolean closed;

    private CloseTrackingInputStream(final byte[] buffer) {
      super(buffer);
    }

    @Override
    public void close() {
      closed = true;
      try {
        super.close();
      } catch (final java.io.IOException e) {
        throw new IllegalStateException("Unexpected close failure", e);
      }
    }

    private boolean isClosed() {
      return closed;
    }
  }

  @Test
  public void delimitedParserClosesInputStreamAfterParsing() throws ParserException {
    final CloseTrackingInputStream dataStream = new CloseTrackingInputStream(new byte[0]);
    final ResourceRef ref =
        new ResourceRef() {
          @Override
          public String location() {
            return "test:empty";
          }

          @Override
          public boolean exists() {
            return true;
          }

          @Override
          public InputStream openStream() {
            return dataStream;
          }
        };

    // Commons CSV handles empty input gracefully (0 records, no exception)
    new GNISFileParser(ref).parseLocations();
    assertThat(dataStream.isClosed(), is(true));
  }

  @Test
  public void location() throws ParserException, FormatterException {

    final String locationString = "Aberdeen;;GB;Europe/London;+5710-00204/";
    final Location location = LocationsListParser.parseLocation(locationString);

    assertThat(LocationFormatter.formatLocation(location), is(locationString));
  }

  @Test
  public void locations() throws ParserException {
    final ResourceRef ref = ResourceRefs.ofClasspath("locations.data");
    final Collection<Location> locations = new LocationsListParser(ref).parseLocations();

    assertThat(locations.size(), is(109));
  }

  @Test
  public void locationsParserClosesInputStream() throws ParserException {
    final CloseTrackingInputStream dataStream =
        new CloseTrackingInputStream(
            "# comment%ncity;admin_code;country_code;timezone;coordinates%nAberdeen;;GB;Europe/London;+5710-00204/%n"
                .formatted()
                .getBytes(StandardCharsets.UTF_8));
    final ResourceRef ref =
        new ResourceRef() {
          @Override
          public String location() {
            return "test:data";
          }

          @Override
          public boolean exists() {
            return true;
          }

          @Override
          public InputStream openStream() {
            return dataStream;
          }
        };

    final Collection<Location> locations = new LocationsListParser(ref).parseLocations();

    assertThat(locations.size(), is(1));
    assertThat(dataStream.isClosed(), is(true));
  }
}
