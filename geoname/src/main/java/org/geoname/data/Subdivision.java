/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * An immutable country subdivision (state, province, region, etc.) with a lookup code, display
 * name, type description, and data source.
 *
 * @param code Full lookup code (e.g. {@code "US-AL"} or {@code "US01"})
 * @param name Display name (e.g. {@code "Alabama"})
 * @param type Division type (e.g. {@code "state"}, {@code "province"}); never {@code null}, may be
 *     empty
 * @param source Data source
 */
public record Subdivision(String code, String name, String type) implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  public Subdivision {
    code = Objects.requireNonNull(code, "Code must not be null");
    name = Objects.requireNonNull(name, "Name must not be null");
    type = type != null ? type : "";
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof final Subdivision other)) {
      return false;
    }
    return Objects.equals(code, other.code);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(code);
  }

  @Override
  public String toString() {
    return code + " (" + name + ")";
  }
}
