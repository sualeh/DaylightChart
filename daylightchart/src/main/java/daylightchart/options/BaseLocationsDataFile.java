package daylightchart.options;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.geoname.data.Location;
import org.geoname.parser.GNISFilesParser;
import org.geoname.parser.GNSCountryFilesParser;
import org.geoname.parser.LocationFormatter;
import org.geoname.parser.LocationParser;
import org.geoname.parser.UnicodeReader;

import daylightchart.gui.actions.LocationFileType;

/**
 * Represents a location file, with data.
 * 
 * @author sfatehi
 */
public abstract class BaseLocationsDataFile
  extends BaseTypedFile<LocationFileType>
{

  private static final Logger LOGGER = Logger
    .getLogger(BaseLocationsDataFile.class.getName());

  protected List<Location> locations;

  /**
   * Constructor.
   * 
   * @param locationDataFile
   *        File
   */
  public BaseLocationsDataFile(final BaseTypedFile<LocationFileType> locationDataFile)
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
  public BaseLocationsDataFile(final File file, final LocationFileType fileType)
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
   * Gets the locations for the current user.
   * 
   * @return Locations
   */
  public final List<Location> getLocations()
  {
    return new ArrayList<Location>(locations);
  }

  /**
   * Loads a list of locations from a file of a given format.
   */
  protected void load()
  {
    if (!exists())
    {
      LOGGER.log(Level.WARNING, "No locations file provided");
      locations = null;
      return;
    }

    final List<InputStream> inputs = new ArrayList<InputStream>();
    final File file = getFile();
    try
    {
      switch (getFileType())
      {
        case data:
        case gns_country_file:
        case gnis_state_file:
          inputs.add(new FileInputStream(file));
          break;
        case gns_country_file_zipped:
        case gnis_state_file_zipped:
          final ZipFile zipFile = new ZipFile(file);
          final List<ZipEntry> zippedFiles = (List<ZipEntry>) Collections
            .list(zipFile.entries());
          for (final ZipEntry zipEntry: zippedFiles)
          {
            inputs.add(zipFile.getInputStream(zipEntry));
          }
          break;
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not read locations from " + file, e);
      locations = null;
    }

    load(inputs.toArray(new InputStream[inputs.size()]));
  }

  protected final void load(final InputStream... inputs)
  {
    locations = new ArrayList<Location>();
    try
    {
      for (final InputStream inputStream: inputs)
      {
        final Reader reader = new UnicodeReader(inputStream, "UTF-8");
        switch (getFileType())
        {
          case data:
            locations.addAll(LocationParser.parseLocations(reader));
            break;
          case gns_country_file:
          case gns_country_file_zipped:
            locations.addAll(GNSCountryFilesParser.parseLocations(reader));
            break;
          case gnis_state_file:
          case gnis_state_file_zipped:
            locations.addAll(GNISFilesParser.parseLocations(reader));
            break;
        }
        reader.close();
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not read locations", e);
      locations = null;
    }
    finally
    {
      for (final InputStream inputStream: inputs)
      {
        if (inputStream != null)
        {
          try
          {
            inputStream.close();
          }
          catch (final IOException e)
          {
            LOGGER.log(Level.WARNING, "Could not close input stream", e);
          }
        }
      }
    }

    if (locations.size() == 0)
    {
      locations = null;
    }
  }

  /**
   * Saves locations to a file.
   */
  protected void save()
  {
    if (locations == null)
    {
      LOGGER.log(Level.WARNING, "No locations provided");
      return;
    }

    final File file = getFile();
    if (file == null)
    {
      LOGGER.log(Level.WARNING, "No locations file provided");
      return;
    }

    try
    {
      if (file.exists())
      {
        file.delete();
      }
      final Writer writer = FileOperations.getFileWriter(file);
      if (writer == null)
      {
        LOGGER.log(Level.WARNING, "Could not save locations to " + file);
        return;
      }

      LocationFormatter.formatLocations(locations, writer);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not save locations to " + file, e);
    }
  }

}
