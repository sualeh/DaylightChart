/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** Message bundle accessor. */
public class Messages {
  private static final String BUNDLE_NAME = "daylightchart.gui.messages"; // $NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  /**
   * Gets a string from a resource bundle.
   *
   * @param key Key to look up.
   * @return Strings
   */
  public static String getString(final String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (final MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  private Messages() {}
}
