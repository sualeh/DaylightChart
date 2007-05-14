package daylightchart.gui;


import java.io.File;

/**
 * Filters files by extension.
 * 
 * @author sfatehi
 */
public class ExtensionFileFilter
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
      extension = fileName.substring(i + 1).toLowerCase();
    }
    return "." + extension;
  }

  /** A description for the file type. */
  private final String description;

  /** File extensions that are accepted. */
  private final String extension;

  /**
   * Constructor.
   * 
   * @param description
   *        A description of the file type;
   * @param extension
   *        The file extension;
   */
  public ExtensionFileFilter(final String description, final String extension)
  {
    this.description = description;

    String fileExtension = extension;
    if (fileExtension == null)
    {
      fileExtension = "";
    }
    if (!fileExtension.startsWith("."))
    {
      fileExtension = "." + fileExtension;
    }
    this.extension = fileExtension;

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
      accept = extension != null && extension.equals(this.extension);
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
    return description;
  }

  /**
   * Gets the file extension.
   * 
   * @return File extension.
   */
  public String getExtension()
  {
    return extension;
  }

}
