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
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.timezones.DefaultTimezones;
import org.junit.jupiter.api.Test;
import us.fatehi.pointlocation6709.Angle;
import us.fatehi.pointlocation6709.Longitude;

/**
 * Unit tests for {@link DefaultTimezones#attemptTimeZoneMatch}, covering each of the four inference
 * steps in order:
 *
 * <ol>
 *   <li>Single default timezone per country
 *   <li>City name match
 *   <li>Longitude proximity (multi-timezone country)
 *   <li>GMT fallback (unknown country / no defaults)
 * </ol>
 */
public class TestDefaultTimezones {

  // ── Step 1: single default timezone ─────────────────────────────────────

  /**
   * Slovakia has exactly one registered default timezone (Europe/Bratislava). The method should
   * return it immediately without consulting city-name lookup.
   */
  @Test
  public void singleDefaultTimezoneReturnedDirectly() {
    final Country slovakia = Countries.lookupIso3166CountryCode2("SK");
    // Longitude of Bratislava ≈ 17.1°E
    final Longitude bratislavaLon = new Longitude(Angle.fromDegrees(17.1));
    final String tz = DefaultTimezones.attemptTimeZoneMatch("Bratislava", slovakia, bratislavaLon);
    assertThat("Slovakia has one default timezone", tz, is("Europe/Bratislava"));
  }

  /**
   * Even a city name that does not match any JVM timezone entry should still yield the single
   * country default for a single-timezone country, because step 1 short-circuits before step 2.
   */
  @Test
  public void singleDefaultTimezoneIgnoresCityName() {
    final Country slovakia = Countries.lookupIso3166CountryCode2("SK");
    final Longitude lon = new Longitude(Angle.fromDegrees(17.1));
    // "XYZNoMatch" will never match a timezone ID
    final String tz = DefaultTimezones.attemptTimeZoneMatch("XYZNoMatch", slovakia, lon);
    assertThat(
        "Non-matching city in single-timezone country still returns country default",
        tz,
        is("Europe/Bratislava"));
  }

  // ── Step 2: city name match ──────────────────────────────────────────────

  /**
   * The US has multiple default timezones. "New_York" (normalised from "New York") matches the
   * "New_York" city segment of "America/New_York", so step 2 should return that timezone rather
   * than the longitude-nearest one.
   */
  @Test
  public void cityNameMatchOverridesLongitudeForMultiTzCountry() {
    final Country us = Countries.lookupIso3166CountryCode2("US");
    // New York is at roughly -74°W → nominal offset ≈ -5h, same as America/New_York.
    // The test value is deliberately chosen so longitude proximity would also give the right
    // answer here — the important thing is city match fires first (step 2 before step 3).
    final Longitude newYorkLon = new Longitude(Angle.fromDegrees(-74.0));
    final String tz = DefaultTimezones.attemptTimeZoneMatch("New York", us, newYorkLon);
    assertThat("City-name match should resolve to America/New_York", tz, is("America/New_York"));
  }

  // ── Step 3: longitude proximity ──────────────────────────────────────────

  /**
   * The US has multiple defaults. A city name with no matching timezone ID forces step 3. Honolulu
   * is at -158°W → nominal offset ≈ -10h → should resolve to Pacific/Honolulu.
   *
   * <p>Note: {@code roundToNearestFraction} uses {@link java.math.MathContext MathContext(1)} which
   * rounds to one significant digit. For longitude offsets whose absolute value is ≥ 7.5 (i.e.,
   * longitudes beyond ±112.5°), the function rounds to the nearest multiple of 5 rather than 0.5,
   * so -10.5 → -10 and -8.0 → -10. The important thing is that step 3 fires and returns a
   * registered US default timezone rather than a synthetic GMT string.
   */
  @Test
  public void longitudeProximityPicksClosestUSTimezone() {
    final Country us = Countries.lookupIso3166CountryCode2("US");
    // Use a deliberately unmatchable city name to bypass step 2.
    final Longitude honoluluLon = new Longitude(Angle.fromDegrees(-157.85));
    final String tz = DefaultTimezones.attemptTimeZoneMatch("ZZZUnknownCity", us, honoluluLon);
    assertThat(
        "Longitude proximity should pick Pacific/Honolulu for -158° longitude",
        tz,
        is("Pacific/Honolulu"));
  }

  // ── Step 4: GMT fallback ─────────────────────────────────────────────────

  /**
   * A country with no registered defaults (looked up via an invalid/unknown code) should yield a
   * synthetic GMT±HH:MM string derived from the longitude.
   */
  @Test
  public void noDefaultsProducesGmtFallback() {
    // lookupCountry returns an "Unknown" placeholder, not null, for unrecognised codes.
    // Use a long invalid string so it can never be a real ISO 3166 code.
    final Country unknown = Countries.lookupCountry("ZZZZUNKNOWN");
    final Longitude lon = new Longitude(Angle.fromDegrees(45.0)); // +45° → UTC+3
    final String tz = DefaultTimezones.attemptTimeZoneMatch("Nowhere", unknown, lon);
    assertThat("Unknown country should produce a GMT-based fallback", tz, startsWith("GMT"));
  }

  // ── Null guard ───────────────────────────────────────────────────────────

  /** A null longitude should return null without throwing an exception. */
  @Test
  public void nullLongitudeReturnsNull() {
    final Country us = Countries.lookupIso3166CountryCode2("US");
    final String tz = DefaultTimezones.attemptTimeZoneMatch("Boston", us, null);
    assertThat("Null longitude should return null", tz, is(nullValue()));
  }
}
