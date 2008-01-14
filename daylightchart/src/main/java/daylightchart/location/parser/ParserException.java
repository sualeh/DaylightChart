/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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


/**
 * Parser exception.
 * 
 * @author Sualeh Fatehi
 */
public class ParserException
  extends Exception
{

  private static final long serialVersionUID = -8091140656979529951L;

  /**
   * Constructor.
   */
  public ParserException()
  {
  }

  /**
   * Constructor.
   * 
   * @param message
   *        Message
   */
  public ParserException(final String message)
  {
    super(message);
  }

  /**
   * Constructor.
   * 
   * @param message
   *        Message
   * @param cause
   *        Cause
   */
  public ParserException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  /**
   * Constructor.
   * 
   * @param cause
   *        Cause
   */
  public ParserException(final Throwable cause)
  {
    super(cause);
  }

}
