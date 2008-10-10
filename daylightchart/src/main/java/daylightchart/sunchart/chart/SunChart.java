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
package daylightchart.sunchart.chart;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.xy.VectorSeries;
import org.jfree.data.xy.VectorSeriesCollection;
import org.jfree.ui.RectangleInsets;

import daylightchart.daylightchart.chart.ChartConfiguration;
import daylightchart.location.Location;
import daylightchart.location.parser.LocationParser;
import daylightchart.options.chart.ChartOptions;
import daylightchart.options.chart.ChartOptionsListener;
import daylightchart.sunchart.calculation.SunChartUtility;
import daylightchart.sunchart.calculation.SunChartYearData;
import daylightchart.sunchart.calculation.SunPosition;
import daylightchart.sunchart.calculation.SunPositions;

/**
 * Produces a chart of daylight times for any location.
 * 
 * @author Sualeh Fatehi
 */
public class SunChart
  extends JFreeChart
  implements ChartOptionsListener
{

  /**
   * 
   */
  private static final long serialVersionUID = 7087394007991008310L;

  private static final Logger LOGGER = Logger.getLogger(SunChart.class
    .getName());

  public static void main(final String[] args)
  {
    try
    {
      final SunChartYearData sunChartData = SunChartUtility
        .createSunChartYear(LocationParser
                              .parseLocation("Albany, NY;US;America/New_York;+4239-07345/"),
                            Calendar.getInstance().get(Calendar.YEAR));
      final SunChart sunChart = new SunChart(sunChartData);

      final JFrame frame = new JFrame("Sun Chart");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      final JPanel chartPanel = new ChartPanel(sunChart);
      chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
      frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
      frame.pack();
      frame.setVisible(true);
    }
    catch (final Exception e)
    {
      e.printStackTrace();
    }
  }

  private final SunChartYearData sunChartData;

  /**
   * Create an empty chart, just to get the default chart options.
   */
  public SunChart()
  {
    this(SunChartUtility.createSunChartYear(null, Calendar.getInstance()
      .get(Calendar.YEAR)));
    setTitle("");
  }

  /**
   * Instantiate the chart for a given location, and given year.
   * 
   * @param location
   *        Location
   * @param year
   *        Year
   * @param options
   *        Options
   */
  public SunChart(final SunChartYearData sunChartData)
  {
    super(new XYPlot());
    this.sunChartData = sunChartData;
    createChart();
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.chart.ChartOptionsListener#afterSettingChartOptions(ChartOptions)
   */
  public void afterSettingChartOptions(final ChartOptions chartOptions)
  {
    Font titleFont;
    final TextTitle title = getTitle();
    if (title != null)
    {
      titleFont = title.getFont();
    }
    else
    {
      titleFont = ChartConfiguration.chartFont;
    }
    createTitles(chartOptions, titleFont.deriveFont(Font.BOLD, 18));
  }

  /**
   * {@inheritDoc}
   * 
   * @see daylightchart.options.chart.ChartOptionsListener#beforeSettingChartOptions(ChartOptions)
   */
  public void beforeSettingChartOptions(@SuppressWarnings("unused") final ChartOptions chartOptions)
  {
    // No-op
  }

  @SuppressWarnings("deprecation")
  private void createAltitudeAxis(final XYPlot plot)
  {
    final NumberAxis axis = new NumberAxis();
    axis.setTickLabelFont(ChartConfiguration.chartFont.deriveFont(Font.PLAIN,
                                                                  12));
    //
    plot.setRangeAxis(axis);
  }

  @SuppressWarnings("deprecation")
  private void createAzimuthAxis(final XYPlot plot)
  {
    final NumberAxis axis = new NumberAxis();
    axis.setTickLabelFont(ChartConfiguration.chartFont.deriveFont(Font.PLAIN,
                                                                  12));
    axis.setVerticalTickLabels(true);
    //
    plot.setDomainAxis(axis);
  }

  /**
   * Creates bands for the sunrise and sunset times for the whole year.
   */
  private void createBandsInPlot(final XYPlot plot)
  {
    final VectorSeriesCollection vectorSeriesCollection = new VectorSeriesCollection();
    final List<SunPositions> sunPositionsList = sunChartData
      .getSunPositionsList();
    for (final SunPositions sunPositions: sunPositionsList)
    {
      final VectorSeries vectorSeries = new VectorSeries(sunPositions.getDate()
        .toString());
      final List<SunPosition> sunPositionList = sunPositions.getSunPositions();
      for (final SunPosition sunPosition: sunPositionList)
      {
        vectorSeries.add(sunPosition.getAzimuth(),
                         sunPosition.getAltitude(),
                         0,
                         0);
      }
      vectorSeriesCollection.addSeries(vectorSeries);
    }
    final int currentDatasetNumber = plot.getDatasetCount();
    plot.setDataset(currentDatasetNumber, vectorSeriesCollection);
  }

  /**
   * Creates the daylight chart.
   */
  private void createChart()
  {

    setBackgroundPaint(Color.white);

    final XYPlot plot = getXYPlot();

    plot.setRenderer(new StandardXYItemRenderer());
    plot.setBackgroundPaint(Color.white);

    plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
    createAzimuthAxis(plot);
    createAltitudeAxis(plot);

    createBandsInPlot(plot);

  }

  @SuppressWarnings("unchecked")
  private void createTitles(final ChartOptions chartOptions,
                            final Font titleFont)
  {

    // Clear all titles and subtitles
    setTitle((TextTitle) null);
    for (final Title subtitle: (List<Title>) getSubtitles())
    {
      if (subtitle instanceof TextTitle)
      {
        removeSubtitle(subtitle);
      }
    }

    // Build new titles and legend
    final Location location = sunChartData.getLocation();
    final boolean showTitle = chartOptions.getTitleOptions().getShowTitle();
    if (location != null && showTitle)
    {
      final TextTitle title = new TextTitle(location.toString(), titleFont);
      setTitle(title);

      final Font subtitleFont = titleFont.deriveFont(Font.PLAIN);
      final TextTitle subtitle = new TextTitle(location.getDetails(),
                                               subtitleFont);
      subtitle.setPaint(title.getPaint());
      addSubtitle(subtitle);
    }
  }

}
