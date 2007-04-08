/*
 * Copyright (c) 2004-2007 Sualeh Fatehi. All Rights Reserved.
 */
package daylightchart.gui;


import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;

import daylightchart.chart.DaylightChart;
import daylightchart.location.Angle;
import daylightchart.location.Coordinates;
import daylightchart.location.Latitude;
import daylightchart.location.Location;
import daylightchart.location.Longitude;

/**
 * GUI Utilties.
 * 
 * @author Sualeh Fatehi
 */
public class $ChartGuiUtils
{

  /**
   * Creates a chart editor instance.
   */
  public final static ChartEditor getChartEditor()
  {
    // Create a fake chart
    Coordinates coordinates = new Coordinates(new Latitude(new Angle()),
                                              new Longitude(new Angle()));
    Location location = new Location("", "", "", coordinates);
    DaylightChart chart = new DaylightChart(location, 2007, null);

    ChartEditor chartEditor = ChartEditorManager.getChartEditor(chart);

    return chartEditor;
  }

  private $ChartGuiUtils()
  {
  }

}
