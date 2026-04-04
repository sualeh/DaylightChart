/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.data;

import java.io.Serial;
import java.util.Comparator;
import us.fatehi.pointlocation6709.Latitude;

/** Sort order for locations. */
public enum LocationsSortOrder implements Comparator<Location> {
  BY_NAME("Sort locations by name") {
    @Override
    public int compare(final Location location1, final Location location2) {
      return location1
          .getDescription()
          .toLowerCase()
          .compareTo(location2.getDescription().toLowerCase());
    }
  },
  BY_LATITUDE("Sort locations by latitude") {
    @Override
    public int compare(final Location location1, final Location location2) {
      final Latitude latitude1 = location1.getPointLocation().getLatitude();
      final Latitude latitude2 = location2.getPointLocation().getLatitude();
      return (int) Math.signum(latitude2.getRadians() - latitude1.getRadians());
    }
  };

  @Serial private static final long serialVersionUID = 4483200154586111166L;

  private final String description;

  LocationsSortOrder(final String description) {
    this.description = description;
  }

  /**
   * Description.
   *
   * @return Description
   */
  public String getDescription() {
    return description;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return description;
  }
}
