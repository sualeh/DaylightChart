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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;
import org.pointlocation6709.parser.CoordinateParser;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
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

  enum LocationMaintenanceOperation
  {
    Delete,
    Edit,
    Add;
  }

  private static final long serialVersionUID = -5161588534167787490L;

  private static final Logger LOGGER = Logger.getLogger(LocationDialog.class
    .getName());

  private final JTextField txtCity;
  private final JComboBox cbCountries;
  private final JTextField txtLatitude;
  private final JTextField txtLongitude;
  private final JTextField txtTimeZone;
  private final JLabel lblMessage;

  private final JButton ok;
  private final JButton cancel;

  private final Location editLocation;

  /**
   * Dialog for location delete, add or edit operation to be performed.
   */
  public LocationDialog(final LocationsList locationsList,
                        final LocationMaintenanceOperation operation)
  {

    super(locationsList.getMainWindow(), operation + " a Location", true);
    setSize(350, 230);
    setResizable(false);

    if (operation == LocationMaintenanceOperation.Add)
    {
      editLocation = null;
    }
    else
    {
      editLocation = locationsList.getSelectedLocation();
    }

    txtCity = new JTextField();
    cbCountries = new JComboBox(new Vector<Country>(Countries.getAllCountries()));
    txtLatitude = new JTextField();
    txtLongitude = new JTextField();
    txtTimeZone = new JTextField();
    txtTimeZone.setEditable(false);

    final ActionListener actionListener = new ActionListener()
    {

      public void actionPerformed(final ActionEvent actionEvent)
      {
        final Object source = actionEvent.getSource();
        if (source == cancel)
        {
          dispose();
        }
        else if (source == ok)
        {
          boolean hasError = validateAndShowError();
          if (hasError)
          {
            return;
          }
          Location location = getCurrentLocation();
          switch (operation)
          {
            case Add:
              locationsList.addLocation(location);
              break;
            case Edit:
              locationsList.replaceLocation(editLocation, location);
              break;
            case Delete:
              locationsList.removeLocation(editLocation);
          }
        }
        dispose();
      }
    };

    ok = new JButton("Ok");
    cancel = new JButton("Cancel");

    ok.addActionListener(actionListener);
    cancel.addActionListener(actionListener);

    lblMessage = new JLabel(" ");
    lblMessage.setForeground(Color.red);

    getContentPane().add(buildDialogPanel());

    final FocusListener focusListener = new FocusListener()
    {
      public void focusGained(FocusEvent e)
      {
      }

      public void focusLost(FocusEvent event)
      {
        if (event.isTemporary())
        {
          return;
        }

        final Component focusComponent = event.getComponent();
        System.err.println("Focus lost in " + focusComponent);

        if (focusComponent.equals(txtLatitude))
        {
          setLatitude(getLatitude());
        }
        if (focusComponent.equals(txtLongitude))
        {
          setLongitude(getLongitude());
        }

        boolean hasError = validateAndShowError();
        if (!hasError)
        {
          setTimeZoneId(getTimeZoneId());
        }
      }
    };

    txtLatitude.addFocusListener(focusListener);
    txtLongitude.addFocusListener(focusListener);
    txtCity.addFocusListener(focusListener);
    cbCountries.addFocusListener(focusListener);

    if (operation == LocationMaintenanceOperation.Delete)
    {
      txtLatitude.setEditable(false);
      txtLongitude.setEditable(false);
      txtCity.setEditable(false);
      cbCountries.setEnabled(false);
      lblMessage.setVisible(false);
    }

    setCurrentLocation(editLocation);

    pack();
    setLocationRelativeTo(locationsList.getMainWindow());
    setVisible(true);
  }

  private JPanel buildDialogPanel()
  {

    final JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(ok, cancel);

    FormLayout layout;
    PanelBuilder builder;
    final CellConstraints cc = new CellConstraints();

    layout = new FormLayout("right:p, 3dlu, p", // columns
                            "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"); // rows

    builder = new PanelBuilder(layout);
    builder.setDefaultDialogBorder();

    builder.addLabel("City", cc.xy(1, 1));
    builder.add(txtCity, cc.xy(3, 1));
    builder.addLabel("Country", cc.xy(1, 3));
    builder.add(cbCountries, cc.xy(3, 3));
    builder.addLabel("Latitude", cc.xy(1, 5));
    builder.add(txtLatitude, cc.xy(3, 5));
    builder.addLabel("Longitude", cc.xy(1, 7));
    builder.add(txtLongitude, cc.xy(3, 7));
    builder.addLabel("Time Zone", cc.xy(1, 9));
    builder.add(txtTimeZone, cc.xy(3, 9));

    builder.add(lblMessage, cc.xyw(1, 11, 3));
    builder.add(buttonBar, cc.xyw(1, 13, 3));

    final JPanel dialogPanel = builder.getPanel();
    return dialogPanel;
  }

  private String getCity()
  {
    return txtCity.getText().trim();
  }

  private Country getCountry()
  {
    return (Country) cbCountries.getSelectedItem();
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
      latitude = CoordinateParser.parseLatitude(txtLatitude.getText());
    }
    catch (final org.pointlocation6709.parser.ParserException e)
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
      longitude = CoordinateParser.parseLongitude(txtLongitude.getText());
    }
    catch (final org.pointlocation6709.parser.ParserException e)
    {
      LOGGER.log(Level.FINE, e.getMessage(), e);
    }
    return longitude;
  }

  private String getTimeZoneId()
  {
    final String timeZoneId = DefaultTimezones
      .attemptTimeZoneMatch(getCity(), getCountry(), getLongitude());
    return timeZoneId;
  }

  private void setCity(final String city)
  {
    txtCity.setText(city);
  }

  private void setCountry(final Country country)
  {
    cbCountries.setSelectedItem(country);
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
      txtLatitude.setText(latitude.toString());
    }
  }

  private void setLongitude(final Longitude longitude)
  {
    if (longitude != null)
    {
      txtLongitude.setText(longitude.toString());
    }
  }

  private void setTimeZoneId(final String timeZoneId)
  {
    final TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
    txtTimeZone.setText(timeZone.getDisplayName());
  }

  private void showError(final String message, final Component component)
  {
    lblMessage.setText(message);
    if (component != null)
    {
      component.requestFocus();
    }
  }

  private boolean validateAndShowError()
  {
    boolean hasError = false;
    if (getCity().length() == 0)
    {
      hasError = true;
      showError("Enter a city", txtCity);
    }
    else if (getLatitude() == null)
    {
      hasError = true;
      showError("Enter a latitude", txtLatitude);
    }
    else if (getLongitude() == null)
    {
      hasError = true;
      showError("Enter a longitude", txtLongitude);
    }
    else
    {
      showError(" ", null);
    }
    return hasError;
  }

}
