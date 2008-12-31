package daylightchart.daylightchart.layout;


import daylightchart.gui.util.FileType;

public enum ChartFileType
  implements FileType
{

  png("Portable Network Graphics", ".png"),
  jpg("JPEG", ".jpg"),
  html("Web Page", ".html"),
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
   * @see daylightchart.gui.util.FileType#getDescription()
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.gui.util.FileType#getFileExtension()
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
