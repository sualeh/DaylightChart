/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2013, Sualeh Fatehi.
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
package daylightchart.gui.util;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;

import org.apache.commons.lang.StringUtils;

/**
 * An abstract action, which takes listeners.
 * 
 * @author sfatehi
 */
public class GuiAction
  extends AbstractAction
{

  private static final long serialVersionUID = -5319269508462388520L;

  private static final Logger LOGGER = Logger.getLogger(GuiAction.class
    .getName());

  private final EventListenerList listeners = new EventListenerList();

  /**
   * Creates a new action.
   * 
   * @param text
   *        Text of the action
   */
  public GuiAction(final String text)
  {
    super(text);
    setDescription(text);
  }

  /**
   * Creates a new action.
   * 
   * @param text
   *        Text of the action
   * @param iconResource
   *        Icon
   */
  public GuiAction(final String text, final String iconResource)
  {
    this(text);

    if (StringUtils.isNotBlank(iconResource))
    {
      final URL resource = GuiAction.class.getResource(iconResource);
      if (resource != null)
      {
        setIcon(new ImageIcon(resource));
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(final ActionEvent e)
  {
    try
    {
      final ActionListener[] actionListeners = listeners
        .getListeners(ActionListener.class);
      for (final ActionListener actionListener: actionListeners)
      {
        actionListener.actionPerformed(e);
      }
    }
    catch (final Exception ex)
    {
      LOGGER.log(Level.WARNING, "Cannot perform action - "
                                + getValue(SHORT_DESCRIPTION), ex);
    }
  }

  /**
   * Adds an action listener
   * 
   * @param l
   *        Listener
   */
  public void addActionListener(final ActionListener l)
  {
    listeners.add(ActionListener.class, l);
  }

  /**
   * Gets the descripion.
   * 
   * @return Description
   */
  public String getDescription()
  {
    return getStringValue(NAME);
  }

  /**
   * Gets the icon.
   * 
   * @return Icon
   */
  public ImageIcon getIcon()
  {
    final Object value = getValue(SMALL_ICON);
    if (value == null || !(value instanceof ImageIcon))
    {
      return null;
    }
    else
    {
      return (ImageIcon) value;
    }
  }

  /**
   * Gets the shortcut key.
   * 
   * @return Shortcut key
   */
  public KeyStroke getShortcutKey()
  {
    final Object value = getValue(ACCELERATOR_KEY);
    if (value == null || !(value instanceof KeyStroke))
    {
      return null;
    }
    else
    {
      return (KeyStroke) value;
    }
  }

  /**
   * Gets the text.
   * 
   * @return text
   */
  public String getText()
  {
    return getStringValue(NAME);
  }

  /**
   * Gets the description.
   * 
   * @param description
   *        Description
   */
  public void setDescription(final String description)
  {
    putValue(SHORT_DESCRIPTION, description);
  }

  /**
   * Gets the icon.
   * 
   * @param icon
   *        Icon
   */
  public void setIcon(final ImageIcon icon)
  {
    putValue(SMALL_ICON, icon);
  }

  /**
   * Gets the shortcut key.
   * 
   * @param keyStroke
   *        Shortcut key
   */
  public void setShortcutKey(final KeyStroke keyStroke)
  {
    putValue(ACCELERATOR_KEY, keyStroke);
  }

  /**
   * Gets the text.
   * 
   * @param text
   *        Text
   */
  public void setText(final String text)
  {
    putValue(NAME, text);
  }

  private String getStringValue(final String key)
  {
    final Object value = getValue(key);
    if (value != null)
    {
      return value.toString();
    }
    else
    {
      return "";
    }
  }

}
