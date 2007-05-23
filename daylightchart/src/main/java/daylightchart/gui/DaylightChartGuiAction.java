package daylightchart.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.event.EventListenerList;

class DaylightChartGuiAction
  extends AbstractAction
{

  private static final long serialVersionUID = -5319269508462388520L;

  private static final Logger LOGGER = Logger
    .getLogger(DaylightChartGuiAction.class.getName());

  private final EventListenerList listeners = new EventListenerList();

  /**
   * Creates a new action.
   * 
   * @param text
   *        Text of the action
   * @param icon
   *        Icon
   * @param description
   *        description
   */
  public DaylightChartGuiAction(final String text,
                                final Icon icon,
                                final String description)
  {
    super(text, icon);
    putValue(ACTION_COMMAND_KEY, text);
    putValue(SHORT_DESCRIPTION, description);
  }

  /**
   * Creates a new action.
   * 
   * @param text
   *        Text of the action
   * @param description
   *        description
   */
  public DaylightChartGuiAction(final String text, final String description)
  {
    super(text);
    putValue(SHORT_DESCRIPTION, description);
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

}
