package daylightchart.gui.actions;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;

import sf.util.ui.GuiChoiceAction;
import daylightchart.chart.Twilight;
import daylightchart.gui.Messages;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;

/**
 * Choice menu items for locations sort order.
 * 
 * @author sfatehi
 */
public class TwilightChoiceAction
  extends GuiChoiceAction
{

  private static final long serialVersionUID = -8217342421085173266L;

  /**
   * Creates a twilight choice.
   * 
   * @param twilight
   *        Chart orientation
   * @param group
   *        Button group
   */
  public TwilightChoiceAction(final Twilight twilight, final ButtonGroup group)
  {
    if (twilight == null)
    {
      throw new IllegalArgumentException("No twilight choice specified");
    }

    isSelected = UserPreferences.getOptions().getTwilight() == twilight;
    switch (twilight)
    {
      case none:
        text = Messages
          .getString("DaylightChartGui.Menu.Options.Twilight.None"); //$NON-NLS-1$        
        this.group = group;
        break;
      case civil:
        text = Messages
          .getString("DaylightChartGui.Menu.Options.Twilight.Civil"); //$NON-NLS-1$
        this.group = group;
        break;
      case nautical:
        text = Messages
          .getString("DaylightChartGui.Menu.Options.Twilight.Nautical"); //$NON-NLS-1$
        this.group = group;
        break;
      case astronomical:
        text = Messages
          .getString("DaylightChartGui.Menu.Options.Twilight.Astronomical"); //$NON-NLS-1$
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
          options.setTwilight(twilight);
          UserPreferences.setOptions(options);
        }
      }
    });
  }

  /**
   * Adds all choices for the enum to the menu bar.
   * 
   * @param menuBar
   *        Menu bar to add to
   */
  public static void addAllToMenu(final JMenu menu)
  {
    if (menu == null)
    {
      throw new IllegalArgumentException("No menu specified");
    }

    ButtonGroup group = new ButtonGroup();
    for (Twilight twilight: Twilight.values())
    {
      TwilightChoiceAction timeZoneOptionChoiceAction = new TwilightChoiceAction(twilight,
                                                                                 group);
      menu.add(timeZoneOptionChoiceAction.toMenuItem());
    }
  }

}
