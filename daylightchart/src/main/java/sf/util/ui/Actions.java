/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package sf.util.ui;


import java.awt.Component;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * GUI helper methods.
 * 
 * @author Sualeh Fatehi
 */
public class Actions
{

  /**
   * Shows an open file dialog, and returns the selected file. Checks if
   * the file is readable.
   * 
   * @param parent
   * @param dialogTitle
   *        Dialog title
   * @param fileFilters
   *        File filters
   * @param suggestedFile
   *        Suggested file name
   * @param cannotReadMessage
   *        Message if the file cannot be read
   * @return Selected file to open
   */
  public static <T extends FileType> SelectedFile<T> showOpenDialog(final Component parent,
                                                                    final String dialogTitle,
                                                                    final List<ExtensionFileFilter<T>> fileFilters,
                                                                    final File suggestedFile,
                                                                    final String cannotReadMessage)
  {
    final JFileChooser fileDialog = new JFileChooser();
    fileDialog.setDialogTitle(dialogTitle);
    fileDialog.setSelectedFile(suggestedFile);
    if (fileFilters != null && fileFilters.size() > 0)
    {
      fileDialog.setAcceptAllFileFilterUsed(false);
      for (final FileFilter fileFilter: fileFilters)
      {
        fileDialog.addChoosableFileFilter(fileFilter);
      }
    }
    fileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
    final int dialogReturnValue = fileDialog.showOpenDialog(parent);

    if (dialogReturnValue != JFileChooser.APPROVE_OPTION)
    {
      return new SelectedFile<T>();
    }

    File selectedFile = fileDialog.getSelectedFile();
    selectedFile = addExtension(fileDialog, selectedFile);
    if (selectedFile == null || !selectedFile.exists()
        || !selectedFile.canRead())
    {
      JOptionPane.showMessageDialog(parent, selectedFile + "\n"
                                            + cannotReadMessage);
      return new SelectedFile<T>();
    }
    return new SelectedFile<T>(selectedFile,
                               (ExtensionFileFilter<T>) fileDialog
                                 .getFileFilter());
  }

  /**
   * Shows the save dialog.
   * 
   * @param parent
   *        Main GUI window.
   * @param dialogTitle
   *        Dialog title.
   * @param suggestedFile
   *        Suggested file name.
   * @param fileFilters
   *        File filters for the dialog
   * @param overwriteMessage
   *        Message to confirm overwrite
   * @return Selected file, or null if no file is selected.
   */
  public static <T extends FileType> SelectedFile<T> showSaveDialog(final Component parent,
                                                                    final String dialogTitle,
                                                                    final List<ExtensionFileFilter<T>> fileFilters,
                                                                    final File suggestedFile,
                                                                    final String overwriteMessage)
  {
    final JFileChooser fileDialog = new JFileChooser();
    fileDialog.setDialogTitle(dialogTitle);
    fileDialog.setSelectedFile(suggestedFile);
    if (fileFilters != null && fileFilters.size() > 0)
    {
      fileDialog.setAcceptAllFileFilterUsed(false);
      for (final FileFilter fileFilter: fileFilters)
      {
        fileDialog.addChoosableFileFilter(fileFilter);
      }
    }
    fileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
    final int dialogReturnValue = fileDialog.showSaveDialog(parent);

    if (dialogReturnValue != JFileChooser.APPROVE_OPTION)
    {
      return null;
    }

    File selectedFile = fileDialog.getSelectedFile();
    if (selectedFile != null)
    {
      selectedFile = addExtension(fileDialog, selectedFile);

      if (selectedFile.exists())
      {
        final int confirm = JOptionPane
          .showConfirmDialog(parent,
                             selectedFile + "\n" + overwriteMessage,
                             dialogTitle,
                             JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
        {
          selectedFile = null;
        }
      }
    }
    return new SelectedFile<T>(selectedFile,
                               (ExtensionFileFilter<T>) fileDialog
                                 .getFileFilter());
  }

  private static File addExtension(final JFileChooser fileDialog,
                                   File selectedFile)
  {
    // Add extension, if it is not provided
    final FileFilter fileFilter = fileDialog.getFileFilter();
    if (fileFilter instanceof ExtensionFileFilter)
    {
      final ExtensionFileFilter<?> extFileFilter = (ExtensionFileFilter<?>) fileFilter;
      final String selectedExtension = extFileFilter.getFileType()
        .getFileExtension();
      if (!ExtensionFileFilter.getExtension(selectedFile)
        .equals(selectedExtension))
      {
        selectedFile = new File(selectedFile.getAbsoluteFile()
                                + selectedExtension);
      }
    }
    return selectedFile;
  }

  private Actions()
  {
    // Prevent instantiation
  }
}
