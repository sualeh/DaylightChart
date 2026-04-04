package daylightchart.web.controller;

import daylightchart.chart.options.ChartOptions;
import daylightchart.chart.options.ChartOptionsService;
import daylightchart.chart.report.DaylightChartReport;
import daylightchart.chart.report.DaylightChartReportService;
import daylightchart.options.ChartOrientation;
import daylightchart.options.Options;
import daylightchart.options.TwilightType;
import daylightchart.web.dto.LocationDto;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoname.data.Countries;
import org.geoname.data.Location;
import org.geoname.data.LocationRegistry;
import org.jfree.chart.ChartUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import us.fatehi.pointlocation6709.Angle;
import us.fatehi.pointlocation6709.Latitude;
import us.fatehi.pointlocation6709.Longitude;
import us.fatehi.pointlocation6709.PointLocation;

@RestController
public class ChartRestController {

  private static final Logger LOGGER = Logger.getLogger(ChartRestController.class.getName());

  static final Location BOSTON =
      new Location(
          "Boston",
          Countries.lookupIso3166CountryCode2("US"),
          "America/New_York",
          new PointLocation(
              new Latitude(Angle.fromDegrees(42.36)), new Longitude(Angle.fromDegrees(-71.06))));

  private final LocationRegistry locationRegistry;
  private final ChartOptions defaultChartOptions =
      ChartOptionsService.chartOptions().createDefaultChartOptions();

  public ChartRestController(final LocationRegistry locationRegistry) {
    this.locationRegistry = locationRegistry;
  }

  private static final int MIN_WIDTH = 640;
  private static final int MAX_WIDTH = 1920;
  private static final int DEFAULT_WIDTH = 1024;

  @GetMapping(value = "/api/chart", produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<StreamingResponseBody> chart(
      @RequestParam(name = "locationKey", required = false) final String locationKey,
      @RequestParam(name = "width", required = false) final Integer requestedWidth,
      @RequestParam(name = "height", required = false) final Integer requestedHeight,
      @RequestParam(name = "twilightType", required = false) final String twilightTypeName,
      @RequestParam(name = "orientation", required = false) final String orientationName,
      @RequestParam(name = "showLegend", required = false) final Boolean showLegend)
      throws IOException {
    final Location location = resolveLocation(locationKey);
    final int width = clamp(requestedWidth, MIN_WIDTH, MAX_WIDTH, DEFAULT_WIDTH);
    final int height =
        requestedHeight != null
            ? clamp(requestedHeight, MIN_WIDTH * 9 / 16, MAX_WIDTH * 9 / 16, DEFAULT_WIDTH * 9 / 16)
            : Math.round(width * 9f / 16f);

    final Options options = new Options();
    if (twilightTypeName != null) {
      try {
        options.setTwilightType(TwilightType.valueOf(twilightTypeName.toUpperCase()));
      } catch (final IllegalArgumentException ignored) {
        /* keep default CIVIL */
      }
    }
    if (orientationName != null) {
      try {
        options.setChartOrientation(ChartOrientation.valueOf(orientationName.toUpperCase()));
      } catch (final IllegalArgumentException ignored) {
        /* keep default STANDARD */
      }
    }
    if (showLegend != null) {
      options.setShowChartLegend(showLegend);
    }

    final DaylightChartReport report =
        DaylightChartReportService.reports().createReport(location, options, defaultChartOptions);

    final StreamingResponseBody body =
        outputStream -> {
          try (final BufferedOutputStream bufferedOut = new BufferedOutputStream(outputStream)) {
            ChartUtils.writeChartAsPNG(bufferedOut, report.getChart(), width, height);
            bufferedOut.flush();
          } catch (final IOException e) {
            LOGGER.log(Level.WARNING, "Could not stream chart image");
          }
        };

    return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(body);
  }

  @GetMapping(value = "/api/locations/default", produces = MediaType.APPLICATION_JSON_VALUE)
  public LocationDto defaultLocation() {
    return LocationDto.from(BOSTON);
  }

  private Location resolveLocation(final String locationKey) {
    if (locationKey == null || locationKey.isBlank()) {
      return BOSTON;
    }
    return locationRegistry.getAllLocations().stream()
        .filter(loc -> loc.deduplicationKey().equals(locationKey))
        .findFirst()
        .orElse(BOSTON);
  }

  private static int clamp(final Integer value, final int min, final int max, final int fallback) {
    if (value == null) return fallback;
    return Math.max(min, Math.min(max, value));
  }
}
