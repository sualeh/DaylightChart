header {
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
package daylightchart.location.parser;

import java.util.*;
import daylightchart.location.*;
import org.pointlocation6709.*;
import org.pointlocation6709.parser.*;

}

class AntlrLocationParser extends Parser;
options
{
  defaultErrorHandler = false;
}

locations
  returns [List<Location> locations = new ArrayList<Location>()]
  {
    Location location = null;
  }
  :
  (
  location = location
  {
    locations.add(location);
  }
  NEWLINE
  )+
  EOF
  ;

location
  returns [Location location = null]
  {
    String city;
    String country;
    TimeZone timeZone;
    PointLocation pointLocation;
  }
  :
  city = city
  FIELD_SEPARATOR
  country = country
  FIELD_SEPARATOR
  timeZone = timeZone
  FIELD_SEPARATOR
  pointLocation = pointLocation
  {
    location = new Location(
      city,
      country,
      timeZone.getID(),
      pointLocation
    );
  }
  ;

city
  returns [String value = ""]
  :
  text: TEXT_FIELD
  {
    value = text.getText();
  }
  ;

country
  returns [String value = ""]
  :
  text: TEXT_FIELD
  {
    value = text.getText();
  }
  ;

timeZone
  returns [TimeZone timeZone = null]
  :
  text: TEXT_FIELD
  {
    timeZone = TimeZone.getTimeZone(text.getText());
  }
  ;

pointLocation
  returns [PointLocation pointLocation = null]
  :
  text: TEXT_FIELD
  {
    try {
      pointLocation = PointLocationParser.parsePointLocation(text.getText());
    }
    catch (org.pointlocation6709.parser.ParserException e)
    {
      throw new RecognitionException(e.getMessage());
    }
  }
  ;

class AntlrLocationLexer extends Lexer;

options {
  k = 2; // Lookahead, needed for processing newlines
  charVocabulary = '\u0000'..'\u007F'; // Allow ASCII
}

TEXT_FIELD
  : (~(';' | '\r' | '\n'))+
  ;

FIELD_SEPARATOR
  : ';'
  ;

NEWLINE
  : ('\r''\n') => '\r''\n' // Windows
  | '\r'                   // Macintosh
  | '\n'                   // UNIX
  {
    newline();
    $setType(Token.SKIP);
  }
  ;
