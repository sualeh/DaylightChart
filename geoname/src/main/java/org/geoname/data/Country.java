/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.data;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

/** Country, with ISO 3166 country code, and FIPS 10 country code. */
public record Country(String name, String alpha2Code, String alpha3Code)
    implements Serializable, Comparable<Country> {

  /** Unknown country. */
  public static final Country UNKNOWN = new Country("", "", "");

  /**
   * Constructor.
   *
   * @param alpha2Code Two letter ISO 3166 country code
   * @param alpha3Code Three letter ISO 3166 country code
   * @param name Country name
   */
  public Country {
    name = requireNonNull(name);
    alpha2Code = requireNonNull(alpha2Code);
    alpha3Code = requireNonNull(alpha3Code);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final Country otherCountry) {
    return name.compareTo(otherCountry.name);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return name;
  }
}
