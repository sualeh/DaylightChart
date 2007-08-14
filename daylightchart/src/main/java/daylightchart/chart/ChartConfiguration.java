package daylightchart.chart;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * One place to change certain configuration options.
 * 
 * @author Sualeh Fatehi
 */
public final class ChartConfiguration
{

  public static final Dimension chartDimension = new Dimension(770, 570);
  public static final DateFormat monthsFormat = new SimpleDateFormat("MMM");

  public static final Color daylightColor = new Color(0xFF, 0xFF, 0x60, 170);
  public static final Color twilightColor = new Color(0xFF, 0xFF, 0xA0, 130);
  public static final Color nightColor = new Color(75, 11, 91, 150);
  public static final Font chartFont = new Font("Helvetica", Font.PLAIN, 12);

}
