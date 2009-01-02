package daylightchart.gui.actions;


import daylightchart.options.FileType;

public enum LocationFileType
  implements FileType
{

  data("Daylight Chart data file", ".data"),
  gns_country_file("GNS Country File", ".txt"),
  gns_country_file_zipped("GNS Country File, zipped", ".zip"),
  gnis_state_file("GNIS states file", ".txt"),
  gnis_state_file_zipped("GNIS states file, zipped", ".zip"), ;

  private final String description;
  private final String fileExtension;

  private LocationFileType(final String description, final String fileExtension)
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
