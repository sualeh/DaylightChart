package daylightchart.gui.util;


/**
 * Defines a type of file, with a given file extension.
 * 
 * @author sfatehi
 */
public interface FileType
{

  /**
   * The description of the file type.
   * 
   * @return the description
   */
  String getDescription();

  /**
   * Gets the file extension.
   * 
   * @return File extension
   */
  String getFileExtension();

}
