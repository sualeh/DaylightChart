package daylightchart.gui.actions;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;

import sf.util.ui.GuiChoiceAction;
import daylightchart.chart.TimeZoneOption;
import daylightchart.gui.Messages;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;

/**
 * Choice menu items for locations sort order.
 * 
 * @author sfatehi
 */
public class TimeZoneOptionChoiceAction
  extends GuiChoiceAction
{

  private static final class GuiActionListener
    implements ItemListener
  {
    private final TimeZoneOption timeZoneOption;

    private GuiActionListener(final TimeZoneOption timeZoneOption)
    {
      this.timeZoneOption = timeZoneOption;
    }

    public void itemStateChanged(final ItemEvent e)
    {
      if (e.getStateChange() == ItemEvent.SELECTED)
      {
        final Options options = UserPreferences.getOptions();
        options.setTimeZoneOption(timeZoneOption);
        UserPreferences.setOptions(options);
      }
    }
  }

  private static final long serialVersionUID = -8217342421085173266L;

  /**
   * Adds all choices for the enum to the menu bar.
   * 
   * @param menu
   *        Menu to add to
   */
  public static void addAllToMenu(final JMenu menu)
  {
    if (menu == null)
    {
      throw new IllegalArgumentException("No menu specified");
    }

    final ButtonGroup group = new ButtonGroup();
    for (final TimeZoneOption timeZoneOption: TimeZoneOption.values())
    {
      final TimeZoneOptionChoiceAction timeZoneOptionChoiceAction = new TimeZoneOptionChoiceAction(timeZoneOption,
                                                                                                   group);
      menu.add(timeZoneOptionChoiceAction.toMenuItem());
    }
  }

  /**
   * Creates a locations sort order choice.
   * 
   * @param timeZoneOption
   *        Timezone option
   * @param group
   *        Button group
   */
  public TimeZoneOptionChoiceAction(final TimeZoneOption timeZoneOption,
                                    final ButtonGroup group)
  {
    if (timeZoneOption == null)
    {
      throw new IllegalArgumentException("No time zone option specified");
    }

    isSelected = UserPreferences.getOptions().getTimeZoneOption() == timeZoneOption;
    switch (timeZoneOption)
    {
      case USE_LOCAL_TIME:
        text = Messages.getString("DaylightChartGui.Menu.Options.UseLocalTime"); //$NON-NLS-1$        
        this.group = group;
        break;
      case USE_TIME_ZONE:
        text = Messages.getString("DaylightChartGui.Menu.Options.UseTimeZone"); //$NON-NLS-1$
        this.group = group;
        break;
    }

    addItemListener(new GuiActionListener(timeZoneOption));
  }

}
