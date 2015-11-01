package sf.util;


import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;

import java.nio.file.Path;

public class FileUtils
{

  public static boolean isDirectoryValid(final Path directory)
  {
    return directory != null && exists(directory) && isDirectory(directory);
  }

  public static boolean isFileReadable(final Path file)
  {
    return file != null && exists(file) && isRegularFile(file)
           && isReadable(file);
  }

  private FileUtils()
  {
    // Utility class
  }

}
