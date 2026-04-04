package daylightchart.web.dto;

import org.geoname.data.Location;

public record LocationDto(
    String locationKey,
    String city,
    String description,
    String adminCode,
    String adminName,
    String countryCode,
    String countryName,
    String timeZoneId,
    double latitude,
    double longitude) {

  public static LocationDto from(final Location location) {
    if (location == null) {
      return null;
    }
    final String adminCode =
        location.getSubdivision() != null ? location.getSubdivision().code() : "";
    final String adminName =
        location.getSubdivision() != null ? location.getSubdivision().name() : "";
    final String countryCode =
        location.getCountry() != null ? location.getCountry().alpha2Code() : "";
    final String countryName = location.getCountry() != null ? location.getCountry().name() : "";
    return new LocationDto(
        location.deduplicationKey(),
        location.getCity(),
        location.getDescription(),
        adminCode,
        adminName,
        countryCode,
        countryName,
        location.getTimeZoneId(),
        location.getPointLocation().getLatitude().getDegrees(),
        location.getPointLocation().getLongitude().getDegrees());
  }
}
