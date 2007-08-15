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
package sf.util.ui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.EventListenerList;

import org.apache.commons.lang.StringUtils;

/**
 * An abstract action, which takes listeners.
 * 
 * @author sfatehi
 */
public class GuiChoiceAction
  implements Serializable
{

  private static final long serialVersionUID = 2667827621916360389L;

  private static final Logger LOGGER = Logger.getLogger(GuiChoiceAction.class
    .getName());

  protected String text;
  protected String description;
  protected Icon selectedIcon;
  protected Icon defaultIcon;
  protected boolean isSelected;
  protected ButtonGroup group;

  private final EventListenerList listeners = new EventListenerList();

  /**
   * Creates a new action.
   * 
   * @param text
   *        Text of the action
   * @param isSelected
   *        Whether this action is selected
   * @param group
   *        Button group
   */
  public GuiChoiceAction(final String text,
                         final boolean isSelected,
                         final ButtonGroup group)
  {
    this(text, null, null, isSelected, group);
  }

  /**
   * Creates a new action.
   * 
   * @param text
   *        Text of the action
   * @param selectedIconResource
   *        Selected icon
   * @param deselectedIconResource
   *        Deselected icon
   * @param isSelected
   *        Whether this action is the default
   * @param group
   *        Button group
   */
  public GuiChoiceAction(final String text,
                         final String selectedIconResource,
                         final String deselectedIconResource,
                         final boolean isSelected,
                         final ButtonGroup group)
  {
    if (StringUtils.isBlank(text))
    {
      throw new IllegalArgumentException("Text not provided");
    }
    this.text = text;
    description = text;
    if (StringUtils.isNotBlank(selectedIconResource))
    {
      selectedIcon = loadIcon(selectedIconResource);
    }
    else
    {
      selectedIcon = null;
    }
    if (StringUtils.isNotBlank(deselectedIconResource))
    {
      defaultIcon = loadIcon(deselectedIconResource);
    }
    else
    {
      defaultIcon = null;
    }
    this.isSelected = isSelected;
    this.group = group;
  }

  /**
   * Create an empty object, so that subclasses can set the fields.
   */
  protected GuiChoiceAction()
  {
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
      LOGGER.log(Level.WARNING, "Cannot perform action", ex);
    }
  }

  /**
   * Adds an item listener
   * 
   * @param l
   *        Listener
   */
  public void addItemListener(final ItemListener l)
  {
    listeners.add(ItemListener.class, l);
  }

  public Icon getDefaultIcon()
  {
    return defaultIcon;
  }

  public String getDescription()
  {
    return description;
  }

  public ButtonGroup getGroup()
  {
    return group;
  }

  public Icon getSelectedIcon()
  {
    return selectedIcon;
  }

  public String getText()
  {
    return text;
  }

  public boolean isSelected()
  {
    return isSelected;
  }

  public JRadioButtonMenuItem toMenuItem()
  {
    final JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(text,
                                                                   defaultIcon,
                                                                   isSelected);
    menuItem.setSelectedIcon(selectedIcon);
    for (final ItemListener itemListener: listeners
      .getListeners(ItemListener.class))
    {
      menuItem.addItemListener(itemListener);
    }
    group.add(menuItem);
    return menuItem;
  }

  /**
   * @param selectedIconResource
   * @return
   */
  protected Icon loadIcon(final String selectedIconResource)
  {
    return new ImageIcon(GuiChoiceAction.class
      .getResource(selectedIconResource));
  }

}
