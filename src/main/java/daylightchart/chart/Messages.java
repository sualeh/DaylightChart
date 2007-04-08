/*
 * Copyright (c) 2004-2007 Sualeh Fatehi. All Rights Reserved.
 */
package daylightchart.chart;


import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Message bundle accessor.
 * 
 * @author Sualeh Fatehi
 */
public class Messages
{
  private static final String BUNDLE_NAME = "daylightchart.chart.messages"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
    .getBundle(BUNDLE_NAME);

  /**
   * Gets a string from a resource bundle.
   * 
   * @param key
   *        Key to look up.
   * @return Strings
   */
  public static String getString(final String key)
  {
    try
    {
      return RESOURCE_BUNDLE.getString(key);
    }
    catch (final MissingResourceException e)
    {
      return '!' + key + '!';
    }
  }

  private Messages()
  {
  }
}
