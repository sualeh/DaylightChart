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
 * US State, with codes from the Federal Information Processing
 * Standards Publication 5-2 - Codes For The Identification Of The
 * States, The District Of Columbia And The Outlying Areas Of The United
 * States, And Associated Areas.
 * 
 * @author Sualeh Fatehi
 */
public final class USState
  implements Serializable
{

  private static final long serialVersionUID = -5807680114125246988L;

  private final String name;
  private final String fips5_2AlphaCode;
  private final int fips5_2NumericCode;

  /**
   * Constructor.
   * 
   * @param name
   *        State name
   * @param fips5_2AlphaCode
   *        FIPS 5-2 Alpha Code
   * @param fips5_2NumericCode
   *        FIPS 5-2 Numeric Code
   */
  public USState(final String name,
                 final String fips5_2AlphaCode,
                 final int fips5_2NumericCode)
  {
    this.name = name;
    this.fips5_2AlphaCode = fips5_2AlphaCode;
    this.fips5_2NumericCode = fips5_2NumericCode;
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
    final USState other = (USState) obj;
    if (fips5_2AlphaCode == null)
    {
      if (other.fips5_2AlphaCode != null)
      {
        return false;
      }
    }
    else if (!fips5_2AlphaCode.equals(other.fips5_2AlphaCode))
    {
      return false;
    }
    if (fips5_2NumericCode != other.fips5_2NumericCode)
    {
      return false;
    }
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
    return true;
  }

  /**
   * Gets the FIPS 5-2 Alpha Code.
   * 
   * @return FIPS 5-2 Alpha Code
   */
  public String getFips5_2AlphaCode()
  {
    return fips5_2AlphaCode;
  }

  /**
   * Gets the FIPS 5-2 Numeric Code.
   * 
   * @return FIPS 5-2 Numeric Code
   */
  public int getFips5_2NumericCode()
  {
    return fips5_2NumericCode;
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
    result = prime * result
             + (fips5_2AlphaCode == null? 0: fips5_2AlphaCode.hashCode());
    result = prime * result + fips5_2NumericCode;
    result = prime * result + (name == null? 0: name.hashCode());
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
