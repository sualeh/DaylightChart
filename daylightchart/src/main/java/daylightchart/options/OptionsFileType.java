package daylightchart.options;


/**
 * Options file.
 * 
 * @author sfatehi
 */
class OptionsFileType
  implements FileType
{

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.FileType#getDescription()
   */
  public String getDescription()
  {
    return "Options file";
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.FileType#getFileExtension()
   */
  public String getFileExtension()
  {
    return ".xml";
  }

}
