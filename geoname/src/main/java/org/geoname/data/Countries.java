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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/** In-memory database of locations. */
public final class Countries {

  private static final Map<String, Country> iso3166_alpha2Map;
  private static final Map<String, Country> iso3166_alpha3Map;
  private static final Map<String, Country> countryNameMap;

  /** Loads data from internal database. */
  static {
    iso3166_alpha2Map = new HashMap<>();
    iso3166_alpha3Map = new HashMap<>();
    countryNameMap = new HashMap<>();

    loadISO3166_1();
  }

  /**
   * Gets a collection of all countries.
   *
   * @return All countries.
   */
  public static List<Country> getAllCountries() {
    final ArrayList<Country> countries = new ArrayList<>(iso3166_alpha2Map.values());
    Collections.sort(countries);
    return countries;
  }

  /**
   * Looks up a country from the provided string - whether a country code or a country name.
   *
   * @param countryString Country
   * @return Country ojbect, or null
   */
  public static Country lookupCountry(final String countryString) {
    if (countryString == null) {
      return null;
    }
    if (countryNameMap.containsKey(countryString)) {
      return countryNameMap.get(countryString);
    }
    if (iso3166_alpha2Map.containsKey(countryString)) {
      return iso3166_alpha2Map.get(countryString);
    }
    if (iso3166_alpha3Map.containsKey(countryString)) {
      return iso3166_alpha3Map.get(countryString);
    }
    return null;
  }

  /**
   * Looks up a country from the country name.
   *
   * @param countryName Country name
   * @return Country ojbect, or null
   */
  public static Country lookupCountryName(final String countryName) {
    return countryNameMap.get(countryName);
  }

  /**
   * Looks up a country from the two-letter ISO 3166 country code.
   *
   * @param iso3166CountryCode2 ISO 3166 country code
   * @return Country ojbect, or null
   */
  public static Country lookupIso3166CountryCode2(final String iso3166CountryCode2) {
    return iso3166_alpha2Map.get(iso3166CountryCode2);
  }

  /**
   * Looks up a country from the two-letter ISO 3166 country code.
   *
   * @param iso3166CountryCode3 ISO 3166 country code
   * @return Country ojbect, or null
   */
  public static Country lookupIso3166CountryCode3(final String iso3166CountryCode3) {
    return iso3166_alpha3Map.get(iso3166CountryCode3);
  }

  private static void loadISO3166_1() {
    final String dataResource = "iso3166-1.data";
    final CSVFormat format =
        CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(true)
            .get();
    try (CSVParser csvParser =
        format.parse(
            new InputStreamReader(
                Countries.class.getClassLoader().getResourceAsStream(dataResource), "UTF-8"))) {
      for (final CSVRecord record : csvParser) {
        final String name = record.get("name");
        final String alpha2 = record.get("alpha-2");
        final String alpha3 = record.get("alpha-3");
        if (alpha2 == null || alpha3 == null || name == null || name.isEmpty()) {
          continue;
        }
        final Country country = new Country(name, alpha2, alpha3);
        countryNameMap.put(country.name(), country);
        iso3166_alpha2Map.put(country.alpha2Code(), country);
        iso3166_alpha3Map.put(country.alpha3Code(), country);
      }
    } catch (final IOException e) {
      throw new IllegalStateException("Cannot read data from internal database", e);
    }
  }
}
