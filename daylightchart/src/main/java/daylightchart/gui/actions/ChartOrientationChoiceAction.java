package daylightchart.gui.actions;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;

import sf.util.ui.GuiChoiceAction;
import daylightchart.chart.ChartOrientation;
import daylightchart.gui.Messages;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;

/**
 * Choice menu items for locations sort order.
 * 
 * @author sfatehi
 */
public class ChartOrientationChoiceAction
  extends GuiChoiceAction
{

  private static final class GuiActionListener
    implements ItemListener
  {
    private final ChartOrientation chartOrientation;

    private GuiActionListener(final ChartOrientation chartOrientation)
    {
      this.chartOrientation = chartOrientation;
    }

    public void itemStateChanged(final ItemEvent e)
    {
      if (e.getStateChange() == ItemEvent.SELECTED)
      {
        final Options options = UserPreferences.getOptions();
        options.setChartOrientation(chartOrientation);
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
    for (final ChartOrientation chartOrientation: ChartOrientation.values())
    {
      final ChartOrientationChoiceAction timeZoneOptionChoiceAction = new ChartOrientationChoiceAction(chartOrientation,
                                                                                                       group);
      menu.add(timeZoneOptionChoiceAction.toMenuItem());
    }
  }

  /**
   * Creates a locations sort order choice.
   * 
   * @param chartOrientation
   *        Chart orientation
   * @param group
   *        Button group
   */
  public ChartOrientationChoiceAction(final ChartOrientation chartOrientation,
                                      final ButtonGroup group)
  {
    if (chartOrientation == null)
    {
      throw new IllegalArgumentException("No time zone option specified");
    }

    isSelected = UserPreferences.getOptions().getChartOrientation() == chartOrientation;
    switch (chartOrientation)
    {
      case standard:
        text = Messages
          .getString("DaylightChartGui.Menu.Options.Orientation.Standard"); //$NON-NLS-1$        
        this.group = group;
        break;
      case conventional:
        text = Messages
          .getString("DaylightChartGui.Menu.Options.Orientation.Conventional"); //$NON-NLS-1$
        this.group = group;
        break;
      case vertical:
        text = Messages
          .getString("DaylightChartGui.Menu.Options.Orientation.Vertical"); //$NON-NLS-1$
        this.group = group;
        break;
    }

    addItemListener(new GuiActionListener(chartOrientation));
  }

}
