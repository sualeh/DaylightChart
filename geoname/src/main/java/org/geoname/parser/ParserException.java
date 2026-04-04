/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.parser;

import java.io.Serial;

/** Parser exception. */
public class ParserException extends Exception {

  @Serial private static final long serialVersionUID = -8091140656979529951L;

  /** Constructor. */
  public ParserException() {}

  /**
   * Constructor.
   *
   * @param message Message
   */
  public ParserException(final String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message Message
   * @param cause Cause
   */
  public ParserException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param cause Cause
   */
  public ParserException(final Throwable cause) {
    super(cause);
  }
}
