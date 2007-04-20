/*
 * Copyright 2007, Sualeh Fatehi <sualeh@hotmail.com>
 * This work is licensed under the Creative Commons Attribution-Noncommercial-No Derivative Works 3.0 License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ 
 * or send a letter to Creative Commons, 543 Howard Street, 5th Floor, San Francisco, California, 94105, USA.
 */
package daylightchart.gui;


import java.io.File;

/**
 * Accept just known image files.
 * 
 * @author Sualeh Fatehi
 */
public class LocationsFileFilter
  extends javax.swing.filechooser.FileFilter
  implements java.io.FileFilter
{

  public enum LocationsFileFormat
  {
    /** JPEG */
    daylightchartLocationsFormat("Daylight Chart Locations File"),
    /** JPEG */
    gnsCountryFilesFormat("GNS Country Files Format"), ;

    private final String description;

    private LocationsFileFormat(final String extension)
    {
      description = extension;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()
    {
      return description;
    }

  }

  private final LocationsFileFormat fileFormat;

  public LocationsFileFilter(final LocationsFileFormat fileFormat)
  {
    if (fileFormat == null)
    {
      throw new IllegalArgumentException("File format cannot be null");
    }
    this.fileFormat = fileFormat;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
   */
  @Override
  public boolean accept(final File f)
  {
    return f.exists() && !f.isDirectory() && f.canRead();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.swing.filechooser.FileFilter#getDescription()
   */
  @Override
  public String getDescription()
  {
    return fileFormat.toString();
  }

  /**
   * @return the fileFormat
   */
  public LocationsFileFormat getFileFormat()
  {
    return fileFormat;
  }

}
