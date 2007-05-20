/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package daylightchart.servlet;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtilities;
import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;
import org.pointlocation6709.parser.ParserException;
import org.pointlocation6709.parser.PointLocationParser;

import daylightchart.chart.DaylightChart;
import daylightchart.location.Country;
import daylightchart.location.Location;
import daylightchart.location.parser.Countries;
import daylightchart.location.parser.DefaultTimezones;

/**
 * Serves up Daylight Charts.
 * 
 * @author Sualeh Fatehi
 */
public class DaylightChartServlet
  extends HttpServlet
{

  private static final long serialVersionUID = 3184265489619614766L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    doPost(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {

    try
    {

      // Get the city
      String city = request.getParameter("city");
      if (city == null || city.trim().length() == 0)
      {
        city = "";
      }

      // Get the country
      String countryString = request.getParameter("country");
      if (countryString == null || countryString.trim().length() == 0)
      {
        countryString = "";
      }
      Country country = Countries.lookupCountry(countryString);
      if (country == null)
      {
        country = Country.UNKNOWN;
      }

      // Get the latitude
      String latitudeValue = request.getParameter("latitude");
      if (latitudeValue == null || latitudeValue.length() == 0)
      {
        latitudeValue = request.getParameter("lat");
      }
      if (latitudeValue == null || latitudeValue.length() == 0)
      {
        throw new ServletException("Latitude not provided");
      }
      else
      {
        latitudeValue = latitudeValue.trim();
      }
      if (!latitudeValue.matches("[+-].*"))
      {
        latitudeValue = "+" + latitudeValue;
      }
      Latitude latitude = PointLocationParser.parseLatitude(latitudeValue);

      // Get the longitude
      String longitudeValue = request.getParameter("longitude");
      if (longitudeValue == null || longitudeValue.length() == 0)
      {
        longitudeValue = request.getParameter("lng");
      }
      if (longitudeValue == null || longitudeValue.length() == 0)
      {
        throw new ServletException("Longitude not provided");
      }
      else
      {
        longitudeValue = longitudeValue.trim();
      }
      if (!longitudeValue.matches("[+-].*"))
      {
        longitudeValue = "+" + longitudeValue;
      }
      Longitude longitude = PointLocationParser.parseLongitude(longitudeValue);

      // Create the chart
      PointLocation pointLocation = new PointLocation(latitude, longitude);
      String timeZoneId = DefaultTimezones
        .attemptTimeZoneMatch(city, country, pointLocation.getLongitude());
      Location location = new Location(city, country, timeZoneId, pointLocation);

      DaylightChart daylightChart = new DaylightChart(location);

      response.setContentType("image/jpeg");
      ChartUtilities.writeChartAsJPEG(response.getOutputStream(),
                                      daylightChart,
                                      700,
                                      495);

    }
    catch (ParserException e)
    {
      throw new ServletException(e);
    }
  }
}
