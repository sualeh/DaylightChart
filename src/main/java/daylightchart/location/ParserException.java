/*
 * Copyright (c) 2004-2007 Sualeh Fatehi. All Rights Reserved.
 */
package daylightchart.location;


/**
 * @author Sualeh Fatehi
 */
public class ParserException
  extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = -8091140656979529951L;

  /**
   * Constructor.
   */
  public ParserException()
  {
  }

  /**
   * Constructor.
   * 
   * @param message
   */
  public ParserException(final String message)
  {
    super(message);
  }

  /**
   * Constructor.
   * 
   * @param message
   * @param cause
   */
  public ParserException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  /**
   * Constructor.
   * 
   * @param cause
   */
  public ParserException(final Throwable cause)
  {
    super(cause);
  }

}
