/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.LightGray;
import daylightchart.chart.options.ChartOptionsService;
import daylightchart.gui.DaylightChartGui;
import daylightchart.options.service.PersistenceConfigurationService;
import daylightchart.options.service.UserPreferencesService;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

/** Main window. */
public final class Main {

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  /**
   * Main window.
   *
   * @param args Arguments
   */
  public static void main(final String[] args) {
    final PersistenceConfigurationService persistenceConfigurationService =
        PersistenceConfigurationService.configuration();
    persistenceConfigurationService.configureLoggingFromEnvironment();

    final Path preferencesDirectory = persistenceConfigurationService.resolvePreferencesDirectory();
    UserPreferencesService.preferences().initialize(preferencesDirectory);
    ChartOptionsService.chartOptions().initialize(preferencesDirectory);
    ChartOptionsService.chartOptions().loadChartOptions();

    // Set UI look and feel
    try {
      PlasticLookAndFeel.setPlasticTheme(new LightGray());
      UIManager.setLookAndFeel(new PlasticLookAndFeel());
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Cannot set look and feel");
    }

    new DaylightChartGui().setVisible(true);
  }

  private Main() {
    // No-op
  }
}
