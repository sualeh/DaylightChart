package daylightchart.options;


/**
 * Options file.
 * 
 * @author sfatehi
 */
class ReportFileType
  implements FileType
{

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.FileType#getDescription()
   */
  public String getDescription()
  {
    return "Daylight Chart report file";
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.FileType#getFileExtension()
   */
  public String getFileExtension()
  {
    return ".jrxml";
  }

}
