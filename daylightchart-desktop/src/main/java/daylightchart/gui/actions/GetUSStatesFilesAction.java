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

public final class GetUSStatesFilesAction extends BaseBrowserAction {

  @Serial private static final long serialVersionUID = 4002590686393404496L;

  public GetUSStatesFilesAction() {
    super(
        Messages.getString("DaylightChartGui.Menu.Actions.GetUSStatesFiles"), // $NON-NLS-1$
        "https://edits.nationalmap.gov/apps/gaz-domestic/public/search/names" //$NON-NLS-1$
        );
  }
}
