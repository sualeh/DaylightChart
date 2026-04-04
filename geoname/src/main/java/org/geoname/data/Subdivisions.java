/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Unified in-memory registry of subdivisions, loaded from both the FIPS 10 ({@code fips10.data})
 * and ISO 3166-2 ({@code iso_adm.data}) data files.
 *
 * <p>FIPS 10 entries use 4-character keys (e.g. {@code "US01"}). ISO 3166-2 entries use hyphenated
 * keys (e.g. {@code "US-AL"}). The key spaces are disjoint, so both sets can coexist in one map.
 */
public final class Subdivisions {

  private static final Map<String, Subdivision> subdivisionMap;
  private static final Map<String, Subdivision> subdivisionNameMap;

  /** Loads both data files into the in-memory map. */
  static {
    subdivisionMap = new HashMap<>();
    subdivisionNameMap = new HashMap<>();

    loadISO3166_2();
  }

  /**
   * Looks up a subdivision by its full code.
   *
   * @param code Full lookup code (e.g. {@code "US-AL"} or {@code "US01"}); {@code null} or blank
   *     returns {@code null}
   * @return {@link Subdivision}, or {@code null} if not found
   */
  public static Subdivision lookupSubdivision(final String code) {
    if (code == null || code.isBlank()) {
      return null;
    }
    return subdivisionMap.get(code.trim());
  }

  /**
   * Looks up a subdivision by its full name.
   *
   * @param name Subdivision name
   * @return {@link Subdivision}, or {@code null} if not found
   */
  public static Subdivision lookupSubdivisionName(final String name) {
    if (name == null || name.isBlank()) {
      return null;
    }
    return subdivisionMap.get(name.trim());
  }

  private static void loadISO3166_2() {
    final String dataResource = "iso3166-2.data";
    final CSVFormat format =
        CSVFormat.DEFAULT
            .builder()
            .setDelimiter(';')
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(true)
            .get();

    try (CSVParser csvParser =
        format.parse(
            new InputStreamReader(
                Subdivisions.class.getClassLoader().getResourceAsStream(dataResource), "UTF-8"))) {
      for (final CSVRecord record : csvParser) {
        final String countryCode = record.get("country_code").trim();
        final String regionName = record.get("region_name").trim();
        final String regionType = record.get("region_type").trim();
        final String regionalCode = record.get("regional_code").trim();
        if (!countryCode.isEmpty() && !regionalCode.isEmpty() && !regionName.isEmpty()) {
          final String key = countryCode + "-" + regionalCode;
          final Subdivision subdivision = new Subdivision(key, regionName, regionType);
          subdivisionMap.put(key, subdivision);
          subdivisionNameMap.put(subdivision.name(), subdivision);
        }
      }
    } catch (final IOException e) {
      throw new IllegalStateException("Cannot read ISO subdivision data from internal database", e);
    }
  }

  private Subdivisions() {}
}
