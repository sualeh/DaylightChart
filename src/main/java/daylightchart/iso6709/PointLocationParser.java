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
package daylightchart.iso6709;


import java.io.Reader;
import java.io.StringReader;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import daylightchart.iso6709.parser.AntlrPointLocationLexer;
import daylightchart.iso6709.parser.AntlrPointLocationParser;

/**
 * Parses objects from strings.
 * 
 * @author Sualeh Fatehi
 */
public class PointLocationParser
{

  /**
   * Parses a string representation of the point location.
   * 
   * @param representation
   *        String representation of the point location
   * @return Point location
   * @throws ParserException
   */
  public final static PointLocation parsePointLocation(final String representation)
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
      throw new ParserException(e);
    }
    catch (final TokenStreamException e)
    {
      throw new ParserException(e);
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
  }

}
