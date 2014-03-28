/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2014, Sualeh Fatehi.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package org.geoname.data;


import java.io.Serializable;

/**
 * Country, with ISO 3166 country code, and FIPS 10 country code.
 * 
 * @author Sualeh Fatehi
 */
public final class Country
  implements Serializable, Comparable<Country>
{

  /** Unknown country. */
  public static final Country UNKNOWN = new Country("", "");

  private static final long serialVersionUID = -5625327893850178062L;

  private final String name;
  private final String countryCode;

  /**
   * Constructor.
   * 
   * @param countryCode
   *        Two letter ISO 3166 country code
   * @param name
   *        Country name
   */
  public Country(final String countryCode, final String name)
  {
    this.name = name;
    this.countryCode = countryCode;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(final Country otherCountry)
  {
    return name.compareTo(otherCountry.name);
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
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final Country other = (Country) obj;
    if (name == null)
    {
      if (other.name != null)
      {
        return false;
      }
    }
    else if (!name.equals(other.name))
    {
      return false;
    }
    if (countryCode == null)
    {
      if (other.countryCode != null)
      {
        return false;
      }
    }
    else if (!countryCode.equals(other.countryCode))
    {
      return false;
    }
    return true;
  }

  /**
   * Gets the ISO 3166 2-letter country code.
   * 
   * @return ISO 3166 2-letter country code
   */
  public String getCode()
  {
    return countryCode;
  }

  /**
   * @return the country name
   */
  public String getName()
  {
    return name;
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
    int result = 1;
    result = prime * result + (name == null? 0: name.hashCode());
    result = prime * result + (countryCode == null? 0: countryCode.hashCode());
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
    return name;
  }

}
