/*
 *
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2016, Sualeh Fatehi.
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
package daylightchart.gui;


import static com.jgoodies.forms.layout.CellConstraints.BOTTOM;
import static com.jgoodies.forms.layout.CellConstraints.RIGHT;

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EnumSet;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.geoname.data.LocationsSortOrder;

import com.jgoodies.forms.builder.FormBuilder;

import daylightchart.daylightchart.calculation.TwilightType;
import daylightchart.daylightchart.chart.ChartOrientation;
import daylightchart.daylightchart.chart.TimeZoneOption;
import daylightchart.options.Options;

/**
 * This class is used for the display of the options.
 *
 * @author Sualeh Fatehi
 */
public class OptionsDialog
  extends JDialog
{

  final class DialogButtonListener
    implements ActionListener, KeyListener
  {
    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
      doAction(actionEvent);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(final KeyEvent keyEvent)
    {
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(final KeyEvent keyEvents)
    {
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(final KeyEvent keyEvent)
    {
      doAction(keyEvent);
    }

    private void doAction(final AWTEvent event)
    {
      final Object source = event.getSource();
      if (source == ok)
      {
        setOptionsFromDialog();
      }
      dispose();
    }

  }

  private static final long serialVersionUID = -8756602036694384557L;

  /**
   * Show a dialog for options.
   *
   * @param mainWindow
   *        Main Daylight Chart window
   * @param options
   *        Current options
   * @return Options
   */
  public static Options showOptionsDialog(final Frame mainWindow,
                                          final Options options)
  {
    final OptionsDialog dialog = new OptionsDialog(mainWindow, options);
    return dialog.getOptions();
  }

  private static <E extends Enum<E>> JComboBox<E> enumDropDown(final Class<E> e)
  {
    return new JComboBox<>(new Vector<E>(EnumSet.allOf(e)));
  }

  private Options options;
  private final JComboBox<LocationsSortOrder> listLocationsSortOrder;
  private final JComboBox<TimeZoneOption> listTimeZoneOption;
  private final JComboBox<ChartOrientation> listChartOrientation;
  private final JComboBox<TwilightType> listTwilightType;

  private final JCheckBox checkShowChartLegend;
  private final JButton ok;

  private final JButton cancel;

  private OptionsDialog(final Frame mainWindow, final Options options)
  {

    super(mainWindow, "Daylight Chart Options", true);
    disposeOnEscapeKey();

    listLocationsSortOrder = enumDropDown(LocationsSortOrder.class);
    listTimeZoneOption = enumDropDown(TimeZoneOption.class);
    listChartOrientation = enumDropDown(ChartOrientation.class);
    listTwilightType = enumDropDown(TwilightType.class);
    checkShowChartLegend = new JCheckBox();

    ok = new JButton(Messages.getString("DaylightChartGui.OptionsEditor.Ok"));
    cancel = new JButton(Messages
      .getString("DaylightChartGui.OptionsEditor.Cancel"));

    final DialogButtonListener listener = new DialogButtonListener();
    ok.addActionListener(listener);
    ok.addKeyListener(listener);
    cancel.addActionListener(listener);
    cancel.addKeyListener(listener);

    final FormBuilder builder = FormBuilder.create()
      .border(new EmptyBorder(15, 15, 15, 15)).columns("right:pref, 5dlu, pref")
      .rows("pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 10dlu, pref");

    builder
      .addLabel(Messages
        .getString("DaylightChartGui.OptionsEditor.LocationsSortOrder"))
      .labelFor(listLocationsSortOrder).rc(1, 1).add(listLocationsSortOrder)
      .rc(1, 3);

    builder
      .addLabel(Messages
        .getString("DaylightChartGui.OptionsEditor.TimeZoneOption"))
      .labelFor(listTimeZoneOption).rc(3, 1).add(listTimeZoneOption).rc(3, 3);

    builder
      .addLabel(Messages
        .getString("DaylightChartGui.OptionsEditor.ChartOrientation"))
      .labelFor(listChartOrientation).rc(5, 1).add(listChartOrientation)
      .rc(5, 3);

    builder
      .addLabel(Messages
        .getString("DaylightChartGui.OptionsEditor.TwilightType"))
      .labelFor(listTwilightType).rc(7, 1).add(listTwilightType).rc(7, 3);

    builder
      .addLabel(Messages.getString("DaylightChartGui.OptionsEditor.ShowLegend"))
      .labelFor(checkShowChartLegend).rc(9, 1).add(checkShowChartLegend)
      .rc(9, 3);

    builder.addBar(ok, cancel).rc(11, 3, BOTTOM, RIGHT);

    getContentPane().add(builder.getPanel());

    pack();
    setLocationRelativeTo(mainWindow);
    setResizable(false);

    setOptions(options);

    setVisible(true);
  }

  /**
   * Get the options from the dialog.
   *
   * @return Options from the dialog.
   */
  public Options getOptions()
  {
    return options;
  }

  private void disposeOnEscapeKey()
  {
    final Action action = new AbstractAction()
    {

      private static final long serialVersionUID = -180000433351276424L;

      @Override
      public void actionPerformed(final ActionEvent actionEvent)
      {
        dispose();
      }
    };
    final KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    rootPane.getActionMap().put(action, action);
    rootPane.getInputMap(JComponent.WHEN_FOCUSED).put(stroke, action);
  }

  private void setOptions(final Options options)
  {
    if (options == null)
    {
      throw new IllegalArgumentException("Cannot use null options");
    }

    listLocationsSortOrder.setSelectedItem(options.getLocationsSortOrder());
    listTimeZoneOption.setSelectedItem(options.getTimeZoneOption());
    listChartOrientation.setSelectedItem(options.getChartOrientation());
    listTwilightType.setSelectedItem(options.getTwilightType());
    checkShowChartLegend.setSelected(options.isShowChartLegend());

    this.options = options;
  }

  private void setOptionsFromDialog()
  {
    options.setLocationsSortOrder((LocationsSortOrder) listLocationsSortOrder
      .getSelectedItem());
    options
      .setTimeZoneOption((TimeZoneOption) listTimeZoneOption.getSelectedItem());
    options.setChartOrientation((ChartOrientation) listChartOrientation
      .getSelectedItem());
    options.setTwilightType((TwilightType) listTwilightType.getSelectedItem());
    options.setShowChartLegend(checkShowChartLegend.isSelected());
  }

}
