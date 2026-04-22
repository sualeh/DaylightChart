/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.actions;

import daylightchart.chart.options.ChartOptions;
import daylightchart.chart.options.ChartOptionsService;
import daylightchart.chart.report.ChartFileType;
import daylightchart.chart.report.DaylightChartReport;
import daylightchart.chart.report.DaylightChartReportService;
import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.Messages;
import daylightchart.gui.util.Actions;
import daylightchart.gui.util.ExtensionFileFilter;
import daylightchart.gui.util.GuiAction;
import daylightchart.gui.util.SelectedFile;
import daylightchart.options.Options;
import daylightchart.options.service.UserPreferencesService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/** Saves locations into a file. */
public final class SaveChartAction extends GuiAction {

  private static final class GuiActionListener implements ActionListener {
    private final DaylightChartGui mainWindow;

    private GuiActionListener(final DaylightChartGui mainWindow) {
      this.mainWindow = mainWindow;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent actionevent) {
      final UserPreferencesService preferencesService = UserPreferencesService.preferences();
      final ChartOptions chartOptions = ChartOptionsService.chartOptions().loadChartOptions();
      final Options options = preferencesService.loadOptions();
      final DaylightChartReport daylightChartReport =
          DaylightChartReportService.reports()
              .createReport(mainWindow.getSelectedLocation(), options, chartOptions);
      final List<ExtensionFileFilter<ChartFileType>> fileFilters = new ArrayList<>();
      for (final ChartFileType chartFileType : ChartFileType.values()) {
        fileFilters.add(new ExtensionFileFilter<>(chartFileType));
      }
      final String reportFilename = daylightChartReport.getReportFileName(ChartFileType.png);
      final Path chartFile = Path.of(options.getWorkingDirectory().toString(), reportFilename);
      final SelectedFile<ChartFileType> selectedFile =
          Actions.showSaveDialog(
              mainWindow,
              Messages.getString("DaylightChartGui.Menu.File.SaveChart"),
              fileFilters,
              chartFile.toFile(),
              Messages.getString("DaylightChartGui.Message.Confirm.FileOverwrite")); // $NON-NLS-1$
      if (selectedFile.isSelected()) {
        try {
          daylightChartReport.write(selectedFile.getFile(), selectedFile.getFileType());
        } catch (final Exception e) {
          LOGGER.log(
              Level.WARNING,
              Messages.getString("DaylightChartGui.Message.Error.CannotSaveFile"), // $NON-NLS-1$
              e);
          JOptionPane.showMessageDialog(
              mainWindow,
              Messages.getString("DaylightChartGui.Message.Error.CannotSaveFile") // $NON-NLS-1$
                  + "\n" //$NON-NLS-1$
                  + selectedFile,
              Messages.getString("DaylightChartGui.Message.Error.CannotSaveFile"), // $NON-NLS-1$
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  @Serial private static final long serialVersionUID = 1173685118494564955L;

  private static final Logger LOGGER = Logger.getLogger(SaveChartAction.class.getName());

  /**
   * Saves locations into a file.
   *
   * @param mainWindow Main window.
   */
  public SaveChartAction(final DaylightChartGui mainWindow) {

    super(
        Messages.getString("DaylightChartGui.Menu.File.SaveChart"), // $NON-NLS-1$
        "/icons/save.gif" //$NON-NLS-1$
        );
    setShortcutKey(KeyStroke.getKeyStroke("control S"));
    addActionListener(new GuiActionListener(mainWindow));
  }
}
