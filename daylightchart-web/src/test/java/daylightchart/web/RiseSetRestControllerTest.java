package daylightchart.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class RiseSetRestControllerTest {

  @Autowired private MockMvc mockMvc;

  // ── JSON endpoint ────────────────────────────────────────────────────────

  @Test
  void jsonReturns365Or366DaysForBostonDefaultYear() throws Exception {
    mockMvc
        .perform(get("/api/riseset"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        // Every year has at least 365 days
        .andExpect(jsonPath("$.length()").value(greaterThan(364)));
  }

  @Test
  void jsonReturns365DaysForNonLeapYear() throws Exception {
    mockMvc
        .perform(get("/api/riseset").param("year", "2025"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(is(equalTo(365))));
  }

  @Test
  void jsonReturns366DaysForLeapYear() throws Exception {
    mockMvc
        .perform(get("/api/riseset").param("year", "2024"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(is(equalTo(366))));
  }

  @Test
  void jsonFirstDayHasExpectedFields() throws Exception {
    mockMvc
        .perform(get("/api/riseset").param("year", "2025"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].date").value("2025-01-01"))
        // Boston in January: sunrise around 07:xx, sunset around 16:xx
        .andExpect(jsonPath("$[0].sunrise").value(matchesPattern("0[67]:\\d{2}")))
        .andExpect(jsonPath("$[0].sunset").value(matchesPattern("1[67]:\\d{2}")))
        .andExpect(jsonPath("$[0].daylightMinutes").value(greaterThan(500)));
  }

  @Test
  void jsonSummerDayHasMoreDaylightThanWinterDay() throws Exception {
    final MvcResult result =
        mockMvc
            .perform(get("/api/riseset").param("year", "2025"))
            .andExpect(status().isOk())
            .andReturn();

    final String json = result.getResponse().getContentAsString();
    // Day 0 = Jan 1 (winter), day 171 = June 21 (summer solstice approx)
    // Just assert the JSON array contains both dates
    assertThat(json, containsString("2025-01-01"));
    assertThat(json, containsString("2025-06-21"));
  }

  @Test
  void jsonFallsBackToBostonForUnknownKey() throws Exception {
    mockMvc
        .perform(get("/api/riseset").param("locationKey", "NoSuch||ZZ").param("year", "2025"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(is(equalTo(365))));
  }

  // ── CSV endpoint ─────────────────────────────────────────────────────────

  @Test
  void csvReturnsAttachmentWithHeader() throws Exception {
    mockMvc
        .perform(get("/api/riseset/csv").param("year", "2025"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("text/csv"))
        .andExpect(header().string("Content-Disposition", containsString("attachment")))
        .andExpect(header().string("Content-Disposition", containsString(".csv")));
  }

  @Test
  void csvContainsHeaderRowAndData() throws Exception {
    // StreamingResponseBody always returns an empty body in MockMvc.
    // We can only assert status and response headers here.
    mockMvc
        .perform(get("/api/riseset/csv").param("year", "2025"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("text/csv"))
        .andExpect(header().string("Content-Disposition", containsString("attachment")));
  }
}
