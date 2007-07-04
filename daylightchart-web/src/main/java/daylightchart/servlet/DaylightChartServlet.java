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
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtilities;
import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;
import org.pointlocation6709.parser.CoordinateParser;
import org.pointlocation6709.parser.ParserException;

import daylightchart.chart.DaylightChart;
import daylightchart.location.Country;
import daylightchart.location.Location;
import daylightchart.location.parser.Countries;
import daylightchart.location.parser.DefaultTimezones;
import daylightchart.options.Options;

/**
 * Serves up Daylight Charts.
 * 
 * @author Sualeh Fatehi
 */
public class DaylightChartServlet
  extends HttpServlet
{

  private static final long serialVersionUID = 3184265489619614766L;

  @Override
  protected void doGet(final HttpServletRequest request,
                       final HttpServletResponse response)
    throws ServletException, IOException
  {
    doPost(request, response);
  }

  @Override
  protected void doPost(final HttpServletRequest request,
                        final HttpServletResponse response)
    throws ServletException, IOException
  {
    try
    {
      // Create the chart
      final Location location = getRequestedLocation(request);
      final DaylightChart daylightChart = new DaylightChart(location, Calendar
        .getInstance().get(Calendar.YEAR), new Options());

      // Get the image width
      int width;
      try
      {
        width = Integer.parseInt(request.getParameter("width"));
      }
      catch (final NumberFormatException e)
      {
        width = 700;
      }

      // Get the image height
      int height;
      try
      {
        height = Integer.parseInt(request.getParameter("height"));
      }
      catch (final NumberFormatException e)
      {
        height = 495;
      }

      response.setContentType("image/jpeg");
      ChartUtilities.writeChartAsJPEG(response.getOutputStream(),
                                      daylightChart,
                                      width,
                                      height);

    }
    catch (final RuntimeException e)
    {
      throw new ServletException(e);
    }

  }

  /**
   * Gets the location from the request.
   * 
   * @param request
   *        HTTP request
   * @return Location
   * @throws ServletException
   *         On an exception
   */
  private Location getRequestedLocation(final HttpServletRequest request)
    throws ServletException
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
      final Latitude latitude = CoordinateParser.parseLatitude(latitudeValue);

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
      final Longitude longitude = CoordinateParser
        .parseLongitude(longitudeValue);

      // Create the chart
      final PointLocation pointLocation = new PointLocation(latitude, longitude);
      final String timeZoneId = DefaultTimezones
        .attemptTimeZoneMatch(city, country, pointLocation.getLongitude());
      final Location location = new Location(city,
                                             country,
                                             timeZoneId,
                                             pointLocation);
      return location;
    }

    catch (final ParserException e)
    {
      throw new ServletException(e);
    }
  }

}
