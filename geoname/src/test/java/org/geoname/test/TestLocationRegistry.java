/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Collection;
import java.util.List;
import org.geoname.data.Countries;
import org.geoname.data.Location;
import org.geoname.data.LocationRegistry;
import org.geoname.parser.LocationsListParser;
import org.geoname.parser.ParserException;
import org.geoname.parser.resources.ResourceRefs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestLocationRegistry {

  private static final Collection<Location> BUNDLED_LOCATIONS;

  static {
    try {
      BUNDLED_LOCATIONS =
          new LocationsListParser(ResourceRefs.ofClasspath("locations.data")).parseLocations();
    } catch (final ParserException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private LocationRegistry registry;

  @BeforeEach
  public void setUp() {
    registry = new LocationRegistry();
  }

  @Test
  public void emptyRegistry() {
    assertThat(registry.size(), is(0));
    assertThat(registry.getAllLocations(), is(empty()));
  }

  @Test
  public void nullSafeAdd() {
    registry.addLocations(null);
    assertThat(registry.size(), is(0));

    registry.addLocations(List.of());
    assertThat(registry.size(), is(0));
  }

  @Test
  public void initialLocationsConstructor() {
    final LocationRegistry r = new LocationRegistry(BUNDLED_LOCATIONS);
    assertThat(r.size(), is(BUNDLED_LOCATIONS.size()));
  }

  @Test
  public void addLocations() {
    registry.addLocations(BUNDLED_LOCATIONS);
    assertThat(registry.size(), is(BUNDLED_LOCATIONS.size()));
  }

  @Test
  public void deduplication() {
    registry.addLocations(BUNDLED_LOCATIONS);
    final int sizeAfterFirst = registry.size();

    // Adding the same set again must not increase the count
    registry.addLocations(BUNDLED_LOCATIONS);
    assertThat(registry.size(), is(sizeAfterFirst));
  }

  @Test
  public void addMoreLocations() {
    final Location boston =
        new Location(
            "Boston",
            Countries.lookupCountry("US"),
            "America/New_York",
            new us.fatehi.pointlocation6709.PointLocation(
                new us.fatehi.pointlocation6709.Latitude(
                    us.fatehi.pointlocation6709.Angle.fromDegrees(42.36)),
                new us.fatehi.pointlocation6709.Longitude(
                    us.fatehi.pointlocation6709.Angle.fromDegrees(-71.06))));
    registry.addLocations(BUNDLED_LOCATIONS);
    final int before = registry.size();

    // Boston is not in the bundled list — adding it increases the count
    registry.addLocations(List.of(boston));
    assertThat(registry.size(), is(before + 1));
  }

  @Test
  public void getAllLocationsIsUnmodifiable() {
    registry.addLocations(BUNDLED_LOCATIONS);
    final Collection<Location> all = registry.getAllLocations();
    assertThat(all.size(), is(BUNDLED_LOCATIONS.size()));

    try {
      all.clear();
    } catch (final UnsupportedOperationException e) {
      // expected — returned collection must not be modifiable
    }
    assertThat(registry.size(), is(BUNDLED_LOCATIONS.size()));
  }

  @Test
  public void searchByCity() {
    registry.addLocations(BUNDLED_LOCATIONS);
    final Collection<Location> results = registry.search("Aberdeen");
    assertThat(results.size(), is(greaterThan(0)));
    results.forEach(
        loc -> assertThat(loc.getCity().toLowerCase().contains("aberdeen"), is(true)));
  }

  @Test
  public void searchByCountryName() {
    registry.addLocations(BUNDLED_LOCATIONS);
    // "United Kingdom" locations exist in the bundled list
    final Collection<Location> results = registry.search("United Kingdom");
    assertThat(results.size(), is(greaterThan(0)));
  }

  @Test
  public void searchByAdminAreaName() {
    registry.addLocations(BUNDLED_LOCATIONS);
    // Several US locations have state admin areas in the bundled list
    final Collection<Location> results = registry.search("New York");
    assertThat(results.size(), is(greaterThan(0)));
  }

  @Test
  public void searchCaseInsensitive() {
    registry.addLocations(BUNDLED_LOCATIONS);
    final Collection<Location> upper = registry.search("ABERDEEN");
    final Collection<Location> lower = registry.search("aberdeen");
    final Collection<Location> mixed = registry.search("Aberdeen");
    assertThat(upper.size(), is(lower.size()));
    assertThat(mixed.size(), is(lower.size()));
  }

  @Test
  public void searchNullReturnsAll() {
    registry.addLocations(BUNDLED_LOCATIONS);
    assertThat(registry.search(null), hasSize(registry.size()));
  }

  @Test
  public void searchBlankReturnsAll() {
    registry.addLocations(BUNDLED_LOCATIONS);
    assertThat(registry.search(""), hasSize(registry.size()));
    assertThat(registry.search("   "), hasSize(registry.size()));
  }

  @Test
  public void searchNoResults() {
    registry.addLocations(BUNDLED_LOCATIONS);
    assertThat(registry.search("ZZZNONEXISTENT_XYZ"), is(empty()));
  }

  @Test
  public void insertionOrderPreserved() {
    registry.addLocations(BUNDLED_LOCATIONS);
    final List<Location> all = new java.util.ArrayList<>(registry.getAllLocations());
    assertThat(all.size(), is(greaterThanOrEqualTo(2)));
    // Re-adding in reverse order must not change the stored order (first-one-wins)
    final List<Location> reversed = new java.util.ArrayList<>(all);
    java.util.Collections.reverse(reversed);
    registry.addLocations(reversed);
    final List<Location> afterReverseAdd = new java.util.ArrayList<>(registry.getAllLocations());
    assertThat(afterReverseAdd, is(all));
  }
}
