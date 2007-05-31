/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
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


import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.LightGray;

import daylightchart.gui.DaylightChartGui;
import daylightchart.options.UserPreferences;

/**
 * Main window.
 * 
 * @author Sualeh Fatehi
 */
public final class Main
{

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  /**
   * Main window.
   * 
   * @param args
   *        Arguments
   */
  public static void main(final String[] args)
  {

    // Parse command line
    if (args.length > 1)
    {
      if (args[0].equals("-noprefs"))
      {
        UserPreferences.setSavePreferences(false);
      }
    }

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

    new DaylightChartGui().setVisible(true);
  }

  private Main()
  {
    // No-op
  }

}
