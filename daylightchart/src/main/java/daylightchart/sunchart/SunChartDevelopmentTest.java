package daylightchart.sunchart;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import org.geoname.data.Location;
import org.geoname.parser.LocationsListParser;

import daylightchart.sunchart.calculation.SunChartUtility;
import daylightchart.sunchart.calculation.SunChartYearData;
import daylightchart.sunchart.chart.SunChart;

public class SunChartDevelopmentTest
{

  public static void main(final String[] args)
    throws Exception
  {
    // Set up data
    final String locationString = "Boston, MA;US;America/New_York;+4219-07105/";

    final Location location = LocationsListParser.parseLocation(locationString);
    final int year = 2014;
    final SunChartYearData sunChartYearData = SunChartUtility
      .createSunChartYear(location, year);

    // Create file names
    final String hexString = Integer
      .toHexString((int) (Math.random() * 100000));
    final File jpgFile = new File("./target",
                                  location.getDescription() + " " + hexString
                                              + ".jpg");
    final File dataFile = new File("./target",
                                   location.getDescription() + " " + hexString
                                               + ".txt");

    // Create Sun Chart JPEG file
    final SunChart sunChart = new SunChart(sunChartYearData);
    final BufferedImage image = sunChart
      .createBufferedImage(842, 595, BufferedImage.TYPE_INT_RGB, null);

    ImageIO.write(image, "jpg", jpgFile);
    System.out.println("Sun Chart JPEG:\n" + jpgFile.getAbsolutePath());

    // Create Sun Chart calculations file
    SunChartUtility
      .writeCalculations(location, new FileWriter(dataFile.getCanonicalPath()));
    System.out
      .println("Sun Chart Calculations:\n " + dataFile.getAbsolutePath());
  }

}
