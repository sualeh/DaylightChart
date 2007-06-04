/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
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
package daylightchart.location;


import java.io.Serializable;

/**
 * Country, with ISO 3166 country code, and FIPS 10 country code.
 * 
 * @author Sualeh Fatehi
 */
public final class Country
  implements Serializable, Comparable<Country>
{

  public static final Country UNKNOWN = new Country("", "", "");

  private static final long serialVersionUID = -5625327893850178062L;
  private final String name;
  private final String iso3166Code2;

  private final String fips10Code;

  /**
   * Constructor.
   * 
   * @param name
   *        Country name
   * @param iso3166Code2
   *        Two letter ISO 3166 country code
   * @param fips10Code
   *        FIPS 10 country code
   */
  public Country(final String name,
                 final String iso3166Code2,
                 final String fips10Code)
  {
    this.name = name;
    this.iso3166Code2 = iso3166Code2;
    this.fips10Code = fips10Code;
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
    if (fips10Code == null)
    {
      if (other.fips10Code != null)
      {
        return false;
      }
    }
    else if (!fips10Code.equals(other.fips10Code))
    {
      return false;
    }
    if (iso3166Code2 == null)
    {
      if (other.iso3166Code2 != null)
      {
        return false;
      }
    }
    else if (!iso3166Code2.equals(other.iso3166Code2))
    {
      return false;
    }
    return true;
  }

  /**
   * Gets the FIPS 10 country code.
   * 
   * @return FIPS 10 country code
   */
  public String getFips10Code()
  {
    return fips10Code;
  }

  /**
   * Gets the ISO 3166 2-letter country code.
   * 
   * @return ISO 3166 2-letter country code
   */
  public String getIso3166Code2()
  {
    return iso3166Code2;
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
    result = prime * result + (fips10Code == null? 0: fips10Code.hashCode());
    result = prime * result
             + (iso3166Code2 == null? 0: iso3166Code2.hashCode());
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
