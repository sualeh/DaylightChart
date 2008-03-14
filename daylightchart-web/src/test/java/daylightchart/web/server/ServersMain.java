/*
 * Copyright 2007-2008, Sualeh Fatehi <sualeh@hotmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution 3.0 License. 
 * To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by/3.0/ 
 * or send a letter to 
 * Creative Commons
 * 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
package daylightchart.web.server;



public class ServersMain
{

  public static void main(final String[] args)
  {
    try
    {
      final String webApplicationPath = args[0];
      final int port = Integer.parseInt(args[1]);
      final WebApplicationServer webApplicationServer = new WebApplicationServer(webApplicationPath,
                                                                                 port);
      webApplicationServer.start();

      while (System.in.available() == 0)
      {
        Thread.sleep(5000);
      }
      webApplicationServer.stop();
    }
    catch (final Exception e)
    {
      e.printStackTrace();
      System.exit(100);
    }
  }

}
