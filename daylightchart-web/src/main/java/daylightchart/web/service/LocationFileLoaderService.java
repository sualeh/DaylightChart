package daylightchart.web.service;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoname.data.Location;
import org.geoname.data.LocationRegistry;
import org.geoname.parser.GNISFileParser;
import org.geoname.parser.GNSCountryFileParser;
import org.geoname.parser.LocationsListParser;
import org.geoname.parser.ParserException;
import org.geoname.parser.resources.ResourceRefs;
import org.springframework.stereotype.Service;

/** Parses GNS country zip files and GNIS state zip files into the {@link LocationRegistry}. */
@Service
public class LocationFileLoaderService {

  private static final Logger LOGGER = Logger.getLogger(LocationFileLoaderService.class.getName());

  /** GNIS filenames match the pattern {@code <STATE>_Features_<DATE>.zip}. */
  private static boolean isGnisFile(final String filename) {
    return filename.toLowerCase().contains("_features_");
  }

  private final LocationRegistry locationRegistry;

  public LocationFileLoaderService(final LocationRegistry locationRegistry) {
    this.locationRegistry = locationRegistry;
  }

  /**
   * Loads all GNS/GNIS zip files found in the given directory into the registry.
   *
   * @param directory directory to scan; ignored if null or does not exist
   */
  public void loadDirectory(final Path directory) {
    if (directory == null || !Files.isDirectory(directory)) {
      return;
    }
    try (var stream = Files.list(directory)) {
      stream
          .filter(Files::isRegularFile)
          .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".zip"))
          .forEach(this::loadZip);
    } catch (final IOException e) {
      LOGGER.log(Level.WARNING, "Could not scan data directory: " + directory, e);
    }
  }

  /**
   * Replaces all current locations with those from a GNIS file (zip or plain text).
   *
   * @param filePath path to a GNIS zip or raw pipe-delimited text file
   * @return total number of locations now in the registry
   */
  public int replaceWithGnis(final Path filePath) throws IOException, ParserException {
    final Collection<Location> locations =
        isZip(filePath)
            ? parseGnis(filePath)
            : new GNISFileParser(ResourceRefs.ofFile(filePath)).parseLocations();
    locationRegistry.replaceLocations(locations);
    return locationRegistry.size();
  }

  /**
   * Replaces all current locations with those from a GNS file (zip or plain text).
   *
   * @param filePath path to a GNS zip or raw tab-delimited text file
   * @return total number of locations now in the registry
   */
  public int replaceWithGns(final Path filePath) throws IOException, ParserException {
    final Collection<Location> locations =
        isZip(filePath)
            ? parseGns(filePath)
            : new GNSCountryFileParser(ResourceRefs.ofFile(filePath)).parseLocations();
    locationRegistry.replaceLocations(locations);
    return locationRegistry.size();
  }

  /**
   * Replaces all current locations with those from a {@code locations.data} file.
   *
   * @param filePath path to a semicolon-delimited locations.data file
   * @return total number of locations now in the registry
   */
  public int replaceWithData(final Path filePath) throws ParserException {
    final Collection<Location> locations =
        new LocationsListParser(ResourceRefs.ofFile(filePath)).parseLocations();
    locationRegistry.replaceLocations(locations);
    return locationRegistry.size();
  }

  /**
   * Replaces all current locations with those parsed from the given GNS or GNIS zip file. The
   * registry is cleared atomically before the new locations are added.
   *
   * @param zipPath path to the zip file
   * @return number of locations loaded
   */
  public int replaceWithZip(final Path zipPath) {
    final String filename = zipPath.getFileName().toString();
    try {
      final Collection<Location> locations =
          isGnisFile(filename) ? parseGnis(zipPath) : parseGns(zipPath);
      locationRegistry.replaceLocations(locations);
      final int total = locationRegistry.size();
      LOGGER.log(
          Level.INFO,
          "Replaced registry with {0} locations from {1}",
          new Object[] {total, filename});
      return total;
    } catch (final IOException | ParserException e) {
      LOGGER.log(Level.WARNING, "Could not load zip file: " + filename, e);
      return 0;
    }
  }

  /**
   * Loads a single GNS or GNIS zip file into the registry.
   *
   * @param zipPath path to the zip file
   * @return number of new locations added
   */
  public int loadZip(final Path zipPath) {
    final String filename = zipPath.getFileName().toString();
    try {
      final Collection<Location> locations =
          isGnisFile(filename) ? parseGnis(zipPath) : parseGns(zipPath);
      final int before = locationRegistry.size();
      locationRegistry.addLocations(locations);
      final int added = locationRegistry.size() - before;
      LOGGER.log(Level.INFO, "Loaded {0} new locations from {1}", new Object[] {added, filename});
      return added;
    } catch (final IOException | ParserException e) {
      LOGGER.log(Level.WARNING, "Could not load zip file: " + filename, e);
      return 0;
    }
  }

  private Collection<Location> parseGns(final Path zipPath) throws IOException, ParserException {
    final String entry = firstEntry(zipPath);
    if (entry == null) {
      return java.util.List.of();
    }
    return new GNSCountryFileParser(ResourceRefs.ofZipEntry(zipPath, entry)).parseLocations();
  }

  private Collection<Location> parseGnis(final Path zipPath) throws IOException, ParserException {
    final String entry = firstEntry(zipPath);
    if (entry == null) {
      return java.util.List.of();
    }
    return new GNISFileParser(ResourceRefs.ofZipEntry(zipPath, entry)).parseLocations();
  }

  private static boolean isZip(final Path filePath) {
    return filePath.getFileName().toString().toLowerCase().endsWith(".zip");
  }

  private static String firstEntry(final Path zipPath) throws IOException {
    try (FileSystem fs = FileSystems.newFileSystem(zipPath, Map.of())) {
      return Files.walk(fs.getPath("/"))
          .filter(Files::isRegularFile)
          .findFirst()
          .map(Path::toString)
          .orElse(null);
    }
  }
}
