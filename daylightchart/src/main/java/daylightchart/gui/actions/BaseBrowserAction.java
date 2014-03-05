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
package daylightchart.gui.actions;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;

import daylightchart.gui.util.BareBonesBrowserLaunch;
import daylightchart.gui.util.GuiAction;

abstract class BaseBrowserAction
  extends GuiAction
{

  private static final long serialVersionUID = 4002590686393404496L;

  BaseBrowserAction(final String text, final String url)
  {
    this(text, null, null, url);
  }

  BaseBrowserAction(final String text,
                    final String iconResource,
                    final String shortcutKey,
                    final String url)
  {
    super(text, iconResource);
    if (StringUtils.isNotBlank(shortcutKey))
    {
      setShortcutKey(KeyStroke.getKeyStroke(shortcutKey));
    }
    addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent actionevent)
      {
        BareBonesBrowserLaunch.openURL(url);
      }
    });
  }

}
