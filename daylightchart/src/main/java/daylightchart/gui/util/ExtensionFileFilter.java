/*
 *
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2015, Sualeh Fatehi.
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
package daylightchart.gui.util;


import java.io.File;
import java.util.Locale;

import daylightchart.options.FileType;

/**
 * Filters files by extension.
 *
 * @author sfatehi
 * @param <T>
 *        A file type enumeration
 */
public class ExtensionFileFilter<T extends FileType>
  extends javax.swing.filechooser.FileFilter
  implements java.io.FileFilter, FileType
{

  /**
   * Gets the extension for the given file.
   *
   * @param file
   *        File
   * @return Extension
   */
  public static String getExtension(final File file)
  {
    String extension = "";
    final String fileName = file.getName();
    final int i = fileName.lastIndexOf('.');
    if (i > 0 && i < fileName.length() - 1)
    {
      extension = fileName.substring(i + 1).toLowerCase(Locale.ENGLISH);
    }
    return "." + extension;
  }

  /** A description for the file type. */
  private final T fileType;

  /**
   * Constructor.
   *
   * @param fileType
   *        A description of the file type.
   */
  public ExtensionFileFilter(final T fileType)
  {
    this.fileType = fileType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean accept(final File file)
  {
    boolean accept = false;
    if (file.isDirectory())
    {
      accept = true;
    }
    else if (file.isHidden())
    {
      accept = false;
    }
    else
    {
      final String extension = getExtension(file);
      accept = extension != null
               && extension.equals(fileType.getFileExtension());
    }
    return accept;
  }

  /**
   * {@inheritDoc}
   *
   * @see javax.swing.filechooser.FileFilter#getDescription()
   */
  @Override
  public String getDescription()
  {
    return fileType.getDescription();
  }

  /**
   * {@inheritDoc}
   *
   * @see daylightchart.options.FileType#getFileExtension()
   */
  @Override
  public String getFileExtension()
  {
    return fileType.getFileExtension();
  }

  /**
   * Gets the file type.
   *
   * @return File type.
   */
  public T getFileType()
  {
    return fileType;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return fileType.getDescription();
  }

}
