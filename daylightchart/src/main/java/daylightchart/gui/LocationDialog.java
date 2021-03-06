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
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.ZoneId;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.Location;
import org.geoname.parser.DefaultTimezones;
import org.geoname.parser.TimeZoneDisplay;

import com.jgoodies.forms.builder.FormBuilder;

import daylightchart.gui.actions.LocationsListOperation;
import us.fatehi.pointlocation6709.Latitude;
import us.fatehi.pointlocation6709.Longitude;
import us.fatehi.pointlocation6709.PointLocation;
import us.fatehi.pointlocation6709.parse.CoordinateParser;

/**
 * This class is used for the display of the location editor. The user
 * is allowed to add new locations, and delete or modify existing
 * locations.
 *
 * @author Pradnya Devare, Sualeh Fatehi
 */
public class LocationDialog
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
        if (!isCurrentLocationValid())
        {
          return;
        }
        dialogNotCancelled = true;
      }
      dispose();
    }
  }

  private static final long serialVersionUID = -5161588534167787490L;

  private static final Logger LOGGER = Logger
    .getLogger(LocationDialog.class.getName());

  /**
   * Show a dialog for location delete, add or edit operation to be
   * performed.
   *
   * @param mainWindow
   *        Main window
   * @param operation
   *        Operation to perform
   * @return Location, if one was added, edited or deleted, or null if
   *         the dialog was canceled
   */
  public static Location showLocationDialog(final DaylightChartGui mainWindow,
                                            final LocationsListOperation operation)
  {
    final LocationDialog locationDialog = new LocationDialog(mainWindow,
                                                             operation);
    if (locationDialog.dialogNotCancelled)
    {
      return locationDialog.getCurrentLocation();
    }
    else
    {
      return null;
    }
  }

  private final LocationsListOperation operation;

  private final JTextField city;
  private final JComboBox<Country> countries;
  private final JTextField latitudeValue;
  private final JTextField longitudeValue;
  private final JComboBox<TimeZoneDisplay> timeZone;

  private final JButton ok;
  private final JButton cancel;

  private boolean dialogNotCancelled;

  private LocationDialog(final DaylightChartGui mainWindow,
                         final LocationsListOperation operation)
  {

    super(mainWindow, operation.getText(), true);
    disposeOnEscapeKey();

    this.operation = operation;

    city = new JTextField();
    countries = new JComboBox<>(new Vector<Country>(Countries
      .getAllCountries()));
    latitudeValue = new JTextField();
    longitudeValue = new JTextField();
    timeZone = new JComboBox<>(new Vector<TimeZoneDisplay>(DefaultTimezones
      .getAllTimeZonesForDisplay()));

    ok = new JButton(Messages.getString("DaylightChartGui.LocationEditor.Ok")); //$NON-NLS-1$
    cancel = new JButton(Messages
      .getString("DaylightChartGui.LocationEditor.Cancel")); //$NON-NLS-1$

    final DialogButtonListener listener = new DialogButtonListener();
    ok.addActionListener(listener);
    ok.addKeyListener(listener);
    cancel.addActionListener(listener);
    cancel.addKeyListener(listener);

    final FocusListener focusListener = new FocusListener()
    {
      @Override
      public void focusGained(final FocusEvent e)
      {
      }

      @Override
      public void focusLost(final FocusEvent event)
      {
        if (!event.isTemporary())
        {
          if (event.getOppositeComponent().equals(cancel))
          {
            return;
          }

          final Component focusComponent = event.getComponent();
          if (focusComponent.equals(latitudeValue))
          {
            setLatitude(getLatitude());
          }
          else if (focusComponent.equals(longitudeValue))
          {
            setLongitude(getLongitude());
          }

          if (isCurrentLocationValid())
          {
            final String timeZoneId = DefaultTimezones
              .attemptTimeZoneMatch(getCity(), getCountry(), getLongitude());
            setTimeZoneId(timeZoneId);
          }
        }
      }
    };

    latitudeValue.addFocusListener(focusListener);
    longitudeValue.addFocusListener(focusListener);
    city.addFocusListener(focusListener);
    countries.addFocusListener(focusListener);

    final FormBuilder builder = FormBuilder.create()
      .border(new EmptyBorder(15, 15, 15, 15)).columns("right:pref, 5dlu, pref")
      .rows("pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 10dlu, pref");

    builder.addLabel(Messages.getString("DaylightChartGui.LocationEditor.City"))
      .labelFor(city).rc(1, 1).add(city).rc(1, 3);

    builder
      .addLabel(Messages.getString("DaylightChartGui.LocationEditor.Country"))
      .labelFor(countries).rc(3, 1).add(countries).rc(3, 3);

    builder
      .addLabel(Messages.getString("DaylightChartGui.LocationEditor.Latitude"))
      .labelFor(latitudeValue).rc(5, 1).add(latitudeValue).rc(5, 3);

    builder
      .addLabel(Messages.getString("DaylightChartGui.LocationEditor.Longitude"))
      .labelFor(longitudeValue).rc(7, 1).add(longitudeValue).rc(7, 3);

    builder
      .addLabel(Messages.getString("DaylightChartGui.LocationEditor.TimeZone"))
      .labelFor(timeZone).rc(9, 1).add(timeZone).rc(9, 3);

    builder.addBar(ok, cancel).rc(11, 3, BOTTOM, RIGHT);

    getContentPane().add(builder.getPanel());

    if (operation == LocationsListOperation.delete)
    {
      city.setEditable(false);
      city.setEnabled(false);
      countries.setEditable(false);
      countries.setEnabled(false);
      latitudeValue.setEditable(false);
      latitudeValue.setEnabled(false);
      longitudeValue.setEditable(false);
      longitudeValue.setEnabled(false);
      timeZone.setEditable(false);
      timeZone.setEnabled(false);
    }
    if (operation != LocationsListOperation.add)
    {
      setCurrentLocation(mainWindow.getSelectedLocation());
    }

    pack();
    setLocationRelativeTo(mainWindow);
    setResizable(false);
    setVisible(true);
  }

  private void clearError()
  {
    if (operation != LocationsListOperation.delete)
    {
      city.setBackground(Color.white);
      latitudeValue.setBackground(Color.white);
      longitudeValue.setBackground(Color.white);
    }
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

  private String getCity()
  {
    return city.getText().trim();
  }

  private Country getCountry()
  {
    return (Country) countries.getSelectedItem();
  }

  private Location getCurrentLocation()
  {
    return new Location(getCity(),
                        getCountry(),
                        getTimeZoneId(),
                        new PointLocation(getLatitude(), getLongitude()));
  }

  private Latitude getLatitude()
  {
    Latitude latitude = null;
    try
    {
      latitude = new CoordinateParser().parseLatitude(latitudeValue.getText());
    }
    catch (final us.fatehi.pointlocation6709.parse.ParserException e)
    {
      LOGGER.log(Level.FINE, e.getMessage(), e);
    }
    return latitude;
  }

  private Longitude getLongitude()
  {
    Longitude longitude = null;
    try
    {
      longitude = new CoordinateParser()
        .parseLongitude(longitudeValue.getText());
    }
    catch (final us.fatehi.pointlocation6709.parse.ParserException e)
    {
      LOGGER.log(Level.FINE, e.getMessage(), e);
    }
    return longitude;
  }

  private String getTimeZoneId()
  {
    return ((TimeZoneDisplay) timeZone.getSelectedItem()).getTimeZoneId();
  }

  private boolean isCurrentLocationValid()
  {
    boolean hasError = false;
    clearError();

    if (getCity().length() == 0)
    {
      hasError = true;
      showError(city);
    }
    else if (getLatitude() == null)
    {
      hasError = true;
      showError(latitudeValue);
    }
    else if (getLongitude() == null)
    {
      hasError = true;
      showError(longitudeValue);
    }

    return !hasError;
  }

  private void setCity(final String cityName)
  {
    city.setText(cityName);
  }

  private void setCountry(final Country country)
  {
    countries.setSelectedItem(country);
  }

  private void setCurrentLocation(final Location location)
  {
    if (location != null)
    {
      setLatitude(location.getPointLocation().getLatitude());
      setLongitude(location.getPointLocation().getLongitude());
      setCity(location.getCity());
      setCountry(location.getCountry());
      setTimeZoneId(location.getTimeZoneId());
    }
  }

  private void setLatitude(final Latitude latitude)
  {
    if (latitude != null)
    {
      latitudeValue.setText(latitude.toString());
    }
  }

  private void setLongitude(final Longitude longitude)
  {
    if (longitude != null)
    {
      longitudeValue.setText(longitude.toString());
    }
  }

  private void setTimeZoneId(final String timeZoneId)
  {
    timeZone.setSelectedItem(new TimeZoneDisplay(ZoneId.of(timeZoneId)));
  }

  private void showError(final Component component)
  {
    component.setBackground(new Color(255, 255, 204));
  }
}
