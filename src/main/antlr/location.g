header {
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
  )+
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
  NEWLINE
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
  returns [String text = ""]
  :
  text = text_field
  ;

country
  returns [String text = ""]
  :
  text = text_field
  ;

timeZone
  returns [TimeZone timeZone = null]
  {
    String text = "";
  }
  :
  text = text_field
  {
    timeZone = TimeZone.getTimeZone(text);
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
    coordinates = new Coordinates(
      latitude,
      longitude
    );
  }
  ;
  
latitude
  returns [Latitude latitude = null]
  {
    int sign = 1;
    double degrees = 0D;
    double minutes = 0D;
  }
  :
  (PLUS | MINUS { sign = -1; } )
  tens: DIGIT
  {
    degrees = degrees + 10 * Integer.parseInt(tens.getText());
  }
  units: DIGIT
  {
    degrees = degrees + Integer.parseInt(units.getText());
  }
  minutes = minutes
  {
    // Final calculation, and return
    degrees = sign * (degrees + minutes);
    latitude = new Latitude(Angle.fromDegrees(degrees));
  }
  ;

longitude
  returns [Longitude longitude = null]
  {
    int sign = 1;
    double degrees = 0D;
    double minutes = 0D;
  }
  :
  (PLUS | MINUS { sign = -1; } )
  hundreds: DIGIT
  {
    degrees = degrees + 100 * Integer.parseInt(hundreds.getText());
  }
  tens: DIGIT
  {
    degrees = degrees + 10 * Integer.parseInt(tens.getText());
  }
  units: DIGIT
  {
    degrees = degrees + Integer.parseInt(units.getText());
  }
  minutes = minutes
  {
    // Final calculation, and return
    degrees = sign * (degrees + minutes);
    longitude = new Longitude(Angle.fromDegrees(degrees));
  }
  ;

minutes
  returns [double minutes = 0D]
  :
    tens: DIGIT
    {
      minutes = minutes + 10 * Integer.parseInt(tens.getText());
    }
    units: DIGIT
    {
      minutes = minutes + Integer.parseInt(units.getText());
      minutes = minutes / 60D;
    }
  ;

text_field
  returns [String value = ""]
  :
  (
    word: WORD
    {
      value = value + word.getText();
    }
  (
    comma: COMMA
    {
      value = value + comma.getText();
    }
    |
    minus: MINUS
    {
      value = value + minus.getText();
    }
    |
    underscore: UNDERSCORE
    {
      value = value + underscore.getText();
    }
    |
    slash: SLASH
    {
      value = value + slash.getText();
    }
    |
    space: SPACE
    {
      value = value + space.getText();
    }
  )*
  )+
  SEPARATOR
  ;

class LocationLexer extends Lexer;

options {
  k=2; // Needed for newline
  charVocabulary='\u0000'..'\u007F'; // Allow ASCII
}

SEPARATOR
  : ';'
  ;

PLUS
  : '+'
  ;

MINUS
  : '-'
  ;

DIGIT
  : ('0'..'9')
  ;

NEWLINE
  : ( '\r' '\n' | '\n' | '\r' )
  ;

SLASH
  : '/'
  ;

UNDERSCORE
  :
  '_'
  ;

SPACE
  :
  ' '
  ;

COMMA
  :
  ','
  ;

WORD
  : (('A'..'Z') | ('a'..'z'))+
  ;
