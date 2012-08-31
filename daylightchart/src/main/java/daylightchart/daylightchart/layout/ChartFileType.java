/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2012, Sualeh Fatehi.
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
package daylightchart.daylightchart.layout;


import daylightchart.options.FileType;

/**
 * Chart file type, when exported.
 * 
 * @author Sualeh Fatehi
 */
public enum ChartFileType
  implements FileType
{
  /** Portable Network Graphics */
  png("Portable Network Graphics", ".png"),
  /** JPEG */
  jpg("JPEG", ".jpg"),
  /** Web Page */
  html("Web Page", ".html"),
  /** Adobe Acrobat PDF */
  pdf("Adobe Acrobat PDF", ".pdf");

  private final String description;
  private final String fileExtension;

  private ChartFileType(final String description, final String fileExtension)
  {
    this.description = description;
    this.fileExtension = fileExtension;
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.FileType#getDescription()
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.FileType#getFileExtension()
   */
  public String getFileExtension()
  {
    return fileExtension;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString()
  {
    return description + " (*" + fileExtension + ")";
  }

}
