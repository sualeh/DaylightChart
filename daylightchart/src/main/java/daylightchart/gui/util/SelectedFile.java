package daylightchart.gui.util;


import java.nio.file.Path;

import daylightchart.options.BaseTypedFile;
import daylightchart.options.FileType;

/**
 * Selected file.
 *
 * @author Sualeh Fatehi
 * @param <T>
 *        File type
 */
public final class SelectedFile<T extends FileType>
  extends BaseTypedFile<T>
{

  SelectedFile()
  {
    super();
  }

  SelectedFile(final Path file, final ExtensionFileFilter<T> fileFilter)
  {
    super(file, fileFilter.getFileType());
  }

  /**
   * Whether the file was selected.
   *
   * @return Whether the file was selected
   */
  public boolean isSelected()
  {
    return hasFile();
  }

}
