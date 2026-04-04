/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.geoname.data.Subdivision;
import org.geoname.data.Subdivisions;
import org.junit.jupiter.api.Test;

public class TestSubdivisions {

  @Test
  public void afghanistandRegionSampleCount() {
    final String[] afCodes = {
      "BDS", "BGL", "BAL", "BDG", "BAM", "DAY", "FRA", "FYB", "GHA", "GHO", "HEL", "HER", "JOW",
      "KAN", "KHO", "KNR", "KDZ", "KAB", "KAP", "LAG"
    };
    long found =
        java.util.Arrays.stream(afCodes)
            .map(code -> Subdivisions.lookupSubdivision("AF-" + code))
            .filter(a -> a != null)
            .count();
    assertThat("Afghanistan region sample", (int) found, is(greaterThan(15)));
  }

  @Test
  public void blankCodeReturnsNull() {
    assertThat(Subdivisions.lookupSubdivision("  "), is(nullValue()));
  }

  @Test
  public void isoLookupByFullCode() {
    final Subdivision subdivision = Subdivisions.lookupSubdivision("AF-BDS");
    assertThat(subdivision, is(notNullValue()));
    assertThat(subdivision.name(), is("Badakhshān"));
    assertThat(subdivision.type(), is("Province"));
  }

  @Test
  public void isoLookupGbEntry() {
    final Subdivision subdivision = Subdivisions.lookupSubdivision("GB-ABE");
    assertThat(subdivision, is(notNullValue()));
    assertThat(subdivision.name(), is("Aberdeen City"));
  }

  @Test
  public void isoLookupUsState() {
    final Subdivision subdivision = Subdivisions.lookupSubdivision("US-AL");
    assertThat(subdivision, is(notNullValue()));
    assertThat(subdivision.name(), is("Alabama"));
    assertThat(subdivision.type(), is("state"));
  }

  @Test
  public void mapContainsBothSources() {
    long isoCount =
        java.util.stream.Stream.of("US-AL", "US-CA", "US-NY", "GB-ABE", "AF-BDS", "DE-BY", "FR-ARA")
            .map(Subdivisions::lookupSubdivision)
            .filter(a -> a != null)
            .count();
    assertThat("ISO entries loaded", (int) isoCount, is(greaterThan(5)));
  }

  @Test
  public void nullCodeReturnsNull() {
    assertThat(Subdivisions.lookupSubdivision(null), is(nullValue()));
  }

  @Test
  public void partialCodeWithoutCountryPrefixReturnsNull() {
    assertThat(Subdivisions.lookupSubdivision("BDS"), is(nullValue()));
  }

  @Test
  public void unknownCodeReturnsNull() {
    assertThat(Subdivisions.lookupSubdivision("ZZ-ZZZZ"), is(nullValue()));
  }
}
