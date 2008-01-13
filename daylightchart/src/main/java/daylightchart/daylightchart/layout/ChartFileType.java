package daylightchart.daylightchart.layout;


import org.apache.commons.lang.StringUtils;

public enum ChartFileType
{

  png("Portable Network Graphics", ".png"),
  jpg("JPEG", ".jpg"),
  html("Web Page", ".html"),
  pdf("Adobe Acrobat PDF", ".pdf");

  public static ChartFileType fromExtension(final String extension)
  {
    if (StringUtils.isBlank(extension))
    {
      return null;
    }
    ChartFileType chartFileType = null;
    for (final ChartFileType currentChartFileType: values())
    {
      if (currentChartFileType.getFileExtension().equals(extension))
      {
        chartFileType = currentChartFileType;
        break;
      }
    }
    return chartFileType;
  }

  private final String description;

  private final String fileExtension;

  private ChartFileType(final String description, final String fileExtension)
  {
    this.description = description;
    this.fileExtension = fileExtension;
  }

  /**
   * @return the description
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * @return the fileExtension
   */
  public String getFileExtension()
  {
    return fileExtension;
  }

  @Override
  public String toString()
  {
    return description + " (*" + fileExtension + ")";
  }
}
