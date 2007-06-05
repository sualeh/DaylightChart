package daylightchart.gui;


import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import org.pointlocation6709.parser.FormatterException;
import org.pointlocation6709.parser.PointLocationFormatType;
import org.pointlocation6709.parser.PointLocationFormatter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import daylightchart.location.Country;
import daylightchart.location.Location;
import daylightchart.location.parser.Countries;
import daylightchart.location.parser.DefaultTimezones;

/**
 * This class is used for the display of the Location editor. The user
 * is allowed to add new locations, and delete or modify existing
 * locations.
 * 
 * @author Pradnya Devare, Sualeh Fatehi
 */
public class LocationDialog
  extends JDialog
{

  private class Error
  {

    private final String message;
    private final Component component;
    private final boolean hasError;

    Error(final String message,
          final Component component,
          final boolean hasError)
    {
      this.message = message;
      this.component = component;
      this.hasError = hasError;
    }

    void display()
    {
      if (hasError && lblMessage != null)
      {
        lblMessage.setText(message);
        if (component != null)
        {
          component.requestFocus();
        }
      }
      else
      {
        lblMessage.setText("");
      }
    }

    boolean hasError()
    {
      return hasError;
    }

  }

  enum LocationMaintenanceOperation
  {
    DELETE,
    EDIT,
    ADD;
  }

  private static final long serialVersionUID = -5161588534167787490L;

  private static final Logger LOGGER = Logger.getLogger(LocationDialog.class
    .getName());

  private LocationsList locationsList;

  private JTextField txtCity;
  private JComboBox cbCountries;
  private JTextField txtLatitude;
  private JTextField txtLongitude;
  private JTextField txtTimeZone;

  private JLabel lblMessage;

  private JButton btnOK;
  private JButton btnCancel;

  private Location editLocation;

  /**
   * Constructor. This is used when there is a delete or edit operation
   * to be performed.
   * 
   * @param DaylightChartGui
   *        frame
   * @param Location
   *        locn
   * @param String
   *        act
   */
  public LocationDialog(final LocationsList locationsList,
                        final Location location,
                        final LocationMaintenanceOperation act)
  {
    super();
// super((JFrame) locationsList.getParent().getParent().getParent(),
// true);
    setSize(350, 230);
    setResizable(false);

    this.locationsList = locationsList;
    editLocation = location;

    txtCity = new JTextField();
    cbCountries = new JComboBox(new Vector<Country>(Countries.getAllCountries()));
    txtLatitude = new JTextField();
    txtLongitude = new JTextField();
    txtTimeZone = new JTextField();

    ActionListener actionListener = new ActionListener()
    {

      public void actionPerformed(final ActionEvent actevt)
      {
        if (actevt.getSource() == btnOK)
        {
          final Error error = validateInformation();
          if (error.hasError())
          {
            error.display();
            return;
          }
          Location location = getCurrentLocation();
          switch (act)
          {
            case ADD:
              locationsList.addLocation(location);
              break;
            case EDIT:
              locationsList.replaceLocation(editLocation, location);
              break;
            case DELETE:
              locationsList.removeLocation(editLocation);
          }
        }
        dispose();
      }
    };

    btnOK = new JButton("Ok");
    btnCancel = new JButton("Cancel");

    btnOK.addActionListener(actionListener);
    btnCancel.addActionListener(actionListener);

    lblMessage = new JLabel(" ");
    lblMessage.setForeground(Color.red);

    final JPanel dialogPanel = buildDialogPanel();
    add(dialogPanel);

    final FocusListener focusListener = new FocusListener()
    {
      public void focusGained(FocusEvent e)
      {
      }

      public void focusLost(FocusEvent event)
      {
        Latitude latitude = null;
        Longitude longitude = null;
        if (event.getComponent().equals(txtLatitude))
        {
          latitude = getLatitude();
          if (latitude != null)
          {
            txtLatitude.setText(latitude.toString());
          }
        }
        if (event.getComponent().equals(txtLongitude))
        {
          longitude = getLongitude();
          if (longitude != null)
          {
            txtLongitude.setText(longitude.toString());
          }
        }

        if (latitude != null && longitude != null)
        {
          Error error = validateInformation();
          if (error.hasError())
          {
            error.display();
          }
          else
          {
            txtTimeZone.setText(getTimeZoneId());
          }
        }
      }
    };

    txtLatitude.addFocusListener(focusListener);
    txtLongitude.addFocusListener(focusListener);
    txtCity.addFocusListener(focusListener);
    cbCountries.addFocusListener(focusListener);

    setLocation(location);

    if (act == LocationMaintenanceOperation.DELETE)
    {
      txtLatitude.setEditable(false);
      txtLongitude.setEditable(false);
      txtCity.setEditable(false);
      cbCountries.setEnabled(false);
      lblMessage.setVisible(false);
    }

    repaint();
  }

  private void setLocation(final Location location)
  {
    if (location != null)
    {
      Latitude latitude = location.getPointLocation().getLatitude();
      Longitude longitude = location.getPointLocation().getLongitude();

      try
      {
        txtLatitude.setText(PointLocationFormatter
          .formatLatitude(latitude, PointLocationFormatType.LONG));
        txtLongitude.setText(PointLocationFormatter
          .formatLongitude(longitude, PointLocationFormatType.LONG));
      }
      catch (final FormatterException e)
      {
        LOGGER.log(Level.FINE, e.getMessage(), e);
      }
      txtCity.setText(location.getCity());
      cbCountries.setSelectedItem(location.getCountry());
      txtTimeZone.setText(location.getTimeZoneId());
    }
  }

  private JPanel buildDialogPanel()
  {
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
    builder.add(ButtonBarFactory.buildOKCancelBar(btnOK, btnCancel), cc.xyw(1,
                                                                            13,
                                                                            3));

    final JPanel dialogPanel = builder.getPanel();
    return dialogPanel;
  }

  private Country getCountry()
  {
    final Country country = (Country) cbCountries.getSelectedItem();
    return country;
  }

  private String getCity()
  {
    return txtCity.getText().trim();
  }

  private Location getCurrentLocation()
  {
    final PointLocation ploc = new PointLocation(getLatitude(), getLongitude());
    Location location = new Location(getCity(),
                                     getCountry(),
                                     getTimeZoneId(),
                                     ploc);
    return location;
  }

  private Error validateInformation()
  {
    if (getCity().length() == 0)
    {
      return new Error("Enter a city", txtCity, true);
    }
    if (getLatitude() == null)
    {
      return new Error("Enter a latitude", txtLatitude, true);
    }

    if (getLongitude() == null)
    {
      return new Error("Enter a longitude", txtLongitude, true);
    }

    return new Error("", null, false);
  }

  private Latitude getLatitude()
  {
    Latitude latitude = null;
    try
    {
      latitude = CoordinateParser.parseLatitude(txtLatitude.getText());
    }
    catch (org.pointlocation6709.parser.ParserException e)
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
    catch (org.pointlocation6709.parser.ParserException e)
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
}
