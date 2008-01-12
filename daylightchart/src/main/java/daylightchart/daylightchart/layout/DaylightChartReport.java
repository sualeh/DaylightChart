package daylightchart.daylightchart.layout;


import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.renderers.JFreeChartRenderer;
import daylightchart.daylightchart.calculation.RiseSetUtility;
import daylightchart.daylightchart.calculation.RiseSetYearData;
import daylightchart.daylightchart.chart.DaylightChart;
import daylightchart.location.Location;
import daylightchart.options.Options;

public class DaylightChartReport
{

  private final Location location;
  private final JasperPrint jasperPrint;

  public DaylightChartReport(final Location location, final Options options)
  {
    this.location = location;
    jasperPrint = renderDaylightChartReport(options);
  }

  public void write(final File file)
  {
    try
    {
      JasperExportManager.exportReportToHtmlFile(jasperPrint, file
        .getAbsolutePath());
    }
    catch (final JRException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private JasperPrint renderDaylightChartReport(final Options options)
  {
    try
    {
      // Calculate rise and set timings for the whole year, and generate
      // chart
      final RiseSetYearData riseSetData = RiseSetUtility
        .createRiseSetYear(location,
                           Calendar.getInstance().get(Calendar.YEAR),
                           options);
      final DaylightChart chart = new DaylightChart(riseSetData, options);
      options.getChartOptions().updateChart(chart);

      // Generate JasperReport for the chart
      // 1. Load compiled report
      final InputStream reportStream = DaylightChartReport.class
        .getResourceAsStream("/DaylightChartReport.jasper");
      final JasperReport jasperReport = (JasperReport) JRLoader
        .loadObject(reportStream);
      // 2. Prepare parameters
      final Map<String, Object> parameters = new HashMap<String, Object>();
      parameters.put("location", location);
      parameters.put("daylight_chart", new JFreeChartRenderer(chart));
      // 3. Create data set
      final JRDataSource dataSource = new JRBeanCollectionDataSource(riseSetData
        .getRiseSetData());

      // Render the report into PDF
      return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }
    catch (final JRException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }

}
