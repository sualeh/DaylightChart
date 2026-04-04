/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoname.timezones.DefaultTimezones;
import us.fatehi.pointlocation6709.Angle;
import us.fatehi.pointlocation6709.Latitude;
import us.fatehi.pointlocation6709.Longitude;
import us.fatehi.pointlocation6709.PointLocation;
import us.fatehi.pointlocation6709.format.FormatterException;
import us.fatehi.pointlocation6709.format.PointLocationFormatType;
import us.fatehi.pointlocation6709.format.PointLocationFormatter;

/**
 * A location object has all the information required to define a location, such as the name of the
 * city and the country, the point location, and the time zone.
 */
public final class Location implements Serializable, Comparable<Location> {

  @Serial private static final long serialVersionUID = 7929385835483597187L;

  private static final Logger LOGGER = Logger.getLogger(Location.class.getName());

  /** UNKNOWN, setting all fields to blank. */
  public static final Location UNKNOWN =
      new Location(
          "",
          Country.UNKNOWN,
          ZoneId.systemDefault().getId(),
          new PointLocation(
              new Latitude(Angle.fromDegrees(0)), new Longitude(Angle.fromDegrees(0))));

  private final String city;
  private final Subdivision subdivision;
  private final Country country;
  private final PointLocation pointLocation;

  private final String timeZoneId;
  private transient String description;

  private transient String details;

  /**
   * Copy constructor. Copies the value of a provided location.
   *
   * @param location Location to copy the value from.
   */
  public Location(final Location location) {
    this(
        location.city,
        location.subdivision,
        location.country,
        location.timeZoneId,
        location.pointLocation);
  }

  /**
   * Constructor.
   *
   * @param city City
   * @param country Country
   * @param pointLocation Point location
   * @param timeZoneId Timezone
   */
  public Location(
      final String city,
      final Country country,
      final String timeZoneId,
      final PointLocation pointLocation) {
    this(city, null, country, timeZoneId, pointLocation);
  }

  /**
   * Constructor.
   *
   * @param city City
   * @param subdivision Subdivision (optional, may be null)
   * @param country Country
   * @param timeZoneId Timezone
   * @param pointLocation Point location
   */
  public Location(
      final String city,
      final Subdivision subdivision,
      final Country country,
      final String timeZoneId,
      final PointLocation pointLocation) {

    if (city == null) {
      throw new IllegalArgumentException("City needs to be specified");
    }
    this.city = city.trim();

    this.subdivision = subdivision;

    if (country == null) {
      throw new IllegalArgumentException("Country needs to be specified");
    }
    this.country = country;

    if (timeZoneId == null) {
      throw new IllegalArgumentException("Time zone needs to be specified");
    }
    this.timeZoneId = timeZoneId;

    if (pointLocation == null) {
      throw new IllegalArgumentException("Both latitude and longitude need to be specified");
    }
    this.pointLocation = pointLocation;

    // Set transient fields
    setTransientFields();

    // Now that all fields are set, validate
    validateTimeZone();
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final Location location) {
    return toString().compareTo(location.toString());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || !(obj instanceof final Location other)) {
      return false;
    }
    if (city == null) {
      if (other.city != null) {
        return false;
      }
    } else if (!city.equals(other.city)) {
      return false;
    }
    if (subdivision == null) {
      if (other.subdivision != null) {
        return false;
      }
    } else if (!subdivision.equals(other.subdivision)) {
      return false;
    }
    if (country == null) {
      if (other.country != null) {
        return false;
      }
    } else if (!country.equals(other.country)) {
      return false;
    }
    if (pointLocation == null) {
      if (other.pointLocation != null) {
        return false;
      }
    } else if (!pointLocation.equals(other.pointLocation)) {
      return false;
    }
    if (timeZoneId == null) {
      if (other.timeZoneId != null) {
        return false;
      }
    } else if (!timeZoneId.equals(other.timeZoneId)) {
      return false;
    }
    return true;
  }

  /**
   * Returns a composite key suitable for deduplication. The key is {@code
   * city|adminCode|countryCode}, where {@code adminCode} is the administrative area code or an
   * empty string when there is none.
   *
   * @return a stable string key
   */
  public String deduplicationKey() {
    final String admCode = subdivision != null ? subdivision.code() : "";
    return city + "|" + admCode + "|" + country.alpha2Code();
  }

  /**
   * Subdivision (state, province, region, etc.).
   *
   * @return Subdivision, or {@code null} if not available.
   */
  public Subdivision getSubdivision() {
    return subdivision;
  }

  /**
   * City.
   *
   * @return City.
   */
  public String getCity() {
    return city;
  }

  /**
   * Country.
   *
   * @return Country.
   */
  public Country getCountry() {
    return country;
  }

  /**
   * Gets the description - city, country.
   *
   * @return Description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Details for this location.
   *
   * @return Details for this location.
   */
  public String getDetails() {
    return details;
  }

  /**
   * Point location.
   *
   * @return Point location
   */
  public PointLocation getPointLocation() {
    return pointLocation;
  }

  /**
   * Timezone id.
   *
   * @return Timezone id.
   */
  public String getTimeZoneId() {
    return timeZoneId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(city, subdivision, country, pointLocation, timeZoneId);
  }

  /**
   * Description for the location. The description is the name of the city and country, separated by
   * a comma.
   *
   * @return Description for this location.
   * @see #getCountry
   * @see #getCity
   */
  @Override
  public String toString() {
    return description;
  }

  private void readObject(final ObjectInputStream objectInputStream)
      throws ClassNotFoundException, IOException {
    // Perform the default deserialization first
    objectInputStream.defaultReadObject();

    // Set transient fields
    setTransientFields();

    // Now that all fields are set, validate
    validateTimeZone();
  }

  private void setTransientFields() {
    final StringBuilder descriptionBuilder = new StringBuilder();
    if (city.length() > 0) {
      descriptionBuilder.append(city);
    }
    if (subdivision != null) {
      final String adminName = subdivision.name();
      if (adminName != null && !adminName.isEmpty()) {
        if (descriptionBuilder.length() > 0) {
          descriptionBuilder.append(", ");
        }
        descriptionBuilder.append(adminName);
      }
    }
    if (country != Country.UNKNOWN) {
      if (descriptionBuilder.length() > 0) {
        descriptionBuilder.append(", ");
      }
      descriptionBuilder.append(country);
    }
    description = descriptionBuilder.toString();

    final ZoneId zoneId = ZoneId.of(timeZoneId);
    try {
      details =
          PointLocationFormatter.formatPointLocation(
                  getPointLocation(), PointLocationFormatType.HUMAN_MEDIUM)
              + ", "
              + zoneId.getDisplayName(TextStyle.FULL, Locale.getDefault());
    } catch (final FormatterException e) {
      LOGGER.log(Level.FINE, e.getMessage(), e);
      details = "";
    }
  }

  private void validateTimeZone() {
    final Longitude longitude = getPointLocation().getLongitude();

    final double tzOffsetHours = DefaultTimezones.getStandardTimeZoneOffsetHours(timeZoneId);
    final double longitiudeTzOffsetHours = longitude.getDegrees() / 15D;
    final double hoursDifference = Math.abs(longitiudeTzOffsetHours - tzOffsetHours);
    // The tolerance band is a half hour on each side of the time zone,
    // plus about 10 minutes
    final double toleranceBand = 0.5 + 0.17;
    if ((hoursDifference > toleranceBand)) {
      LOGGER.log(
          Level.FINE,
          toString()
              + ": Longitude ("
              + longitude
              + ") and timezone ("
              + timeZoneId
              + ") do not match (difference "
              + hoursDifference
              + " hours)");
    }
  }
}
