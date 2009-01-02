package daylightchart.options;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a data file, with data.
 * 
 * @author sfatehi
 */
public abstract class BaseDataFile<T extends FileType, D>
  extends BaseTypedFile<T>
{

  private static final Logger LOGGER = Logger.getLogger(BaseDataFile.class
    .getName());

  protected D data;

  /**
   * Constructor.
   * 
   * @param typedFile
   *        File
   */
  public BaseDataFile(final BaseTypedFile<T> typedFile)
  {
    this(typedFile.getFile(), typedFile.getFileType());
  }

  /**
   * Constructor.
   * 
   * @param file
   *        File
   * @param fileType
   *        File type
   */
  public BaseDataFile(final File file, final T fileType)
  {
    super(file, fileType);

    // Validation
    if (file == null)
    {
      throw new IllegalArgumentException("No file provided");
    }
    if (fileType == null)
    {
      throw new IllegalArgumentException("No file type provided");
    }
  }

  /**
   * Gets data.
   * 
   * @return Data
   */
  public final D getData()
  {
    return data;
  }

  /**
   * Sets data.
   * 
   * @param data
   *        Data
   */
  public final void setData(D data)
  {
    if (data != null)
    {
      this.data = data;
    }
  }

  /**
   * Loads data from the file.
   */
  public abstract void load();

  public abstract void loadWithFallback();

  protected abstract void load(final InputStream... inputs);

  /**
   * Saves data to a file.
   */
  public abstract void save();

  protected final Writer getFileWriter(final File file)
  {
    try
    {
      final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
                                                                              "UTF-8"));
      return writer;
    }
    catch (final UnsupportedEncodingException e)
    {
      LOGGER.log(Level.WARNING, "Cannot write file " + file, e);
      return null;
    }
    catch (final FileNotFoundException e)
    {
      LOGGER.log(Level.WARNING, "Cannot write file " + file, e);
      return null;
    }
  }

}
