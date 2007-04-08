/*
 * Copyright (c) 2004-2007 Sualeh Fatehi. All Rights Reserved.
 */
package daylightchart.location;


/**
 * Represents a latitude in degrees or radians. The string
 * representation for latitude is comaptible with the format defined in
 * ISO 6709:1983, "Standard representation of latitude, longitude and
 * altitude for geographic point locations".
 * 
 * @author Sualeh Fatehi
 */
public final class Latitude
  extends Angle
{

  private static final long serialVersionUID = -1048509855080052523L;

  /**
   * Copy constructor. Copies the value of a provided angle.
   * 
   * @param angle
   *        Angle to copy the value from.
   */
  public Latitude(final Angle angle)
  {
    super(angle);
    validateDegreesRange(90);
  }

  @Override
  protected String getDirection()
  {
    if (getRadians() < 0)
    {
      return "S";
    }
    else
    {
      return "N";
    }
  }

}
