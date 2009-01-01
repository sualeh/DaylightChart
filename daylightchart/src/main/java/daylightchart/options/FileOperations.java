/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2009, Sualeh Fatehi.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package daylightchart.options;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import org.geoname.data.Location;
import org.geoname.parser.GNISFilesParser;
import org.geoname.parser.GNSCountryFilesParser;
import org.geoname.parser.LocationFormatter;
import org.geoname.parser.LocationParser;
import org.geoname.parser.UnicodeReader;

import com.thoughtworks.xstream.XStream;

import daylightchart.gui.actions.LocationFileType;
import daylightchart.gui.util.SelectedFile;

/**
 * User preferences for the GUI.
 * 
 * @author Sualeh Fatehi
 */
public final class FileOperations
{

  private static final Logger LOGGER = Logger
    .getLogger(FileOperations.class.getName());

  /**
   * Loads a list of locations from a file of a given format.
   * 
   * @param selectedFile
   *        Selected file of a known location file format.
   * @return List of locations, read from the file
   */
  public static List<Location> loadLocationsFromFile(final SelectedFile<LocationFileType> selectedFile)
  {
    if (selectedFile == null || !selectedFile.isSelected())
    {
      LOGGER.log(Level.WARNING, "No locations file provided");
      return null;
    }

    List<Location> locations = new ArrayList<Location>();
    LocationFileType fileType = selectedFile.getFileType();
    List<InputStream> inputs = new ArrayList<InputStream>();
    try
    {
      switch (fileType)
      {
        case data:
        case gns_country_file:
        case gnis_state_file:
          inputs.add(new FileInputStream(selectedFile.getFile()));
          break;
        case gns_country_file_zipped:
        case gnis_state_file_zipped:
          ZipFile zipFile = new ZipFile(selectedFile.getFile());
          List<ZipEntry> zippedFiles = (List<ZipEntry>) Collections
            .list(zipFile.entries());
          for (ZipEntry zipEntry: zippedFiles)
          {
            inputs.add(zipFile.getInputStream(zipEntry));
          }
          break;
      }

      for (InputStream inputStream: inputs)
      {
        Reader reader = new UnicodeReader(inputStream, "UTF-8");
        switch (fileType)
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
      LOGGER.log(Level.WARNING,
                 "Could not read locations from " + selectedFile,
                 e);
      locations = null;
    }
    finally
    {
      for (InputStream inputStream: inputs)
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

    return locations;
  }

  /**
   * Saves locations to a file.
   * 
   * @param locations
   *        Locations to save
   * @param file
   *        File
   */
  public static void saveLocationsToFile(final List<Location> locations,
                                         final File file)
  {
    try
    {
      if (locations == null)
      {
        LOGGER.log(Level.WARNING, "No locations provided");
        return;
      }
      if (file == null)
      {
        LOGGER.log(Level.WARNING, "No locations file provided");
        return;
      }
      if (file.exists())
      {
        file.delete();
      }
      final Writer writer = getFileWriter(file);
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

  /**
   * Saves report to a file.
   * 
   * @param report
   *        JasperReports report
   * @param file
   *        File to write
   */
  public static void saveReportToFile(final JRReport report, final File file)
  {
    try
    {
      if (report == null)
      {
        LOGGER.log(Level.WARNING, "No report provided");
        return;
      }
      if (file == null)
      {
        LOGGER.log(Level.WARNING, "No report file provided");
        return;
      }
      if (file.exists())
      {
        file.delete();
      }

      JRXmlWriter.writeReport(report, file.getAbsolutePath(), "UTF-8");
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not save report to " + file, e);
    }
  }

  private static JasperReport compileReport(final InputStream reportStream)
  {
    try
    {
      if (reportStream == null)
      {
        return null;
      }
      final JasperDesign jasperDesign = JRXmlLoader.load(reportStream);
      final JasperReport jasperReport = JasperCompileManager
        .compileReport(jasperDesign);
      return jasperReport;
    }
    catch (final JRException e)
    {
      LOGGER.log(Level.WARNING, "Cannot load JasperReport", e);
      return null;
    }
    finally
    {
      if (reportStream != null)
      {
        try
        {
          reportStream.close();
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, "Cannot close input stream", e);
        }
      }
    }
  }

  private static Writer getFileWriter(final File file)
  {
    try
    {
      final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
                                                                              "UTF-8"));
      return writer;
    }
    catch (final UnsupportedEncodingException e)
    {
      LOGGER.log(Level.WARNING, "Cannot write file " + file, e);
      return null;
    }
    catch (final FileNotFoundException e)
    {
      LOGGER.log(Level.WARNING, "Cannot write file " + file, e);
      return null;
    }
  }

  /**
   * Loads options from a file.
   * 
   * @param file
   *        File
   * @return Options
   */
  public static Options loadOptionsFromFile(final File file)
  {
    if (file == null || !file.exists() || !file.canRead())
    {
      LOGGER.log(Level.WARNING, "No options file provided");
      return null;
    }

    Reader reader = null;
    Options options;
    try
    {
      reader = new UnicodeReader(new FileInputStream(file), "UTF-8");
      final XStream xStream = new XStream();
      options = (Options) xStream.fromXML(reader);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not read options from " + file, e);
      options = null;
    }
    finally
    {
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.WARNING, "Could not close " + file, e);
        }
      }
    }
    return options;
  }

  /**
   * Loads a report from a file.
   * 
   * @param file
   *        File
   * @return JasperReports report
   */
  public static JasperReport loadReportFromFile(final File file)
  {
    if (file == null || !file.exists() || !file.canRead())
    {
      LOGGER.log(Level.WARNING, "No report file provided");
      return null;
    }

    try
    {
      FileInputStream fileInputStream = new FileInputStream(file);
      return compileReport(fileInputStream);
    }
    catch (final FileNotFoundException e)
    {
      LOGGER.log(Level.WARNING, "Could not read report from " + file, e);
      return null;
    }
  }

  /**
   * Saves options to a file.
   * 
   * @param file
   *        File to write
   * @param options
   *        Options
   */
  public static void saveOptionsToFile(final Options options, final File file)
  {
    try
    {
      if (options == null)
      {
        LOGGER.log(Level.WARNING, "No options provided");
        return;
      }
      if (file == null)
      {
        LOGGER.log(Level.WARNING, "No options file provided");
        return;
      }
      if (file.exists())
      {
        file.delete();
      }
      final Writer writer = getFileWriter(file);
      if (writer == null)
      {
        LOGGER.log(Level.WARNING, "Could not save options to " + file);
        return;
      }

      final XStream xStream = new XStream();
      xStream.toXML(options, writer);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could save options to " + file, e);
    }
  }

  private FileOperations()
  {
    // Prevent external instantiation
  }
}
