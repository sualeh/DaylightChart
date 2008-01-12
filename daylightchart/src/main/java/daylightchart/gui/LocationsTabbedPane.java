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
package daylightchart.gui;


import java.awt.Panel;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;

import sf.util.ui.CloseTabIcon;
import daylightchart.daylightchart.layout.DaylightChartReport;
import daylightchart.location.Location;
import daylightchart.location.parser.LocationFormatter;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;

/**
 * Tabbed pane for location charts.
 * 
 * @author sfatehi
 */
public class LocationsTabbedPane
  extends JTabbedPane
{

  private static final long serialVersionUID = -2086804705336786590L;

  private static final Logger LOGGER = Logger
    .getLogger(LocationsTabbedPane.class.getName());

  /**
   * Add a new tab for the location.
   * 
   * @param location
   *        Location
   */
  public void addLocationTab(final Location location)
  {

    final Options options = UserPreferences.getOptions();
    final DaylightChartReport daylightChartReport = new DaylightChartReport(location,
                                                                            options);
    daylightChartReport
      .write(new File("C:\\Users\\Sualeh Fatehi\\Documents\\_Projects\\DaylightChart\\file.pdf"));

    // Render the report into PDF

    // Open the PDF file in PDF Renderer
    // https://pdf-renderer.dev.java.net/
    final Panel pdfRendererPanel = new Panel();

    addTab(location.toString(),
           new CloseTabIcon(),
           pdfRendererPanel,
           LocationFormatter.getToolTip(location));
    setSelectedIndex(getTabCount() - 1);
  }

}
