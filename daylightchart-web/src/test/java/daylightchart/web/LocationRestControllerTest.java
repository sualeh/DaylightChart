package daylightchart.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import daylightchart.web.dto.LocationDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class LocationRestControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void allLocationsReturnsJsonArray() throws Exception {
    mockMvc
        .perform(get("/api/locations"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void allLocationsContainsBundledEntries() throws Exception {
    final MvcResult result =
        mockMvc.perform(get("/api/locations")).andExpect(status().isOk()).andReturn();

    final List<LocationDto> locations =
        objectMapper.readValue(
            result.getResponse().getContentAsString(), new TypeReference<>() {});

    assertThat("Location list must not be empty", locations, is(not(empty())));
    assertThat("Location list must contain bundled locations", locations.size(), is(greaterThan(0)));
  }

  @Test
  void allLocationsIncludesBoston() throws Exception {
    mockMvc
        .perform(get("/api/locations"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$[?(@.city == 'Boston' && @.countryCode == 'US')]").isNotEmpty());
  }

  @Test
  void allLocationsHaveRequiredFields() throws Exception {
    mockMvc
        .perform(get("/api/locations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].locationKey").isString())
        .andExpect(jsonPath("$[0].city").isString())
        .andExpect(jsonPath("$[0].description").isString())
        .andExpect(jsonPath("$[0].countryCode").isString())
        .andExpect(jsonPath("$[0].timeZoneId").isString())
        .andExpect(jsonPath("$[0].latitude").isNumber())
        .andExpect(jsonPath("$[0].longitude").isNumber());
  }

  @Test
  void locationKeysAreUnique() throws Exception {
    final MvcResult result =
        mockMvc.perform(get("/api/locations")).andExpect(status().isOk()).andReturn();

    final List<LocationDto> locations =
        objectMapper.readValue(
            result.getResponse().getContentAsString(), new TypeReference<>() {});

    final long distinctKeys = locations.stream().map(LocationDto::locationKey).distinct().count();
    assertThat("All location keys must be unique", distinctKeys, is((long) locations.size()));
  }

  @Test
  void queryFilterReturnsMatchingLocations() throws Exception {
    mockMvc
        .perform(get("/api/locations").param("q", "Boston"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.city == 'Boston')]").isNotEmpty());
  }

  @Test
  void queryFilterIsCaseInsensitive() throws Exception {
    final MvcResult lower =
        mockMvc.perform(get("/api/locations").param("q", "boston")).andReturn();
    final MvcResult upper =
        mockMvc.perform(get("/api/locations").param("q", "BOSTON")).andReturn();
    assertThat(
        "Case-insensitive search must return same results",
        lower.getResponse().getContentAsString(),
        is(upper.getResponse().getContentAsString()));
  }

  @Test
  void queryFilterNoMatchReturnsEmptyArray() throws Exception {
    mockMvc
        .perform(get("/api/locations").param("q", "xyzzy_no_match_9999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void emptyQueryReturnsAllLocations() throws Exception {
    final MvcResult withoutQ =
        mockMvc.perform(get("/api/locations")).andReturn();
    final MvcResult withBlankQ =
        mockMvc.perform(get("/api/locations").param("q", "")).andReturn();
    assertThat(
        "Blank q param must return the same result as no q param",
        withBlankQ.getResponse().getContentAsString(),
        is(withoutQ.getResponse().getContentAsString()));
  }
}
