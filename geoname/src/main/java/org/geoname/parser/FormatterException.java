/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.parser;

import java.io.Serial;

/** Formatter exception. */
public class FormatterException extends Exception {

  @Serial private static final long serialVersionUID = -8646676654554219081L;

  /** Constructor. */
  public FormatterException() {}

  /**
   * Constructor.
   *
   * @param message Message
   */
  public FormatterException(final String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message Message
   * @param cause Cause
   */
  public FormatterException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param cause Cause
   */
  public FormatterException(final Throwable cause) {
    super(cause);
  }
}
