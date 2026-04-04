package daylightchart.web.config;

import daylightchart.web.service.LocationFileLoaderService;
import java.nio.file.Path;
import java.util.logging.Logger;
import org.geoname.data.LocationRegistry;
import org.geoname.parser.LocationsListParser;
import org.geoname.parser.ParserException;
import org.geoname.parser.resources.ResourceRefs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class LocationRegistryConfig {

  private static final Logger LOGGER = Logger.getLogger(LocationRegistryConfig.class.getName());

  @Value("${geoname.data-dir:}")
  private String dataDir;

  @Bean
  public LocationRegistry locationRegistry() throws ParserException {
    final var parser = new LocationsListParser(ResourceRefs.ofClasspath("locations.data"));
    return new LocationRegistry(parser.parseLocations());
  }

  @EventListener(ApplicationReadyEvent.class)
  public void preloadDataDirectory(final ApplicationReadyEvent event) {
    if (dataDir == null || dataDir.isBlank()) {
      return;
    }
    final Path dir = Path.of(dataDir);
    LOGGER.info("Pre-loading GNS/GNIS files from: " + dir);
    final LocationFileLoaderService loader =
        event.getApplicationContext().getBean(LocationFileLoaderService.class);
    loader.loadDirectory(dir);
  }
}
