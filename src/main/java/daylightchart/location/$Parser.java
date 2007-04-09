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
package daylightchart.location;


import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import daylightchart.location.parser.LocationLexer;
import daylightchart.location.parser.LocationParser;

/**
 * Parses objects from strings.
 * 
 * @author Sualeh Fatehi
 */
public class $Parser
{
  
  /**
   * Parses a string representation of the coordinates.
   * 
   * @param representation
   *        String representation of the coordinates
   * @return Coordinates
   * @throws ParserException
   */
  public final static Coordinates parseCoordinates(final String representation)
    throws ParserException
  {
    final LocationParser parser = constructLocationParser(representation);
    Coordinates coordinates;
    try
    {
      coordinates = parser.coordinates();
    }
    catch (final RecognitionException e)
    {
      throw new ParserException(e);
    }
    catch (final TokenStreamException e)
    {
      throw new ParserException(e);
    }
    return coordinates;
  }

  /**
   * Parses a string representation of the location.
   * 
   * @param representation
   *        String representation of the location
   * @return Location
   * @throws ParserException
   */
  public final static Location parseLocation(final String representation)
    throws ParserException
  {
    final LocationParser parser = constructLocationParser(representation);
    Location location;
    try
    {
      location = parser.location();
    }
    catch (final RecognitionException e)
    {
      throw new ParserException(e);
    }
    catch (final TokenStreamException e)
    {
      throw new ParserException(e);
    }
    return location;
  }

  /**
   * Reads locations from a reader.
   * 
   * @param reader
   *        Reader
   * @return List of locations
   * @throws ParserException
   */
  public static List<Location> parseLocations(final Reader reader)
    throws ParserException
  {
    final LocationParser parser = constructLocationParser(reader);
    List<Location> locations;
    try
    {
      locations = parser.locations();
    }
    catch (final RecognitionException e)
    {
      throw new ParserException(e);
    }
    catch (final TokenStreamException e)
    {
      throw new ParserException(e);
    }
    return locations;
  }

  private static LocationParser constructLocationParser(final Reader reader)
  {
    final LocationLexer lexer = new LocationLexer(reader);
    final LocationParser parser = new LocationParser(lexer);
    return parser;
  }

  private static LocationParser constructLocationParser(final String text)
  {
    final StringReader reader = new StringReader(text);
    return constructLocationParser(reader);
  }

  private $Parser()
  {
  }

}
