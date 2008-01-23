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


import java.awt.event.ItemListener;

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
{

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
   * Adds an item listener
   * 
   * @param l
   *        Listener
   */
  public void addItemListener(final ItemListener l)
  {
    listeners.add(ItemListener.class, l);
  }

  /**
   * Returns the default icon.
   * 
   * @return Default <code>Icon</code>
   * @see javax.swing.AbstractButton#getIcon
   */
  public Icon getDefaultIcon()
  {
    return defaultIcon;
  }

  /**
   * Returns the description.
   * 
   * @return Description
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Returns the button group.
   * 
   * @return Button group
   */
  public ButtonGroup getGroup()
  {
    return group;
  }

  /**
   * Returns the selected icon.
   * 
   * @return Selected <code>Icon</code>
   * @see javax.swing.AbstractButton#getSelectedIcon()
   */
  public Icon getSelectedIcon()
  {
    return selectedIcon;
  }

  /**
   * Returns the text.
   * 
   * @return Text
   */
  public String getText()
  {
    return text;
  }

  /**
   * Returns the state of the button. True if the toggle button is
   * selected, false if it's not.
   * 
   * @return true if the toggle button is selected, otherwise false
   * @see javax.swing.AbstractButton#isSelected()
   */
  public boolean isSelected()
  {
    return isSelected;
  }

  /**
   * Converts the choice action definition into a radio button menu
   * item.
   * 
   * @return Radio button menu item equivalent
   */
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

  protected Icon loadIcon(final String selectedIconResource)
  {
    return new ImageIcon(GuiChoiceAction.class
      .getResource(selectedIconResource));
  }

}
