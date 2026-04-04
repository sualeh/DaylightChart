/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart;

/**
 * Version information for this product. Has methods to obtain information about the product, as
 * well as a main method, so it can be called from the command line.
 */
public final class Version {

  private static final String PRODUCTNAME = "Daylight Chart"; // $NON-NLS-1$
  private static final String VERSION = "5.0.2"; // $NON-NLS-1$

  /**
   * Information about this product.
   *
   * @return Information about this product.
   */
  public static String about() {
    final StringBuffer about = new StringBuffer();
    about
        .append(getProductName())
        .append(" ")
        .append(getVersion()) // $NON-NLS-1$
        .append("\n")
        .append("\u00A9 2007-2026, Sualeh Fatehi."); // $NON-NLS-1$
    return new String(about);
  }

  /**
   * Product name.
   *
   * @return Product name.
   */
  public static String getProductName() {
    return PRODUCTNAME;
  }

  /**
   * Product version number.
   *
   * @return Product version number.
   */
  public static String getVersion() {
    return VERSION;
  }

  /**
   * Main routine. Prints information about this product.
   *
   * @param args Arguments to the main routine - they are ignored.
   */
  public static void main(final String[] args) {
    System.out.println(about());
  }

  private Version() {}

  /**
   * String representation. Information about this product.
   *
   * @return String representation.
   */
  @Override
  public String toString() {

    return about();
  }
}
