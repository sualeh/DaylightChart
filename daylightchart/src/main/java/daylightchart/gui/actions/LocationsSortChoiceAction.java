package daylightchart.gui.actions;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;

import sf.util.ui.GuiChoiceAction;
import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.Messages;
import daylightchart.location.LocationsSortOrder;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;

/**
 * Choice menu items for locations sort order.
 * 
 * @author sfatehi
 */
public class LocationsSortChoiceAction
  extends GuiChoiceAction
{

  private static final long serialVersionUID = -8217342421085173266L;

  /**
   * Creates a locations sort order choice.
   * 
   * @param mainWindow
   *        Main GUI window
   * @param locationsSortOrder
   *        Locations sort
   * @param group
   *        Button group
   */
  public LocationsSortChoiceAction(final DaylightChartGui mainWindow,
                                   final LocationsSortOrder locationsSortOrder,
                                   final ButtonGroup group)
  {
    if (locationsSortOrder == null)
    {
      throw new IllegalArgumentException("No locations sort order specified");
    }

    isSelected = UserPreferences.getOptions().getLocationsSortOrder() == locationsSortOrder;
    switch (locationsSortOrder)
    {
      case BY_LATITUDE:
        text = Messages
          .getString("DaylightChartGui.Menu.Options.SortByLatitude"); //$NON-NLS-1$
        defaultIcon = loadIcon("/icons/sort_by_latitude.gif"); //$NON-NLS-1$
        selectedIcon = loadIcon("/icons/sort_by_latitude_dim.gif"); //$NON-NLS-1$        
        this.group = group;
        break;
      case BY_NAME:
        text = Messages.getString("DaylightChartGui.Menu.Options.SortByName"); //$NON-NLS-1$
        defaultIcon = loadIcon("/icons/sort_by_name.gif"); //$NON-NLS-1$
        selectedIcon = loadIcon("/icons/sort_by_name_dim.gif"); //$NON-NLS-1$
        this.group = group;
        break;
    }

    addItemListener(new ItemListener()
    {
      public void itemStateChanged(final ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
          final Options options = UserPreferences.getOptions();
          options.setLocationsSortOrder(locationsSortOrder);
          UserPreferences.setOptions(options);

          mainWindow.sortLocations();
        }
      }
    });
  }

  /**
   * Adds all choices for the enum to the menu bar.
   * 
   * @param mainWindow
   *        Main GUI window
   * @param menuBar
   *        Menu bar to add to
   */
  public static void addAllToMenu(final DaylightChartGui mainWindow,
                                  final JMenu menu)
  {
    if (menu == null)
    {
      throw new IllegalArgumentException("No menu specified");
    }

    ButtonGroup group = new ButtonGroup();
    for (LocationsSortOrder locationsSortOrder: LocationsSortOrder.values())
    {
      LocationsSortChoiceAction locationsSortChoiceAction = new LocationsSortChoiceAction(mainWindow,
                                                                                          locationsSortOrder,
                                                                                          group);
      menu.add(locationsSortChoiceAction.toMenuItem());
    }
  }

}
