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
package org.pointlocation6709.parser;


import java.io.Reader;
import java.io.StringReader;

import org.pointlocation6709.Angle;
import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;

import antlr.RecognitionException;
import antlr.TokenStreamException;

/**
 * Parses objects from strings.
 * 
 * @author Sualeh Fatehi
 */
public final class PointLocationParser
{

  /**
   * Parses a string representation of the latitude.
   * 
   * @param representation
   *        String representation of the point location
   * @return Latitude
   * @throws ParserException
   *         On an exception
   */
  public static Latitude parseLatitude(final String representation)
    throws ParserException
  {

    Latitude latitude;

    // 1. Attempt to parse as an angle
    try
    {
      latitude = new Latitude(Angle.fromDegrees(Double
        .parseDouble(representation)));
    }
    catch (final NumberFormatException e)
    {
      latitude = null;
    }

    // 2. Attempt to parse in ISO 6709 format
    if (latitude == null)
    {
      final AntlrPointLocationParser parser = constructPointLocationParser(representation);
      try
      {
        latitude = parser.latitude();
      }
      catch (final RecognitionException e)
      {
        throw new ParserException("Error parsing - " + representation, e);
      }
      catch (final TokenStreamException e)
      {
        throw new ParserException("Error parsing - " + representation, e);
      }
    }

    return latitude;
  }

  /**
   * Parses a string representation of the longitude.
   * 
   * @param representation
   *        String representation of the point location
   * @return Longitude
   * @throws ParserException
   *         On an exception
   */
  public static Longitude parseLongitude(final String representation)
    throws ParserException
  {

    Longitude longitude;

    // 1. Attempt to parse as an angle
    try
    {
      longitude = new Longitude(Angle.fromDegrees(Double
        .parseDouble(representation)));
    }
    catch (final NumberFormatException e)
    {
      longitude = null;
    }

    // 2. Attempt to parse in ISO 6709 format
    if (longitude == null)
    {
      final AntlrPointLocationParser parser = constructPointLocationParser(representation);
      try
      {
        longitude = parser.longitude();
      }
      catch (final RecognitionException e)
      {
        throw new ParserException("Error parsing - " + representation, e);
      }
      catch (final TokenStreamException e)
      {
        throw new ParserException("Error parsing - " + representation, e);
      }
    }

    return longitude;
  }

  /**
   * Parses a string representation of the point location.
   * 
   * @param representation
   *        String representation of the point location
   * @return Point location
   * @throws ParserException
   *         On an exception
   */
  public static PointLocation parsePointLocation(final String representation)
    throws ParserException
  {
    final AntlrPointLocationParser parser = constructPointLocationParser(representation);
    PointLocation pointLocation;
    try
    {
      pointLocation = parser.locationPoint();
    }
    catch (final RecognitionException e)
    {
      throw new ParserException("Error parsing - " + representation, e);
    }
    catch (final TokenStreamException e)
    {
      throw new ParserException("Error parsing - " + representation, e);
    }
    return pointLocation;
  }

  private static AntlrPointLocationParser constructPointLocationParser(final Reader reader)
  {
    final AntlrPointLocationLexer lexer = new AntlrPointLocationLexer(reader);
    final AntlrPointLocationParser parser = new AntlrPointLocationParser(lexer);
    return parser;
  }

  private static AntlrPointLocationParser constructPointLocationParser(final String text)
  {
    final StringReader reader = new StringReader(text);
    return constructPointLocationParser(reader);
  }

  private PointLocationParser()
  {
    // Prevent instantiation
  }
}
