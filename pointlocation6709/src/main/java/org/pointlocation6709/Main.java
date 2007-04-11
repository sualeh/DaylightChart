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
package org.pointlocation6709;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.pointlocation6709.parser.ParserException;
import org.pointlocation6709.parser.PointLocationFormatter;
import org.pointlocation6709.parser.PointLocationParser;

/**
 * Main.
 * 
 * @author Sualeh Fatehi
 */
public final class Main
{

  /**
   * Main.
   * 
   * @param args
   *        Arguments
   * @throws IOException
   *         On an i/o error.
   */
  public static void main(final String[] args)
    throws IOException
  {
    System.out.println("Starting ISO 6709:1983 location point tester. "
                       + new Date());
    System.out.println("Enter a blank line to quit.");
    final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String inputLine = "starter";
    while (inputLine != null && inputLine.trim().length() > 0)
    {
      try
      {
        System.out.print("Enter an ISO 6709:1983 location point: ");
        inputLine = in.readLine();
        // Parse and print location point
        final PointLocation pointLocation = PointLocationParser
          .parsePointLocation(inputLine);
        System.out.println(pointLocation);
        System.out.println(PointLocationFormatter
          .formatIso6709PointLocation(pointLocation));
      }
      catch (final ParserException e)
      {
        System.err.println(e.getMessage());
      }
    }
    System.out.println("Done. " + new Date());
  }

  private Main()
  {
  }

}
