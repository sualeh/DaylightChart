package daylightchart.gui.util;


import java.io.File;

/**
 * Selected file.
 * 
 * @author Sualeh Fatehi
 * @param <T>
 *        File type
 */
public final class SelectedFile<T extends FileType>
{
  private final File file;
  private final T fileType;

  SelectedFile()
  {
    this(null, null);
  }

  SelectedFile(final File file, final ExtensionFileFilter<T> fileFilter)
  {
    this.file = file;
    if (fileFilter != null)
    {
      this.fileType = fileFilter.getFileType();
    }
    else
    {
      this.fileType = null;
    }
  }

  /**
   * Directory of the selected file.
   * 
   * @return Directory of the selected file.
   */
  public File getDirectory()
  {
    if (file == null)
    {
      return null;
    }
    else
    {
      return file.getParentFile();
    }
  }

  /**
   * The selected file.
   * 
   * @return Selected file
   */
  public File getFile()
  {
    return file;
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
   * @return the isSelected
   */
  public boolean isSelected()
  {
    return file != null;
  }

}
