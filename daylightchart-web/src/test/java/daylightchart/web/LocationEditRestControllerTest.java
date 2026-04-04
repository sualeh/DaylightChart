package daylightchart.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.geoname.data.LocationRegistry;
import org.geoname.parser.LocationsListParser;
import org.geoname.parser.resources.ResourceRefs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LocationEditRestControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private LocationRegistry locationRegistry;

  private int registrySizeAtStart;

  @BeforeEach
  void resetRegistry() throws Exception {
    final var parser = new LocationsListParser(ResourceRefs.ofClasspath("locations.data"));
    locationRegistry.replaceLocations(parser.parseLocations());
    registrySizeAtStart = locationRegistry.size();
  }

  // ── Add ──────────────────────────────────────────────────────────────────

  @Test
  void addLocationReturns201WithDto() throws Exception {
    final String body =
        """
        {"city":"TestCity","countryCode":"DE","timeZoneId":"Europe/Berlin","latitude":52.5,"longitude":13.4}
        """;

    mockMvc
        .perform(post("/api/locations").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.city").value("TestCity"))
        .andExpect(jsonPath("$.countryCode").value("DE"))
        .andExpect(jsonPath("$.locationKey").isString());

    assertThat(
        "Registry should have grown by one after add",
        locationRegistry.size(),
        is(equalTo(registrySizeAtStart + 1)));
  }

  @Test
  void addLocationWithInvalidCountryReturnsBadRequest() throws Exception {
    // "ZZZZZ" is 5 chars — won't match 2-char ISO3166, 3-char ISO3166-3, or FIPS10 lookups
    final String body =
        """
        {"city":"Ghost","countryCode":"ZZZZZ","timeZoneId":"UTC","latitude":0.0,"longitude":0.0}
        """;

    mockMvc
        .perform(post("/api/locations").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").isString());
  }

  @Test
  void addLocationMissingCityReturnsBadRequest() throws Exception {
    final String body =
        """
        {"city":"","countryCode":"US","timeZoneId":"America/New_York","latitude":40.0,"longitude":-74.0}
        """;

    mockMvc
        .perform(post("/api/locations").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").isString());
  }

  // ── Update ───────────────────────────────────────────────────────────────

  @Test
  void updateBostonRenamesItAndReturns200() throws Exception {
    final String body =
        """
        {"city":"BostonRenamed","countryCode":"US","timeZoneId":"America/New_York","latitude":42.32,"longitude":-71.04,"adminCode":"US-MA"}
        """;

    mockMvc
        .perform(
            put("/api/locations")
                .param("key", "Boston|US-MA|US")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.city").value("BostonRenamed"));

    assertThat(
        "Registry size should be unchanged after rename",
        locationRegistry.size(),
        is(equalTo(registrySizeAtStart)));
  }

  @Test
  void updateUnknownKeyReturns404() throws Exception {
    final String body =
        """
        {"city":"X","countryCode":"US","timeZoneId":"America/New_York","latitude":0.0,"longitude":0.0}
        """;

    mockMvc
        .perform(
            put("/api/locations")
                .param("key", "NoSuchPlace||XX")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNotFound());
  }

  // ── Delete ───────────────────────────────────────────────────────────────

  @Test
  void deleteBostonRemovesItFromRegistry() throws Exception {
    mockMvc
        .perform(delete("/api/locations").param("key", "Boston|US-MA|US"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.removed").value(true));

    assertThat(
        "Registry should have shrunk by one after delete",
        locationRegistry.size(),
        is(equalTo(registrySizeAtStart - 1)));
  }

  @Test
  void deleteUnknownKeyReturns404() throws Exception {
    mockMvc
        .perform(delete("/api/locations").param("key", "NoSuchPlace||XX"))
        .andExpect(status().isNotFound());
  }

  // ── Chart options params ─────────────────────────────────────────────────

  @Test
  void chartEndpointAcceptsTwilightType() throws Exception {
    mockMvc.perform(get("/api/chart").param("twilightType", "NAUTICAL")).andExpect(status().isOk());
  }

  @Test
  void chartEndpointAcceptsOrientation() throws Exception {
    mockMvc
        .perform(get("/api/chart").param("orientation", "CONVENTIONAL"))
        .andExpect(status().isOk());
  }

  @Test
  void chartEndpointAcceptsShowLegendFalse() throws Exception {
    mockMvc.perform(get("/api/chart").param("showLegend", "false")).andExpect(status().isOk());
  }

  @Test
  void chartEndpointIgnoresUnknownTwilightType() throws Exception {
    mockMvc
        .perform(get("/api/chart").param("twilightType", "UNKNOWN_VALUE"))
        .andExpect(status().isOk()); // falls back to default
  }

  // ── Full list still queryable after mutations ────────────────────────────

  @Test
  void locationsEndpointReflectsAddImmediately() throws Exception {
    final String addBody =
        """
        {"city":"ImmediateCity","countryCode":"FR","timeZoneId":"Europe/Paris","latitude":48.85,"longitude":2.35}
        """;
    mockMvc
        .perform(post("/api/locations").contentType(MediaType.APPLICATION_JSON).content(addBody))
        .andExpect(status().isCreated());

    final int newSize = locationRegistry.size();
    assertThat("Registry grew", newSize, is(greaterThan(registrySizeAtStart)));

    // GET /api/locations should return the enlarged list
    mockMvc
        .perform(get("/api/locations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(newSize));
  }
}
