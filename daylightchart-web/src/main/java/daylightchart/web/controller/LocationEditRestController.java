package daylightchart.web.controller;

import daylightchart.web.dto.LocationDto;
import daylightchart.web.dto.LocationInputDto;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.Location;
import org.geoname.data.LocationRegistry;
import org.geoname.data.Subdivision;
import org.geoname.data.Subdivisions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import us.fatehi.pointlocation6709.Angle;
import us.fatehi.pointlocation6709.Latitude;
import us.fatehi.pointlocation6709.Longitude;
import us.fatehi.pointlocation6709.PointLocation;

/**
 * Manages the location registry at runtime: add, update, and delete individual locations.
 *
 * <p>All three mutating endpoints replace a location in the shared {@link LocationRegistry}. The
 * change takes effect immediately — subsequent calls to {@code GET /api/locations} and {@code GET
 * /api/chart} reflect the updated registry.
 */
@RestController
public class LocationEditRestController {

  private final LocationRegistry locationRegistry;

  public LocationEditRestController(final LocationRegistry locationRegistry) {
    this.locationRegistry = locationRegistry;
  }

  /** Add a new location. Returns 201 Created with the saved {@link LocationDto}. */
  @PostMapping(
      value = "/api/locations",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> addLocation(@RequestBody final LocationInputDto input) {
    try {
      final Location location = buildLocation(input);
      locationRegistry.addLocations(List.of(location));
      return ResponseEntity.status(HttpStatus.CREATED).body(LocationDto.from(location));
    } catch (final IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /**
   * Replace an existing location identified by {@code key}. Returns 200 OK with the updated {@link
   * LocationDto}, or 404 if the key is not found.
   */
  @PutMapping(
      value = "/api/locations",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateLocation(
      @RequestParam(name = "key") final String key, @RequestBody final LocationInputDto input) {
    if (!locationRegistry.findByKey(key).isPresent()) {
      return ResponseEntity.notFound().build();
    }
    try {
      final Location newLocation = buildLocation(input);
      locationRegistry.removeLocation(key);
      locationRegistry.addLocations(List.of(newLocation));
      return ResponseEntity.ok(LocationDto.from(newLocation));
    } catch (final IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /** Delete the location identified by {@code key}. Returns 200 OK on success, 404 if not found. */
  @DeleteMapping(value = "/api/locations", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> deleteLocation(@RequestParam(name = "key") final String key) {
    if (locationRegistry.removeLocation(key)) {
      return ResponseEntity.ok(Map.of("removed", true, "key", key));
    }
    return ResponseEntity.notFound().build();
  }

  // ── Shared helpers ────────────────────────────────────────────────────────

  private static Location buildLocation(final LocationInputDto input) {
    if (input.city() == null || input.city().isBlank()) {
      throw new IllegalArgumentException("City name is required");
    }
    final Country country = Countries.lookupCountry(input.countryCode());
    if (country == null) {
      throw new IllegalArgumentException("Unknown country code: " + input.countryCode());
    }
    if (input.timeZoneId() == null
        || input.timeZoneId().isBlank()
        || !ZoneId.getAvailableZoneIds().contains(input.timeZoneId())) {
      throw new IllegalArgumentException("Invalid timezone ID: " + input.timeZoneId());
    }
    final Subdivision subdivision =
        (input.adminCode() != null && !input.adminCode().isBlank())
            ? Subdivisions.lookupSubdivision(input.adminCode())
            : null;
    final PointLocation point =
        new PointLocation(
            new Latitude(Angle.fromDegrees(input.latitude())),
            new Longitude(Angle.fromDegrees(input.longitude())));
    return new Location(input.city(), subdivision, country, input.timeZoneId(), point);
  }
}
