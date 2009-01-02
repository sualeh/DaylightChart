package daylightchart.options;


import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import org.geoname.data.Location;

import daylightchart.gui.actions.LocationFileType;

/**
 * Represents a location file, with data.
 * 
 * @author sfatehi
 */
public final class LocationsDataFile
  extends BaseLocationsDataFile
{

  private static final Logger LOGGER = Logger.getLogger(LocationsDataFile.class
    .getName());

  /**
   * Constructor.
   * 
   * @param locationDataFile
   *        File
   */
  public LocationsDataFile(final BaseTypedFile<LocationFileType> locationDataFile)
  {
    this(locationDataFile.getFile(), locationDataFile.getFileType());
  }

  /**
   * Constructor.
   * 
   * @param file
   *        File
   * @param fileType
   *        Location file type
   */
  public LocationsDataFile(final File file, final LocationFileType fileType)
  {
    super(file, fileType);

    // Validation
    if (file == null)
    {
      throw new IllegalArgumentException("No file provided");
    }
    if (fileType == null)
    {
      throw new IllegalArgumentException("No file type provided");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.BaseLocationsDataFile#load()
   */
  @Override
  public void load()
  {
    super.load();
  }

  /**
   * Loads a list of locations from a file of a given format, falling
   * back to an internal resource with the same name.
   */
  public void loadWithFallback()
  {
    // 1. Load from file
    load();
    // 2. Load from internal store
    if (locations == null)
    {
      final InputStream input = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(getFilename());
      load(input);
    }
    // 3. If no locations are loaded, fail
    if (locations == null)
    {
      throw new RuntimeException("Cannot load locations");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.BaseLocationsDataFile#save()
   */
  @Override
  public void save()
  {
    super.save();
  }

  /**
   * Sets the locations for the current user.
   * 
   * @param locations
   *        Locations
   */
  public void setLocations(final List<Location> locations)
  {
    if (locations == null)
    {
      return;
    }
    this.locations = locations;
  }

}
