/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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
package sf.util.ui;


import java.io.File;
import java.util.Locale;

/**
 * Filters files by extension.
 * 
 * @author sfatehi
 */
public class ExtensionFileFilter<T extends FileType>
  extends javax.swing.filechooser.FileFilter
  implements java.io.FileFilter
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
   * 
   * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
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

  @Override
  public String getDescription()
  {
    return fileType.getDescription();
  }

  public T getFileType()
  {
    return fileType;
  }

}
