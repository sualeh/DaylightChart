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
package daylightchart;


import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import sf.util.CommandLineParser;
import sf.util.CommandLineUtility;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.LightGray;

import daylightchart.daylightchart.calculation.RiseSetUtility;
import daylightchart.gui.DaylightChartGui;
import daylightchart.location.Location;
import daylightchart.location.parser.LocationParser;
import daylightchart.location.parser.ParserException;
import daylightchart.options.UserPreferences;

/**
 * Main window.
 * 
 * @author Sualeh Fatehi
 */
public final class Main
{

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  private static final String OPTION_DEBUG_CALCULATIONS = "debug-calcs";
  private static final String OPTION_SLIMUI = "slimui";
  private static final String OPTION_NO_PREFERENCES = "noprefs";
  private static final String OPTION_LOCATION = "location";

  /**
   * Main window.
   * 
   * @param args
   *        Arguments
   */
  public static void main(final String[] args)
  {

    CommandLineUtility.setLogLevel(args);

    // Parse command line
    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new BooleanOption(Option.NO_SHORT_FORM,
                                       OPTION_DEBUG_CALCULATIONS));
    parser.addOption(new BooleanOption(Option.NO_SHORT_FORM, OPTION_SLIMUI));
    parser.addOption(new BooleanOption(Option.NO_SHORT_FORM,
                                       OPTION_NO_PREFERENCES));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_LOCATION,
                                      null));
    parser.parse(args);
    final boolean debugCalculations = parser
      .getBooleanOptionValue(OPTION_DEBUG_CALCULATIONS);
    boolean slimUi = parser.getBooleanOptionValue(OPTION_SLIMUI);
    final boolean noPreferences = parser
      .getBooleanOptionValue(OPTION_NO_PREFERENCES);
    final String locationString = parser.getStringOptionValue(OPTION_LOCATION);
    Location location = null;
    if (locationString != null)
    {
      try
      {
        location = LocationParser.parseLocation(locationString);
      }
      catch (final ParserException e)
      {
        location = null;
      }
    }

    UserPreferences.setSavePreferences(!noPreferences);

    if (debugCalculations && location != null)
    {
      final File file = RiseSetUtility.writeCalculationsToFile(location);
      System.out.println("Calculations written to " + file.getAbsolutePath());
    }
    else
    {
      // Set UI look and feel
      try
      {
        PlasticLookAndFeel.setPlasticTheme(new LightGray());
        UIManager.setLookAndFeel(new PlasticLookAndFeel());
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.WARNING, "Cannot set look and feel");
      }

      if (!slimUi)
      {
        slimUi = UserPreferences.isSlimUi();
      }
      UserPreferences.setSlimUi(slimUi);
      new DaylightChartGui(location, slimUi).setVisible(true);
    }
  }

  private Main()
  {
    // No-op
  }

}
