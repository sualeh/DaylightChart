package daylightchart.web.controller;

import daylightchart.web.dto.LocationDto;
import java.util.List;
import org.geoname.data.LocationRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationRestController {

  private final LocationRegistry locationRegistry;

  public LocationRestController(final LocationRegistry locationRegistry) {
    this.locationRegistry = locationRegistry;
  }

  @GetMapping(value = "/api/locations", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<LocationDto> allLocations(
      @RequestParam(name = "q", required = false) final String query) {
    final var all = locationRegistry.getAllLocations();
    if (query == null || query.isBlank()) {
      return all.stream().map(LocationDto::from).toList();
    }
    final String lower = query.strip().toLowerCase();
    return all.stream()
        .filter(loc -> loc.getDescription().toLowerCase().contains(lower))
        .map(LocationDto::from)
        .toList();
  }
}
