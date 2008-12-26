package daylightchart.gui.actions;


import sf.util.ui.FileType;

public enum ReportDesignFileType
  implements FileType
{

  report_design("Report design file", ".jrxml");

  private final String description;
  private final String fileExtension;

  private ReportDesignFileType(final String description,
                               final String fileExtension)
  {
    this.description = description;
    this.fileExtension = fileExtension;
  }

  /**
   * {@inheritDoc}
   * 
   * @see sf.util.ui.FileType#getDescription()
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * {@inheritDoc}
   * 
   * @see sf.util.ui.FileType#getFileExtension()
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
