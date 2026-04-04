/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Thread-safe in-memory registry of locations. Locations are stored in insertion order and
 * deduplicated by {@link Location#deduplicationKey()}. Intended to be used as a single
 * application-scoped source of truth for all known locations; the web layer can wrap this as a
 * singleton bean and add locations at startup (from the bundled list or from GNS/GNIS zip files) as
 * well as during runtime via user uploads.
 */
public final class LocationRegistry {

  private final Map<String, Location> registry = new LinkedHashMap<>();

  /** Creates an empty registry. */
  public LocationRegistry() {}

  /**
   * Creates a registry pre-populated with the given locations.
   *
   * @param initialLocations initial locations; {@code null} and duplicate elements are silently
   *     skipped
   */
  public LocationRegistry(final Collection<Location> initialLocations) {
    addLocations(initialLocations);
  }

  /**
   * Atomically replaces all current locations with the given collection. The registry is cleared
   * first, then the new locations are added in iteration order. Duplicates within the new
   * collection are handled first-one-wins; {@code null} elements are skipped.
   *
   * @param locations new locations; may be {@code null} (treated as empty — results in empty
   *     registry)
   */
  public synchronized void replaceLocations(final Collection<Location> locations) {
    registry.clear();
    addLocations(locations);
  }

  /**
   * Adds locations to the registry. Locations whose {@link Location#deduplicationKey()} already
   * exists are silently skipped (first-one-wins). {@code null} elements are also skipped.
   *
   * @param locations locations to add; may be {@code null} (treated as empty)
   */
  public synchronized void addLocations(final Collection<Location> locations) {
    if (locations == null) {
      return;
    }
    for (final Location location : locations) {
      if (location != null) {
        registry.putIfAbsent(location.deduplicationKey(), location);
      }
    }
  }

  /**
   * Returns the location with the given deduplication key.
   *
   * @param key deduplication key (see {@link Location#deduplicationKey()})
   * @return the location, or empty if not found
   */
  public synchronized java.util.Optional<Location> findByKey(final String key) {
    return java.util.Optional.ofNullable(registry.get(key));
  }

  /**
   * Removes the location with the given deduplication key.
   *
   * @param key deduplication key (see {@link Location#deduplicationKey()})
   * @return {@code true} if a location was removed; {@code false} if not found
   */
  public synchronized boolean removeLocation(final String key) {
    return registry.remove(key) != null;
  }

  /**
   * Returns all locations in insertion order as an unmodifiable collection.
   *
   * @return all locations; never {@code null}
   */
  public synchronized Collection<Location> getAllLocations() {
    final List<Location> sorted = new ArrayList<>(registry.values());
    Collections.sort(sorted);
    return Collections.unmodifiableList(sorted);
  }

  /**
   * Searches locations by a query string. Matching is case-insensitive substring search across city
   * name, country name, and administrative area name. A {@code null} or blank query returns all
   * locations.
   *
   * @param query search query; {@code null} or blank returns all locations
   * @return matching locations in insertion order; never {@code null}
   */
  public synchronized Collection<Location> search(final String query) {
    if (query == null || query.isBlank()) {
      return getAllLocations();
    }
    final String lowerQuery = query.strip().toLowerCase();
    final List<Location> results = new ArrayList<>();
    for (final Location location : registry.values()) {
      if (matches(location, lowerQuery)) {
        results.add(location);
      }
    }
    Collections.sort(results);
    return Collections.unmodifiableList(results);
  }

  /**
   * Returns the number of locations in the registry.
   *
   * @return location count
   */
  public synchronized int size() {
    return registry.size();
  }

  private boolean matches(final Location location, final String lowerQuery) {
    if (location.getCity().toLowerCase().contains(lowerQuery)
        || (location.getCountry() != null
            && location.getCountry().name().toLowerCase().contains(lowerQuery))) {
      return true;
    }
    if (location.getSubdivision() != null
        && location.getSubdivision().name().toLowerCase().contains(lowerQuery)) {
      return true;
    }
    return false;
  }
}
