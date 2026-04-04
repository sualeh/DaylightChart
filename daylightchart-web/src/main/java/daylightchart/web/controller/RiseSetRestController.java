package daylightchart.web.controller;

import daylightchart.chart.data.RiseSetData;
import daylightchart.chart.data.RiseSetUtility;
import daylightchart.chart.data.RiseSetYearData;
import daylightchart.options.Options;
import daylightchart.web.dto.RiseSetDayDto;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoname.data.Location;
import org.geoname.data.LocationRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * REST controller for per-day sunrise/sunset data.
 *
 * <ul>
 *   <li>{@code GET /api/riseset?locationKey=&year=} — JSON array of daily records
 *   <li>{@code GET /api/riseset/csv?locationKey=&year=} — same data as a CSV download
 * </ul>
 */
@RestController
public class RiseSetRestController {

  private static final Logger LOGGER = Logger.getLogger(RiseSetRestController.class.getName());

  private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

  /**
   * Sentinel nanosecond values that {@code RiseSet} uses for polar days (all-day / all-night). When
   * both sunrise and sunset carry these values we cannot distinguish the two cases from the public
   * API, so we default to 0 daylight minutes.
   */
  private static final long JUST_AFTER_MIDNIGHT_NANOS = 1L;

  private static final long JUST_BEFORE_MIDNIGHT_NANOS = 86_399_999_999_999L;

  private final LocationRegistry locationRegistry;

  public RiseSetRestController(final LocationRegistry locationRegistry) {
    this.locationRegistry = locationRegistry;
  }

  // ── JSON endpoint ────────────────────────────────────────────────────────

  /**
   * Returns daily sunrise/sunset data for the requested location and year.
   *
   * @param locationKey Deduplication key from {@link daylightchart.web.dto.LocationDto}; falls back
   *     to Boston when absent or unrecognised.
   * @param year Calendar year; defaults to the current year.
   * @return JSON array of {@link RiseSetDayDto}, one element per day of the year.
   */
  @GetMapping(value = "/api/riseset", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<RiseSetDayDto> riseSetJson(
      @RequestParam(name = "locationKey", required = false) final String locationKey,
      @RequestParam(name = "year", required = false) final Integer year) {

    final Location location = resolveLocation(locationKey);
    final int resolvedYear = year != null ? year : Year.now().getValue();
    return buildDtos(location, resolvedYear);
  }

  // ── CSV download endpoint ────────────────────────────────────────────────

  /**
   * Streams daily sunrise/sunset data as a CSV attachment.
   *
   * @param locationKey Deduplication key; falls back to Boston when absent or unrecognised.
   * @param year Calendar year; defaults to the current year.
   * @return Streaming CSV with a {@code Content-Disposition: attachment} header.
   */
  @GetMapping(value = "/api/riseset/csv")
  public ResponseEntity<StreamingResponseBody> riseSetCsv(
      @RequestParam(name = "locationKey", required = false) final String locationKey,
      @RequestParam(name = "year", required = false) final Integer year) {

    final Location location = resolveLocation(locationKey);
    final int resolvedYear = year != null ? year : Year.now().getValue();
    final List<RiseSetDayDto> rows = buildDtos(location, resolvedYear);

    final String filename =
        "daylight-%s-%d.csv".formatted(location.getCity().replaceAll("\\s+", "_"), resolvedYear);

    final StreamingResponseBody body =
        outputStream -> {
          try (final PrintWriter writer =
              new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {
            writer.println("date,sunrise,sunset,daylightMinutes");
            for (final RiseSetDayDto row : rows) {
              writer.printf(
                  "%s,%s,%s,%d%n",
                  row.date(),
                  row.sunrise() != null ? row.sunrise() : "",
                  row.sunset() != null ? row.sunset() : "",
                  row.daylightMinutes());
            }
          } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "Could not stream CSV", e);
          }
        };

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
        .body(body);
  }

  // ── Helpers ──────────────────────────────────────────────────────────────

  private Location resolveLocation(final String locationKey) {
    if (locationKey == null || locationKey.isBlank()) {
      return ChartRestController.BOSTON;
    }
    return locationRegistry.getAllLocations().stream()
        .filter(loc -> loc.deduplicationKey().equals(locationKey))
        .findFirst()
        .orElse(ChartRestController.BOSTON);
  }

  /**
   * Builds DTOs by computing a full year of rise/set data and converting each day.
   *
   * <p>Uses default {@link Options} (CIVIL twilight, local timezone), matching the chart defaults.
   */
  private List<RiseSetDayDto> buildDtos(final Location location, final int year) {
    final RiseSetYearData yearData =
        RiseSetUtility.createRiseSetYear(location, year, new Options());
    return yearData.getRiseSetData().stream().map(this::toDto).toList();
  }

  /**
   * Converts a single {@link RiseSetData} to a {@link RiseSetDayDto}.
   *
   * <p>Polar days use sentinel values for both sunrise and sunset ({@link
   * #JUST_AFTER_MIDNIGHT_NANOS} and {@link #JUST_BEFORE_MIDNIGHT_NANOS}). Since the public API
   * cannot distinguish all-day from all-night, both are mapped to {@code null} times with 0
   * daylight minutes.
   */
  private RiseSetDayDto toDto(final RiseSetData data) {
    final LocalDateTime sunrise = data.getSunrise();
    final LocalDateTime sunset = data.getSunset();

    final boolean isPolarDay =
        sunrise.toLocalTime().toNanoOfDay() == JUST_AFTER_MIDNIGHT_NANOS
            && sunset.toLocalTime().toNanoOfDay() == JUST_BEFORE_MIDNIGHT_NANOS;

    if (isPolarDay) {
      return new RiseSetDayDto(data.getDate().toString(), null, null, 0);
    }

    final int daylightMinutes = (int) Math.max(0, Duration.between(sunrise, sunset).toMinutes());

    return new RiseSetDayDto(
        data.getDate().toString(),
        sunrise.toLocalTime().format(HH_MM),
        sunset.toLocalTime().format(HH_MM),
        daylightMinutes);
  }
}
