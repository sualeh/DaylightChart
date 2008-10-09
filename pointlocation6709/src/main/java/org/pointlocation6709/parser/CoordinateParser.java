/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.pointlocation6709.Angle;
import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.Utility;

import antlr.RecognitionException;
import antlr.TokenStreamException;

/**
 * Parses objects from strings.
 * 
 * @author Sualeh Fatehi
 */
public final class CoordinateParser
{

  /**
   * Compass direction, for parsing.
   */
  private enum CompassDirection
  {
    N(1), S(-1), E(1), W(-1);

    private final int sign;

    private CompassDirection(int sign)
    {
      this.sign = sign;
    }

    /**
     * @return the sign
     */
    final int getSign()
    {
      return sign;
    }

  }

  /**
   * Parses a string representation of the latitude.
   * 
   * @param latitudeString
   *        String representation of the point location
   * @return Latitude
   * @throws ParserException
   *         On an exception
   */
  public static Latitude parseLatitude(final String latitudeString)
    throws ParserException
  {

    final String representation = cleanupCoordinate(latitudeString);
    Latitude latitude;

    // 1. Attempt to parse as an angle
    try
    {
      latitude = new Latitude(Angle.fromDegrees(Double
        .parseDouble(representation)));
    }
    catch (final RuntimeException e)
    {
      latitude = null;
    }

    // 2. Attempt to parse in ISO 6709 format
    if (latitude == null)
    {
      final AntlrPointLocationParser parser = PointLocationParser
        .constructPointLocationParser(representation + "+");
      try
      {
        latitude = parser.latitude();
      }
      catch (final RecognitionException e)
      {
        throw new ParserException("Error parsing \"" + representation + "\"", e);
      }
      catch (final TokenStreamException e)
      {
        throw new ParserException("Error parsing \"" + representation + "\"", e);
      }
    }

    return latitude;
  }

  /**
   * Parses a string representation of the longitude.
   * 
   * @param longitudeString
   *        String representation of the longitude
   * @return Longitude
   * @throws ParserException
   *         On an exception
   */
  public static Longitude parseLongitude(final String longitudeString)
    throws ParserException
  {

    final String representation = cleanupCoordinate(longitudeString);
    Longitude longitude;

    // 1. Attempt to parse as an angle
    try
    {
      longitude = new Longitude(Angle.fromDegrees(Double
        .parseDouble(representation)));
    }
    catch (final RuntimeException e)
    {
      longitude = null;
    }

    // 2. Attempt to parse in ISO 6709 format
    if (longitude == null)
    {
      final AntlrPointLocationParser parser = PointLocationParser
        .constructPointLocationParser(representation + "+");
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

  private static String cleanupCoordinate(final String coordinateString)
    throws ParserException
  {
    // Clean the representation, so that it can be parsed
    if (StringUtils.isBlank(coordinateString))
    {
      throw new ParserException("No value provided");
    }
    String representation = coordinateString.trim();

    boolean isIso6709Format = true;

    // Validate format
    final int countDegrees = StringUtils.countMatches(representation,
                                                      Angle.Field.DEGREES
                                                        .toString());
    final int countMinutes = StringUtils.countMatches(representation,
                                                      Angle.Field.MINUTES
                                                        .toString());
    final int countSeconds = StringUtils.countMatches(representation,
                                                      Angle.Field.SECONDS
                                                        .toString());
    if (countDegrees > 1 || countMinutes > 1 || countSeconds > 1)
    {
      throw new ParserException("Incorrectly formed angle - " +
                                coordinateString);
    }
    if (countDegrees > 0 || countMinutes > 0 || countSeconds > 0)
    {
      isIso6709Format = false;
    }

    representation = representation.replaceAll(Angle.Field.DEGREES.toString(),
                                               " ");
    representation = representation.replaceAll(Angle.Field.MINUTES.toString(),
                                               " ");
    representation = representation.replaceAll(Angle.Field.SECONDS.toString(),
                                               " ");

    final List<String> degreeParts = new ArrayList<String>(Arrays
      .asList(StringUtils.split(representation, ' ')));
    if (degreeParts.size() > 4)
    {
      throw new ParserException("Incorrectly formed angle - " +
                                coordinateString);
    }
    if (degreeParts.size() > 1)
    {
      isIso6709Format = false;
    }

    // Attempt to find the compass direction, and thus the sign of the
    // angle
    final String firstPart = degreeParts.get(0);
    final String lastPart = degreeParts.get(degreeParts.size() - 1);

    CompassDirection compassDirection;
    try
    {
      compassDirection = CompassDirection
        .valueOf(lastPart.trim().toUpperCase());
      isIso6709Format = false;
    }
    catch (final IllegalArgumentException e)
    {
      compassDirection = null;
    }

    final boolean hasSign = hasSign(firstPart);

    if (compassDirection != null && hasSign)
    {
      throw new ParserException("Corordinate cannot have a compass direction, as well as a signed angle");
    }

    int sign = 1;
    if (compassDirection != null)
    {
      sign = compassDirection.getSign();
    }
    if (hasSign)
    {
      try
      {
        sign = Integer.valueOf(firstPart.trim().substring(0, 1) + "1");
      }
      catch (NumberFormatException e)
      {
        sign = 1;
      }
    }

    // Remove the sign
    if (compassDirection != null)
    {
      degreeParts.remove(lastPart);
    }
    if (hasSign)
    {
      String stripSign = firstPart.trim().substring(1).trim();
      degreeParts.remove(0);
      degreeParts.add(0, stripSign);
      // Check that no other parts have signs
      for (final String degreePart: degreeParts)
      {
        if (hasSign(degreePart))
        {
          throw new ParserException("Cannot have signs on all degree parts");
        }
      }
    }

    if (isIso6709Format)
    {
      return coordinateString.trim();
    }
    else
    {
      // Parse all the numbers
      final List<Integer> angleFields = new ArrayList<Integer>();
      for (final String degreePart: degreeParts)
      {
        try
        {
          angleFields.add(Double.valueOf(degreePart.trim()).intValue());
        }
        catch (final NumberFormatException e)
        {
          throw new ParserException("Incorrectly formed angle - " +
                                    coordinateString);
        }
      }

      double value = Utility.sexagesimalCombine(angleFields);
      value = value * sign;

      return String.valueOf(value);
    }

  }

  private static boolean hasSign(final String string)
  {
    return string.trim().startsWith("+") || string.trim().startsWith("-");
  }

  private CoordinateParser()
  {
    // Prevent instantiation
  }

}
