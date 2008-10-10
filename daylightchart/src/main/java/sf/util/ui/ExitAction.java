/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
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
package sf.util.ui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 * Exits an application.
 * 
 * @author sfatehi
 */
public final class ExitAction
  extends GuiAction
{

  private static final long serialVersionUID = 5749903957626188378L;

  /**
   * Exits an application
   * 
   * @param frame
   *        Main window
   * @param text
   *        Text for the action
   */
  public ExitAction(final JFrame frame, final String text)
  {
    super(text, "/icons/exit.gif" //$NON-NLS-1$ 
    );
    setShortcutKey(KeyStroke.getKeyStroke("control Q"));
    addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused") final ActionEvent actionevent)
      {
        frame.dispose();
        System.exit(0);
      }
    });
  }

}
