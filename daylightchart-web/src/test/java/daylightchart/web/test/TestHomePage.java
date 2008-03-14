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
package daylightchart.web.test;


import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

import daylightchart.web.pages.LocationsPage;

public class TestHomePage
{

  private WicketTester tester;

  @Test
  public void renderHomePage()
  {
    // start and render the test page
    tester.startPage(LocationsPage.class);
  }

  @Before
  public void setUp()
  {
    tester = new WicketTester();
  }

}
