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

}

class LocationParser extends Parser;

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
    Coordinates coordinates;
  }
  :
  city = city
  country = country
  timeZone = timeZone
  coordinates = coordinates
  {
    location = new Location(
      city,
      country,
      timeZone.getID(),
      coordinates
    );
  }
  ;

city
  returns [String value = ""]
  :
  text: TEXT
  {
    value = text.getText();
  }
  ;

country
  returns [String value = ""]
  :
  text: TEXT
  {
    value = text.getText();
  }
  ;

timeZone
  returns [TimeZone timeZone = null]
  {
    String value = "";
  }
  :
  text: TEXT
  {
    timeZone = TimeZone.getTimeZone(text.getText());
  }
  ;

coordinates
  returns [Coordinates coordinates = null]
  {
    Latitude latitude;
    Longitude longitude;
  }
  :
  latitude = latitude
  longitude = longitude
  SLASH
  {
    coordinates = new Coordinates(latitude, longitude);
  }
  ;

latitude
  returns [Latitude latitude = null]
  {
    int sign = 1;
    double degrees = 0;
    double minutesAndSeconds = 0;
  }
  :
  (PLUS | MINUS { sign = -1; })
  (
    (DIGIT DIGIT DECIMAL_POINT DIGIT) =>
    (
      degrees = realNumber
      {
        latitude = new Latitude(Angle.fromDegrees(sign * degrees));
      }
    )
    |
    (
      degrees = twoDigitInteger
      (minutesAndSeconds = minutesAndSeconds)?
      {
        latitude = new Latitude(Angle.fromDegrees(sign * (degrees + minutesAndSeconds)));
      }
    )
  )
  ;

longitude
  returns [Longitude longitude = null]
  {
    int sign = 1;
    double degrees = 0;
    double minutesAndSeconds = 0;
  }
  :
  (PLUS | MINUS { sign = -1; })
  (
    (DIGIT DIGIT DIGIT DECIMAL_POINT DIGIT) =>
    (
      degrees = realNumber
      {
        longitude = new Longitude(Angle.fromDegrees(sign * degrees));
      }
    )
    |
    (
      degrees = threeDigitInteger
      (minutesAndSeconds = minutesAndSeconds)?
      {
        longitude = new Longitude(Angle.fromDegrees(sign * (degrees + minutesAndSeconds)));
      }
    )
  )
  ;

minutesAndSeconds
  returns [double value = 0D]
  {
    double minutes = 0;
    double seconds = 0;
  }
  :
    (DIGIT DIGIT DECIMAL_POINT DIGIT) =>
    (
    minutes = realNumber
    {
      value = value + minutes / 60D;
    }
    )
    |
    (
      (
      minutes = twoDigitInteger
      {
        value = value + minutes / 60D;
      }
      )
      (
        (DIGIT DIGIT DECIMAL_POINT DIGIT) =>
        (
        seconds = realNumber
        {
          value = value + seconds / 3600D;
        }
        )
        |
        (
        seconds = twoDigitInteger
        {
          value = value + seconds / 3600D;
        }
        )?
      )
    )
  ;

twoDigitInteger
  returns [int number = 0]
  :
    tens: DIGIT
    {
      number = number + 10 * Integer.parseInt(tens.getText());
    }
    units: DIGIT
    {
      number = number + Integer.parseInt(units.getText());
    }
  ;

threeDigitInteger
  returns [int number = 0]
  :
    hundreds: DIGIT
    {
      number = number + 100 * Integer.parseInt(hundreds.getText());
    }
    tens: DIGIT
    {
      number = number + 10 * Integer.parseInt(tens.getText());
    }
    units: DIGIT
    {
      number = number + Integer.parseInt(units.getText());
    }
  ;

realNumber
  returns [double number = 0D]
  {
    int digits = 0;
    int decimalDigits = 1;
  }
  :
    (
    digit: DIGIT
    {
      int intDigit = Integer.parseInt(digit.getText());
      if (!(intDigit == 0 && number == 0)) { // Ignore leading zeros
        number = number * Math.pow(10, digits) + intDigit;
        digits++;
      }
    }
    )+
    DECIMAL_POINT
    (
    decimalDigit: DIGIT
    {
      number = number + Math.pow(10, -decimalDigits) * Integer.parseInt(decimalDigit.getText());
      decimalDigits++;
    }
    )+
  ;

class LocationLexer extends Lexer;

options {
  k = 2; // Lookahead, needed for processing newlines
  charVocabulary = '\u0000'..'\u007F'; // Allow ASCII
}

TEXT
  : (~(';'|'\r'|'\n'|'0'..'9'))+ ';'!
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

PLUS
  : '+'
  ;

MINUS
  : '-'
  ;

DIGIT
  : '0'..'9'
  ;

DECIMAL_POINT
  : '.'
  ;

SLASH
  : '/'
  ;
