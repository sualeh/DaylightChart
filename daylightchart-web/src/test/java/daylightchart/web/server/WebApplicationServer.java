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


import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebApplicationServer
{

  private final int port;
  private final String webApplicationPath;
  private final Server server;

  public WebApplicationServer(final String webApplicationPath, final int port)
  {
    this.port = port;
    this.webApplicationPath = webApplicationPath;
    server = new Server();
  }

  public void start()
  {
    final SocketConnector connector = new SocketConnector();
    connector.setMaxIdleTime(1000 * 60 * 60);
    connector.setSoLingerTime(-1);
    connector.setPort(port);
    server.setConnectors(new Connector[] {
      connector
    });

    final WebAppContext webAppContext = new WebAppContext();
    webAppContext.setServer(server);
    webAppContext.setContextPath("/");
    webAppContext.setWar(webApplicationPath);

    server.addHandler(webAppContext);
    try
    {
      server.start();
    }
    catch (final Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  public void stop()
  {
    try
    {
      server.stop();
    }
    catch (final Exception e)
    {
      throw new RuntimeException(e);
    }
  }

}
