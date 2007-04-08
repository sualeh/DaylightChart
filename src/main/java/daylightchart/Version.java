/*
 * Copyright (c) 2004-2007 Sualeh Fatehi. All Rights Reserved.
 */
package daylightchart;


/**
 * Version information for this product. Has methods to obtain
 * information about the product, as well as a main method, so it can be
 * called from the command line.
 * 
 * @author Sualeh Fatehi
 */
public final class Version
{

  private final static String PRODUCTNAME = "Daylight Chart"; //$NON-NLS-1$
  private final static String VERSION = "0.6"; //$NON-NLS-1$

  /**
   * Information about this product.
   * 
   * @return Information about this product.
   */
  public static String about()
  {
    final StringBuffer about = new StringBuffer();
    about.append(getProductName()).append(" ").append(getVersion()) //$NON-NLS-1$
      .append("\n").append("\u00A9 2007 Sualeh Fatehi"); //$NON-NLS-1$ //$NON-NLS-2$
    return new String(about);
  }

  /**
   * Product name.
   * 
   * @return Product name.
   */
  public static String getProductName()
  {
    return PRODUCTNAME;
  }

  /**
   * Product version number.
   * 
   * @return Product version number.
   */
  public static String getVersion()
  {
    return VERSION;
  }

  /**
   * Main routine. Prints information about this product.
   * 
   * @param args
   *        Arguments to the main routine - they are ignored.
   */
  public static void main(final String[] args)
  {
    System.out.println(about());
  }

  private Version()
  {
  }

  /**
   * String representation. Information about this product.
   * 
   * @return String representation.
   */
  @Override
  public String toString()
  {

    return about();

  }

}
