/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package daylightchart.gui.actions;

import daylightchart.gui.util.BareBonesBrowserLaunch;
import daylightchart.gui.util.GuiAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import javax.swing.KeyStroke;
import org.apache.commons.lang3.StringUtils;

abstract class BaseBrowserAction extends GuiAction {

  @Serial private static final long serialVersionUID = 4002590686393404496L;

  BaseBrowserAction(final String text, final String url) {
    this(text, null, null, url);
  }

  BaseBrowserAction(
      final String text, final String iconResource, final String shortcutKey, final String url) {
    super(text, iconResource);
    if (StringUtils.isNotBlank(shortcutKey)) {
      setShortcutKey(KeyStroke.getKeyStroke(shortcutKey));
    }
    addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent actionevent) {
            BareBonesBrowserLaunch.openURL(url);
          }
        });
  }
}
