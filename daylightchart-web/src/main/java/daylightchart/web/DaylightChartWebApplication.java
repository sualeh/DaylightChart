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
package daylightchart.web;


import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.lang.PackageName;

import daylightchart.web.pages.LocationsPage;

public class DaylightChartWebApplication
  extends WebApplication
{

  @Override
  public Class getHomePage()
  {
    return LocationsPage.class;
  }

  @Override
  protected void init()
  {
    super.init();
    mount("/daylightchart", PackageName.forClass(LocationsPage.class));
  }

}
