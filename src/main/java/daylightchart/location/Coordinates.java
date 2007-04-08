/*
 * Copyright (c) 2004-2007 Sualeh Fatehi. All Rights Reserved.
 */
package daylightchart.location;


import java.io.Serializable;

/**
 * Coordinates (latitude and longitude) for a location. The latitude and
 * longitude in the format defined in ISO 6709:1983, "Standard
 * representation of latitude, longitude and altitude for geographic
 * point locations".
 * 
 * @author Sualeh Fatehi
 */
public final class Coordinates
  implements Serializable, Comparable<Coordinates>
{

  private static final long serialVersionUID = 648363972954435138L;

  private final Latitude latitude;
  private final Longitude longitude;

  /**
   * Copy constructor. Copies the value of a provided coordinates.
   * 
   * @param coordinates
   *        Location to copy the value from.
   * @throws NullPointerException
   *         If the argument is null
   */
  public Coordinates(final Coordinates coordinates)
  {
    this(coordinates.latitude, coordinates.longitude);
  }

  /**
   * Constructor.
   * 
   * @param latitude
   * @param longitude
   */
  public Coordinates(final Latitude latitude, final Longitude longitude)
  {
    if (latitude == null || longitude == null)
    {
      throw new IllegalArgumentException("Both latitude and longitude need to be specified");
    }
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(final Coordinates coordinates)
  {
    return toString().compareTo(coordinates.toString());
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (!(obj instanceof Coordinates)) return false;
    final Coordinates other = (Coordinates) obj;
    if (latitude == null)
    {
      if (other.latitude != null) return false;
    }
    else if (!latitude.equals(other.latitude)) return false;
    if (longitude == null)
    {
      if (other.longitude != null) return false;
    }
    else if (!longitude.equals(other.longitude)) return false;
    return true;
  }

  /**
   * Latitude for this location. Northern latitudes are positive.
   * 
   * @return Latitude.
   */
  public Latitude getLatitude()
  {
    return latitude;
  }

  /**
   * Longitude for this location. Eastern latitudes are positive.
   * 
   * @return Longitude.
   */
  public Longitude getLongitude()
  {
    return longitude;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((latitude == null)? 0: latitude.hashCode());
    result = prime * result + ((longitude == null)? 0: longitude.hashCode());
    return result;
  }

}
