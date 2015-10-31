/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2015, Sualeh Fatehi.
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
package sf.util;


import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Utility for the command line.
 * 
 * @author sfatehi
 */
public class CommandLineUtility
{

  /**
   * Sets the application-wide log level.
   * 
   * @param logLevel
   *        Log level to set
   */
  public static void setApplicationLogLevel(final Level logLevel)
  {
    final LogManager logManager = LogManager.getLogManager();
    for (final Enumeration<String> loggerNames = logManager.getLoggerNames(); loggerNames
      .hasMoreElements();)
    {
      final String loggerName = loggerNames.nextElement();
      final Logger logger = logManager.getLogger(loggerName);
      logger.setLevel(null);
      final Handler[] handlers = logger.getHandlers();
      for (final Handler handler: handlers)
      {
        handler.setLevel(logLevel);
      }
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(logLevel);
  }

  /**
   * Parses the command line, and sets the application log level.
   * 
   * @param args
   *        Command line arguments
   */
  public static void setLogLevel(final String[] args)
  {
    final String OPTION_LOG_LEVEL = "log-level";

    final CommandLineParser parser = new CommandLineParser();
    parser
      .addOption(new CommandLineParser.StringOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                    OPTION_LOG_LEVEL,
                                                    "OFF"));
    parser.parse(args);

    final String logLevelString = parser.getStringOptionValue(OPTION_LOG_LEVEL);
    final Level logLevel = Level.parse(logLevelString.toUpperCase());
    setApplicationLogLevel(logLevel);
  }

  private CommandLineUtility()
  {
    // Prevent instantiation
  }

}
