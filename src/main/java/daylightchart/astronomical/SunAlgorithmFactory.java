/*
 * Copyright (c) 2004-2007 Sualeh Fatehi. All Rights Reserved.
 */
package daylightchart.astronomical;


/**
 * Create an instance of a sunrise/ sunset algorithm.
 * 
 * @author Sualeh Fatehi
 */
public class SunAlgorithmFactory
{

  /**
   * Create an instance of a sunrise/ sunset algorithm.
   * 
   * @return Instance of a sunrise/ sunset algorithm.
   */
  public static SunAlgorithm getInstance()
  {
    return new CobbledSunCalc();
  }

  private SunAlgorithmFactory()
  {
    // Prevent instantiation
  }

}
