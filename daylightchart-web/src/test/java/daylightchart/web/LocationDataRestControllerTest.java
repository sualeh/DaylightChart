package daylightchart.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.geoname.data.LocationRegistry;
import org.geoname.parser.LocationsListParser;
import org.geoname.parser.resources.ResourceRefs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class LocationDataRestControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private LocationRegistry locationRegistry;
  @Autowired private ObjectMapper objectMapper;

  /** Reset the registry to the bundled locations before each test so tests are independent. */
  @BeforeEach
  void resetRegistry() throws Exception {
    final var parser = new LocationsListParser(ResourceRefs.ofClasspath("locations.data"));
    locationRegistry.replaceLocations(parser.parseLocations());
  }

  // -- GNIS upload ----------------------------------------------------------

  @Test
  void uploadGnisZipReplacesRegistryWithUsLocations() throws Exception {
    final int beforeSize = locationRegistry.size();
    final String filename = "HI.zip";
    final byte[] bytes = loadTestResource(filename);
    final MockMultipartFile file =
        new MockMultipartFile("file", filename, "application/zip", bytes);

    final MvcResult result =
        mockMvc
            .perform(multipart("/api/locations/upload/gnis").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.filename").value(filename))
            .andExpect(jsonPath("$.added").isNumber())
            .andReturn();

    final int added = readAdded(result);
    assertThat("GNIS upload should load at least one location", added, is(greaterThan(0)));
    // Registry now contains exactly the uploaded set (replaced, not merged)
    assertThat(
        "Registry size must equal the reported count", locationRegistry.size(), is(equalTo(added)));
    // Boston is in the bundled set but not in a Hawaii GNIS file - it must be gone
    assertThat(
        "Original bundled locations should be replaced, not merged",
        locationRegistry.size(),
        is(not(equalTo(beforeSize))));
    final boolean bostonGone =
        locationRegistry.getAllLocations().stream()
            .noneMatch(
                l -> "Boston".equals(l.getCity()) && "US".equals(l.getCountry().alpha2Code()));
    assertThat("Boston (from bundled list) should be absent after GNIS Hawaii upload", bostonGone);
  }

  @Test
  void uploadGnisEmptyFileReturnsError() throws Exception {
    final int sizeBeforeUpload = locationRegistry.size();
    final MockMultipartFile empty =
        new MockMultipartFile("file", "empty.zip", "application/zip", new byte[0]);

    mockMvc
        .perform(multipart("/api/locations/upload/gnis").file(empty))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.error").isString())
        .andExpect(jsonPath("$.added").value(0));

    assertThat(
        "Registry must not be modified on empty-file error",
        locationRegistry.size(),
        is(equalTo(sizeBeforeUpload)));
  }

  // -- GNS upload -----------------------------------------------------------

  @Test
  void uploadGnsZipReplacesRegistryWithCountryLocations() throws Exception {
    final int beforeSize = locationRegistry.size();
    final byte[] bytes = loadTestResource("Slovakia.zip");
    final MockMultipartFile file =
        new MockMultipartFile("file", "Slovakia.zip", "application/zip", bytes);

    final MvcResult result =
        mockMvc
            .perform(multipart("/api/locations/upload/gns").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.filename").value("Slovakia.zip"))
            .andExpect(jsonPath("$.added").isNumber())
            .andReturn();

    final int added = readAdded(result);
    assertThat("GNS upload should load at least one location", added, is(greaterThan(0)));
    assertThat(
        "Registry size must equal the reported count", locationRegistry.size(), is(equalTo(added)));
    assertThat(
        "Original bundled locations should be replaced, not merged",
        locationRegistry.size(),
        is(not(equalTo(beforeSize))));
  }

  @Test
  void uploadGnsEmptyFileReturnsError() throws Exception {
    final int sizeBeforeUpload = locationRegistry.size();
    final MockMultipartFile empty =
        new MockMultipartFile("file", "empty.zip", "application/zip", new byte[0]);

    mockMvc
        .perform(multipart("/api/locations/upload/gns").file(empty))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.error").isString())
        .andExpect(jsonPath("$.added").value(0));

    assertThat(
        "Registry must not be modified on empty-file error",
        locationRegistry.size(),
        is(equalTo(sizeBeforeUpload)));
  }

  // -- locations.data upload ------------------------------------------------

  @Disabled
  @Test
  void uploadLocationsDataFileReplacesRegistry() throws Exception {
    final URL url = getClass().getClassLoader().getResource("locations.data");
    assert url != null : "locations.data not found on classpath";
    final byte[] bytes = Files.readAllBytes(Path.of(url.toURI()));
    final MockMultipartFile file =
        new MockMultipartFile("file", "locations.data", "text/plain", bytes);

    final MvcResult result =
        mockMvc
            .perform(multipart("/api/locations/upload/data").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.filename").value("locations.data"))
            .andExpect(jsonPath("$.added").isNumber())
            .andReturn();

    final int added = readAdded(result);
    assertThat(
        "locations.data upload should load at least one location", added, is(greaterThan(0)));
    assertThat(
        "Registry size must match the reported count", locationRegistry.size(), is(equalTo(added)));
  }

  // -- Download -------------------------------------------------------------

  @Test
  void downloadReturnsLocationsDataFile() throws Exception {
    final MvcResult result =
        mockMvc
            .perform(get("/api/locations/download"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", containsString("locations.data")))
            .andReturn();

    final String body = result.getResponse().getContentAsString();
    assertThat("Download response body must not be empty", body.trim(), is(not("")));
    // The first line must be the header row so the file can be re-uploaded without modification
    assertThat(
        "Download must include CSV header row",
        body,
        containsString("city;admin_code;country_code;timezone;coordinates"));
    assertThat("Download body should contain semicolon-delimited rows", body, containsString(";"));
    // Number of data lines should match registry size
    final long dataLines =
        body.lines()
            .filter(l -> !l.isBlank() && !l.startsWith("#") && !l.startsWith("city"))
            .count();
    assertThat(
        "Each location in the registry should appear as a line in the download",
        (int) dataLines,
        is(equalTo(locationRegistry.size())));
  }

  // -- Round-trip: upload then download then re-upload ----------------------

  @Test
  void downloadedFileCanBeReUploaded() throws Exception {
    // Download the current (bundled) registry
    final MvcResult downloadResult =
        mockMvc.perform(get("/api/locations/download")).andExpect(status().isOk()).andReturn();
    final byte[] downloadedBytes = downloadResult.getResponse().getContentAsByteArray();
    final int originalSize = locationRegistry.size();

    // Replace with a different dataset so the re-upload is a meaningful change
    final String filename = "HI.zip";
    final byte[] gnisBytes = loadTestResource(filename);
    mockMvc
        .perform(
            multipart("/api/locations/upload/gnis")
                .file(new MockMultipartFile("file", filename, "application/zip", gnisBytes)))
        .andExpect(status().isOk());

    // Re-upload the originally downloaded file — registry should be restored
    final MockMultipartFile reUpload =
        new MockMultipartFile("file", "locations.data", "text/plain", downloadedBytes);
    final MvcResult reUploadResult =
        mockMvc
            .perform(multipart("/api/locations/upload/data").file(reUpload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.added").isNumber())
            .andReturn();

    final int restoredCount = readAdded(reUploadResult);
    assertThat(
        "Re-uploading a downloaded file should restore the original location count",
        restoredCount,
        is(equalTo(originalSize)));
  }

  // -- Helpers --------------------------------------------------------------

  private byte[] loadTestResource(final String name) throws Exception {
    final URL url = getClass().getClassLoader().getResource(name);
    assert url != null : name + " not found on test classpath";
    return Files.readAllBytes(Path.of(url.toURI()));
  }

  private int readAdded(final MvcResult result) throws Exception {
    final JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    return json.get("added").intValue();
  }
}
