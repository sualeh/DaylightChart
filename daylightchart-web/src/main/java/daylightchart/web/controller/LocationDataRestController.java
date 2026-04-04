package daylightchart.web.controller;

import daylightchart.web.service.LocationFileLoaderService;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.geoname.data.LocationRegistry;
import org.geoname.parser.FormatterException;
import org.geoname.parser.LocationFormatter;
import org.geoname.parser.ParserException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles uploading and downloading location data files.
 *
 * <p>Upload endpoints accept either a zipped or plain-text file and replace the entire location
 * registry with the parsed contents. Three formats are supported:
 *
 * <ul>
 *   <li>GNIS (Geographic Names Information System) – US state files, pipe-delimited
 *   <li>GNS (Geographic Names Server) – country files, tab-delimited
 *   <li>locations.data – Daylight Chart's own semicolon-delimited format
 * </ul>
 *
 * <p>The download endpoint serialises the current registry back to the locations.data format.
 */
@RestController
public class LocationDataRestController {

  private final LocationFileLoaderService loaderService;
  private final LocationRegistry locationRegistry;

  public LocationDataRestController(
      final LocationFileLoaderService loaderService, final LocationRegistry locationRegistry) {
    this.loaderService = loaderService;
    this.locationRegistry = locationRegistry;
  }

  /** Upload a GNIS file (US states). Accepts {@code .zip} or plain pipe-delimited text. */
  @PostMapping(
      value = "/api/locations/upload/gnis",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Object> uploadGnis(@RequestParam(name = "file") final MultipartFile file) {
    return upload(file, "gnis");
  }

  /** Upload a GNS file (countries). Accepts {@code .zip} or plain tab-delimited text. */
  @PostMapping(
      value = "/api/locations/upload/gns",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Object> uploadGns(@RequestParam(name = "file") final MultipartFile file) {
    return upload(file, "gns");
  }

  /** Upload a {@code locations.data} file (Daylight Chart format). */
  @PostMapping(
      value = "/api/locations/upload/data",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Object> uploadData(@RequestParam(name = "file") final MultipartFile file) {
    return upload(file, "data");
  }

  /**
   * Download the current location registry as a {@code locations.data} file. The file can be
   * re-uploaded later to restore the same set of locations.
   */
  @GetMapping(value = "/api/locations/download", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> downloadLocationsData() {
    final var writer = new StringWriter();
    try {
      // LocationsListParser expects the first line to contain column names
      writer.write("city;admin_code;country_code;timezone;coordinates");
      writer.write(System.lineSeparator());
      // LocationFormatter writes the data rows (without a header)
      LocationFormatter.formatLocations(locationRegistry.getAllLocations(), writer);
    } catch (final FormatterException e) {
      throw new RuntimeException("Failed to format locations", e);
    }
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=\"locations.data\"")
        .contentType(MediaType.TEXT_PLAIN)
        .body(writer.toString());
  }

  // ── Shared upload helper ──────────────────────────────────────────────────

  private Map<String, Object> upload(final MultipartFile file, final String type) {
    if (file.isEmpty()) {
      return Map.of("error", "No file provided", "added", 0);
    }
    final String filename =
        file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload";
    Path tmp = null;
    try {
      tmp = Files.createTempFile("geoname-upload-", "-" + filename);
      file.transferTo(tmp);
      final int total =
          switch (type) {
            case "gnis" -> loaderService.replaceWithGnis(tmp);
            case "gns" -> loaderService.replaceWithGns(tmp);
            case "data" -> loaderService.replaceWithData(tmp);
            default -> throw new IllegalArgumentException("Unknown type: " + type);
          };
      return Map.of("filename", filename, "added", total);
    } catch (final IOException | ParserException e) {
      return Map.of(
          "error",
          e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName(),
          "added",
          0,
          "filename",
          filename);
    } finally {
      if (tmp != null) {
        try {
          Files.deleteIfExists(tmp);
        } catch (final IOException ignored) {
          // temp-file cleanup failure is non-critical
        }
      }
    }
  }
}
