/*
 * Copyright (c) 2004-2007 Sualeh Fatehi. All Rights Reserved.
 */
package daylightchart.location;


/**
 * Represents a longitude in degrees or radians. The string
 * representation for longitude is comaptible with the format defined in
 * ISO 6709:1983, "Standard representation of latitude, longitude and
 * altitude for geographic point locations".
 * 
 * @author Sualeh Fatehi
 */
public final class Longitude
  extends Angle
{

  private static final long serialVersionUID = -8615691791807614256L;

  /**
   * Copy constructor. Copies the value of a provided angle.
   * 
   * @param angle
   *        Angle to copy the value from.
   */
  public Longitude(final Angle angle)
  {
    super(angle);
    validateDegreesRange(180);
  }

  @Override
  protected String getDirection()
  {
    if (getRadians() < 0)
    {
      return "W";
    }
    else
    {
      return "E";
    }
  }

}
