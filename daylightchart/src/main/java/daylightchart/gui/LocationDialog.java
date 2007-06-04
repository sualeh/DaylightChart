package daylightchart.gui;


import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;
import org.pointlocation6709.parser.FormatterException;
import org.pointlocation6709.parser.PointLocationFormatType;
import org.pointlocation6709.parser.PointLocationFormatter;
import org.pointlocation6709.parser.PointLocationParser;

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
 * @author Pradnya Devare
 */
public class LocationDialog
  extends JDialog
  implements ActionListener
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

  public static final String DELETE = "Delete";

  public static final String EDIT = "Edit";

  public static final String ADD = "Add";

  private static final long serialVersionUID = -5161588534167787490L;

  private static final Logger LOGGER = Logger.getLogger(LocationDialog.class
    .getName());

  private DaylightChartGui parent = null;

  private JTextField txtCity = null;
  private JComboBox cbCountries = null;
  private JTextField txtLatitude = null;
  private JTextField txtLongitude = null;
  private JTextField txtTimeZone = null;

  private JLabel lblMessage = null;

  private JButton btnOK = null;
  private JButton btnCancel = null;

  private String action = null;
  private List<Location> locations = null;
  private int index = 0;

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
  public LocationDialog(final DaylightChartGui frame,
                        final Location locn,
                        final String act)
  {

    super(frame, true);
    setSize(350, 230);
    setResizable(false);

    parent = frame;
    locations = parent.getLocations();

    txtCity = new JTextField();
    cbCountries = new JComboBox(new Vector<Country>(Countries.getAllCountries()));
    txtLatitude = new JTextField();
    txtLongitude = new JTextField();
    txtTimeZone = new JTextField();

    btnOK = new JButton("Ok");
    btnCancel = new JButton("Cancel");

    btnOK.addActionListener(this);
    btnCancel.addActionListener(this);

    lblMessage = new JLabel(" ");
    lblMessage.setForeground(Color.red);

    final JPanel dialogPanel = buildDialogPanel();
    add(dialogPanel);

    final FocusListener listener = new FocusListener()
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

    txtLatitude.addFocusListener(listener);
    txtLongitude.addFocusListener(listener);
    txtCity.addFocusListener(listener);
    cbCountries.addFocusListener(listener);

    repaint();
    action = act;

    if (locn != null) // i.e if its not the ADD operation
    {
      Latitude latitude = locn.getPointLocation().getLatitude();
      Longitude longitude = locn.getPointLocation().getLongitude();

      if (action.equals(LocationDialog.DELETE))
      {
        txtLatitude.setText(latitude.toString());
        txtLongitude.setText(longitude.toString());
      }
      else if (action.equals(LocationDialog.EDIT))
      {
        try
        {
          txtLatitude.setText(PointLocationFormatter
            .formatLatitude(latitude, PointLocationFormatType.DECIMAL));
          txtLongitude.setText(PointLocationFormatter
            .formatLongitude(longitude, PointLocationFormatType.DECIMAL));
        }
        catch (final FormatterException e)
        {

        }
      }
      txtCity.setText(locn.getCity());
      cbCountries.setSelectedItem(locn.getCountry());
      txtTimeZone.setText(locn.getTimeZoneId());
      index = parent.getSelectedLocationIndex();
    }

    if (act.equals(LocationDialog.DELETE))
    {
      txtLatitude.setEditable(false);
      txtLongitude.setEditable(false);
      txtCity.setEditable(false);
      cbCountries.setEnabled(false);
      lblMessage.setVisible(false);
    }

    repaint();
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

  public void actionPerformed(final ActionEvent actevt)
  {

    if (actevt.getSource() == btnOK)
    {
      if (action.equals(LocationDialog.ADD))
      {
        final Error error = validateInformation();
        if (error.hasError())
        {
          error.display();
        }
        else
        {
          Location location = getCurrentLocation();
          locations.add(location);
          parent.setLocations(locations);
          dispose();
        }
      }
      else if (action.equals(LocationDialog.EDIT))
      {
        final Error error = validateInformation();
        if (error.hasError())
        {
          error.display();
        }
        else
        {
          Location location = getCurrentLocation();
          locations.remove(index);
          locations.add(index, location);

          parent.setLocations(locations);
          dispose();
        }
      }
      else
      // Delete
      {
        final int rep = JOptionPane
          .showConfirmDialog(this,
                             "Are you sure?",
                             "Confirm Deletion",
                             JOptionPane.YES_NO_OPTION);
        if (rep == JOptionPane.OK_OPTION)
        {
          locations.remove(index);
          index = 0;
          parent.setLocations(locations);
          dispose();
        }
        else if (rep == JOptionPane.CANCEL_OPTION)
        {
          dispose();
        }
      }
    }
    else if (actevt.getSource() == btnCancel)
    {
      dispose();
      System.out.println("Cancel pressed ");

    }
  }

  /**
   * This method returns the current index on the Locations list.
   * 
   * @return int
   */
  public int getIndex()
  {
    return index;
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
      latitude = PointLocationParser
        .parseLatitude(txtLatitude.getText().trim());
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
      longitude = PointLocationParser.parseLongitude(txtLongitude.getText()
        .trim());
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
