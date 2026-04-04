package daylightchart.web.dto;

/**
 * Request body for creating or updating a location.
 *
 * @param city City name (required)
 * @param adminCode ISO 3166-2 or FIPS-10 state/province code, e.g. {@code "US-MA"} (optional)
 * @param countryCode Two-letter ISO 3166 country code, e.g. {@code "US"} (required)
 * @param timeZoneId IANA timezone identifier, e.g. {@code "America/New_York"} (required)
 * @param latitude Decimal degrees, -90 to 90 (required)
 * @param longitude Decimal degrees, -180 to 180 (required)
 */
public record LocationInputDto(
    String city,
    String adminCode,
    String countryCode,
    String timeZoneId,
    double latitude,
    double longitude) {}
