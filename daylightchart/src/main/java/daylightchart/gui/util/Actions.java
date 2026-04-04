/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.util;

import daylightchart.options.persistence.FileType;
import daylightchart.options.service.UserPreferencesService;
import java.awt.Component;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/** GUI helper methods. */
public class Actions {

  /**
   * Sets the working directory.
   *
   * @param workingDirectory Working directory
   */
  public static void setWorkingDirectory(final Path workingDirectory) {
    UserPreferencesService.preferences().saveWorkingDirectory(workingDirectory);
  }

  /**
   * Shows an open file dialog, and returns the selected file. Checks if the file is readable.
   *
   * @param <T> File type for the selected file
   * @param parent Parent component
   * @param dialogTitle Dialog title
   * @param fileFilters File filters
   * @param suggestedFile Suggested file name
   * @param cannotReadMessage Message if the file cannot be read
   * @return Selected file to open
   */
  public static <T extends FileType> SelectedFile<T> showOpenDialog(
      final Component parent,
      final String dialogTitle,
      final List<ExtensionFileFilter<T>> fileFilters,
      final File suggestedFile,
      final String cannotReadMessage) {
    final JFileChooser fileDialog = new JFileChooser();
    fileDialog.setDialogTitle(dialogTitle);
    fileDialog.setSelectedFile(suggestedFile);
    if (fileFilters != null && fileFilters.size() > 0) {
      fileDialog.setAcceptAllFileFilterUsed(false);
      for (final FileFilter fileFilter : fileFilters) {
        fileDialog.addChoosableFileFilter(fileFilter);
      }
    }
    fileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);

    Path file = null;

    final int dialogReturnValue = fileDialog.showOpenDialog(parent);
    if (dialogReturnValue == JFileChooser.APPROVE_OPTION) {
      file = getSelectedFileWithExtension(fileDialog);
      if (!Files.isRegularFile(file) || !Files.isReadable(file)) {
        JOptionPane.showMessageDialog(parent, file + "\n" + cannotReadMessage);
        file = null;
      }
    }

    final SelectedFile<T> selectedFile =
        new SelectedFile<T>(file, (ExtensionFileFilter<T>) fileDialog.getFileFilter());

    // Save last selected directory
    if (selectedFile.isSelected()) {
      setWorkingDirectory(selectedFile.getDirectory());
    }

    return selectedFile;
  }

  /**
   * Shows the save dialog.
   *
   * @param <T> File type for the selected file
   * @param parent Main GUI window.
   * @param dialogTitle Dialog title.
   * @param suggestedFile Suggested file name.
   * @param fileFilters File filters for the dialog
   * @param overwriteMessage Message to confirm overwrite
   * @return Selected file, or null if no file is selected.
   */
  public static <T extends FileType> SelectedFile<T> showSaveDialog(
      final Component parent,
      final String dialogTitle,
      final List<ExtensionFileFilter<T>> fileFilters,
      final File suggestedFile,
      final String overwriteMessage) {
    final JFileChooser fileDialog = new JFileChooser();
    fileDialog.setDialogTitle(dialogTitle);
    fileDialog.setSelectedFile(suggestedFile);
    if (fileFilters != null && fileFilters.size() > 0) {
      fileDialog.setAcceptAllFileFilterUsed(false);
      for (final FileFilter fileFilter : fileFilters) {
        fileDialog.addChoosableFileFilter(fileFilter);
      }
    }
    fileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);

    Path file = null;

    final int dialogReturnValue = fileDialog.showSaveDialog(parent);
    if (dialogReturnValue == JFileChooser.APPROVE_OPTION) {
      file = getSelectedFileWithExtension(fileDialog);
      if (file != null && Files.exists(file)) {
        final int confirm =
            JOptionPane.showConfirmDialog(
                parent, file + "\n" + overwriteMessage, dialogTitle, JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
          file = null;
        }
      }
    }

    final SelectedFile<T> selectedFile =
        new SelectedFile<T>(file, (ExtensionFileFilter<T>) fileDialog.getFileFilter());

    // Save last selected directory
    if (selectedFile.isSelected()) {
      setWorkingDirectory(selectedFile.getDirectory());
    }

    return selectedFile;
  }

  /**
   * Get the selected file, and add extension, if it is not provided.
   *
   * @param fileDialog File dialog for the file.
   * @return File, with extension
   */
  private static Path getSelectedFileWithExtension(final JFileChooser fileDialog) {
    File file = fileDialog.getSelectedFile();
    if (file != null) {
      final ExtensionFileFilter<?> fileFilter = (ExtensionFileFilter<?>) fileDialog.getFileFilter();
      if (!ExtensionFileFilter.getExtension(file)
          .equals(fileFilter.getFileType().getFileExtension())) {
        file = new File(file.getAbsoluteFile() + fileFilter.getFileExtension());
      }
    }
    return file.toPath();
  }

  private Actions() {
    // Prevent instantiation
  }
}
