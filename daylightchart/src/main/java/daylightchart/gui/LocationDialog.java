package daylightchart.gui;


import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;

import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;
import org.pointlocation6709.parser.CoordinateParser;
import org.pointlocation6709.parser.ParserException;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import daylightchart.location.Country;
import daylightchart.location.Location;
import daylightchart.location.parser.Countries;
import daylightchart.location.parser.DefaultTimezones;

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

  private static final long serialVersionUID = -5161588534167787490L;

  private static final Logger LOGGER = Logger.getLogger(LocationDialog.class
    .getName());

  /**
   * Show a dialog for location delete, add or edit operation to be
   * performed.
   * 
   * @param locationsList
   *        Locations list
   * @param operation
   *        Operation to perform
   * @return Location, if one was added, edited or deleted, or null if
   *         the dialog was canceled
   */
  public static Location showLocationDialog(final LocationsList locationsList,
                                            final LocationsListMaintenanceOperation operation)
  {
    final LocationDialog locationDialog = new LocationDialog(locationsList,
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

  private final JTextField city;
  private final JComboBox countries;
  private final JTextField latitudeValue;
  private final JTextField longitudeValue;

  private final JTextField timeZone;
  private final JButton ok;

  private final JButton cancel;

  private boolean dialogNotCancelled;

  private LocationDialog(final LocationsList locationsList,
                         final LocationsListMaintenanceOperation operation)
  {

    super(locationsList.getMainWindow(), operation.getText(), true);
    setSize(350, 230);
    setResizable(false);

    city = new JTextField();
    countries = new JComboBox(new Vector<Country>(Countries.getAllCountries()));
    latitudeValue = new JTextField();
    longitudeValue = new JTextField();
    timeZone = new JTextField();
    timeZone.setEditable(false);

    final ActionListener actionListener = new ActionListener()
    {
      public void actionPerformed(final ActionEvent actionEvent)
      {
        final Object source = actionEvent.getSource();
        if (source == ok)
        {
          if (isCurrentLocationValid())
          {
            return;
          }
          dialogNotCancelled = true;
        }
        dispose();
      }
    };

    ok = new JButton("Ok");
    cancel = new JButton("Cancel");

    ok.addActionListener(actionListener);
    cancel.addActionListener(actionListener);

    final FormLayout layout = new FormLayout("right:p, 3dlu, p");
    final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
    builder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    builder.append("City", city);
    builder.append("Country", countries);
    builder.append("Latitude", latitudeValue);
    builder.append("Longitude", longitudeValue);
    builder.append("Time Zone", timeZone);

    builder.append(ButtonBarFactory.buildOKCancelBar(ok, cancel), 3);

    getContentPane().add(builder.getPanel());

    final FocusListener focusListener = new FocusListener()
    {
      public void focusGained(@SuppressWarnings("unused")
      FocusEvent e)
      {
      }

      public void focusLost(FocusEvent event)
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
            setTimeZoneId(getTimeZoneId());
          }
        }
      }
    };

    latitudeValue.addFocusListener(focusListener);
    longitudeValue.addFocusListener(focusListener);
    city.addFocusListener(focusListener);
    countries.addFocusListener(focusListener);

    if (operation == LocationsListMaintenanceOperation.delete)
    {
      latitudeValue.setEditable(false);
      longitudeValue.setEditable(false);
      city.setEditable(false);
      countries.setEnabled(false);
    }
    if (operation != LocationsListMaintenanceOperation.add)
    {
      setCurrentLocation(locationsList.getSelectedLocation());
    }

    pack();
    setLocationRelativeTo(locationsList.getMainWindow());
    setVisible(true);
  }

  private void clearError()
  {
    city.setBackground(Color.white);
    latitudeValue.setBackground(Color.white);
    longitudeValue.setBackground(Color.white);
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
      latitude = CoordinateParser.parseLatitude(latitudeValue.getText());
    }
    catch (final ParserException e)
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
      longitude = CoordinateParser.parseLongitude(longitudeValue.getText());
    }
    catch (final ParserException e)
    {
      LOGGER.log(Level.FINE, e.getMessage(), e);
    }
    return longitude;
  }

  private String getTimeZoneId()
  {
    return DefaultTimezones.attemptTimeZoneMatch(getCity(),
                                                 getCountry(),
                                                 getLongitude());
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
    return hasError;
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
    timeZone.setText(TimeZone.getTimeZone(timeZoneId).getDisplayName());
  }

  private void showError(final Component component)
  {
    if (component != null)
    {
      component.setBackground(new Color(255, 255, 204));
    }
  }

}
