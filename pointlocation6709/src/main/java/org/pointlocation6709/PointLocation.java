/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package org.pointlocation6709;


import java.io.Serializable;

/**
 * Coordinates (latitude, longitude and altitude) for a location. The
 * latitude, longitude and altitude can be parsed from and formatted to
 * the format defined in ISO 6709:1983, "Standard representation of
 * latitude, longitude and altitude for geographic point locations".
 * 
 * @author Sualeh Fatehi
 */
public final class PointLocation
  implements Serializable, Comparable<PointLocation>
{

  private static final long serialVersionUID = 648363972954435138L;

  private final Latitude latitude;
  private final Longitude longitude;
  private final double altitude;

  /**
   * Constructor.
   * 
   * @param latitude
   *        Latitude
   * @param longitude
   *        Longitude
   */
  public PointLocation(final Latitude latitude, final Longitude longitude)
  {
    this(latitude, longitude, 0);
  }

  /**
   * Constructor.
   * 
   * @param latitude
   *        Latitude
   * @param longitude
   *        Longitude
   * @param altitude
   *        Altitude
   */
  public PointLocation(final Latitude latitude,
                       final Longitude longitude,
                       final double altitude)
  {
    if (latitude == null || longitude == null)
    {
      throw new IllegalArgumentException("Both latitude and longitude need to be specified");
    }
    this.latitude = latitude;
    this.longitude = longitude;
    this.altitude = altitude;
  }

  /**
   * Copy constructor. Copies the value of a provided point location.
   * 
   * @param pointLocation
   *        Location to copy the value from.
   */
  public PointLocation(final PointLocation pointLocation)
  {
    this(pointLocation.latitude,
         pointLocation.longitude,
         pointLocation.altitude);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(final PointLocation pointLocation)
  {
    int comparison;
    comparison = (int) Math.signum(altitude - pointLocation.altitude);
    if (comparison == 0)
    {
      comparison = latitude.compareTo(pointLocation.latitude);
    }
    if (comparison == 0)
    {
      comparison = longitude.compareTo(pointLocation.longitude);
    }
    return comparison;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (!super.equals(obj))
    {
      return false;
    }
    if (!(obj instanceof PointLocation))
    {
      return false;
    }
    final PointLocation other = (PointLocation) obj;
    if (Double.doubleToLongBits(altitude) != Double
      .doubleToLongBits(other.altitude))
    {
      return false;
    }
    if (latitude == null)
    {
      if (other.latitude != null)
      {
        return false;
      }
    }
    else if (!latitude.equals(other.latitude))
    {
      return false;
    }
    if (longitude == null)
    {
      if (other.longitude != null)
      {
        return false;
      }
    }
    else if (!longitude.equals(other.longitude))
    {
      return false;
    }
    return true;
  }

  /**
   * Altitude for this location, in meters.
   * 
   * @return the altitude
   */
  public double getAltitude()
  {
    return altitude;
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
    long temp;
    temp = Double.doubleToLongBits(altitude);
    result = prime * result + (int) (temp ^ temp >>> 32);
    result = prime * result + (latitude == null? 0: latitude.hashCode());
    result = prime * result + (longitude == null? 0: longitude.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String string = latitude.toString() + " - " + longitude.toString();
    if (altitude != 0)
    {
      string = string + ": " + altitude;
    }
    return string;
  }

}