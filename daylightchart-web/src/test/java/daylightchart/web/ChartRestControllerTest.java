package daylightchart.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ChartRestControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void chartEndpointReturnsPng() throws Exception {
    mockMvc
        .perform(get("/api/chart"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_PNG));
  }

  @Test
  void chartEndpointAcceptsLocationKey() throws Exception {
    mockMvc
        .perform(get("/api/chart").param("locationKey", "Boston||US"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_PNG));
  }

  @Test
  void chartEndpointAcceptsWidthAndHeight() throws Exception {
    mockMvc
        .perform(get("/api/chart").param("width", "1024").param("height", "576"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_PNG));
  }

  @Test
  void chartEndpointClampsOversizedDimensions() throws Exception {
    mockMvc
        .perform(get("/api/chart").param("width", "9999").param("height", "9999"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_PNG));
  }

  @Test
  void chartEndpointFallsBackToBostonForUnknownKey() throws Exception {
    mockMvc
        .perform(get("/api/chart").param("locationKey", "Unknown||XX"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_PNG));
  }

  @Test
  void defaultLocationReturnsBoston() throws Exception {
    mockMvc
        .perform(get("/api/locations/default"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.locationKey").value("Boston||US"))
        .andExpect(jsonPath("$.city").value("Boston"))
        .andExpect(jsonPath("$.description").value("Boston, United States of America"))
        .andExpect(jsonPath("$.countryCode").value("US"))
        .andExpect(jsonPath("$.timeZoneId").value("America/New_York"))
        .andExpect(jsonPath("$.latitude").value(42.36))
        .andExpect(jsonPath("$.longitude").value(-71.06));
  }

  @Test
  void defaultLocationHasCountryName() throws Exception {
    mockMvc
        .perform(get("/api/locations/default"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.countryName").value("United States of America"));
  }
}
