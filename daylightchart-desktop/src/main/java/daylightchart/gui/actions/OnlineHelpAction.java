/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.actions;

import daylightchart.gui.Messages;
import java.io.Serial;

/** Shows online help. */
public final class OnlineHelpAction extends BaseBrowserAction {

  @Serial private static final long serialVersionUID = 4002590686393404496L;

  /** Shows online help. */
  public OnlineHelpAction() {
    super(
        Messages.getString("DaylightChartGui.Menu.Help.Online"), // $NON-NLS-1$
        "/icons/help.gif", //$NON-NLS-1$
        "F1", //$NON-NLS-1$
        "http://sualeh.github.io/DaylightChart/howto.html" //$NON-NLS-1$
        );
  }
}
