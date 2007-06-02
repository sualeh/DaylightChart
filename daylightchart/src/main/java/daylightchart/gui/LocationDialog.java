package daylightchart.gui;


import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.pointlocation6709.Angle;
import org.pointlocation6709.Latitude;
import org.pointlocation6709.Longitude;
import org.pointlocation6709.PointLocation;
import org.pointlocation6709.parser.FormatterException;
import org.pointlocation6709.parser.PointLocationFormatType;
import org.pointlocation6709.parser.PointLocationFormatter;

import daylightchart.location.Country;
import daylightchart.location.Location;
import daylightchart.location.parser.Countries;
import daylightchart.location.parser.DefaultTimezones;
import daylightchart.location.parser.LocationParser;
import daylightchart.location.parser.ParserException;

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
  public static final String DELETE = "Delete";

  public static final String EDIT = "Edit";

  public static final String ADD = "Add";

  /**
   * 
   */
  private static final long serialVersionUID = -5161588534167787490L;
  private DaylightChartGui parent = null;
  private JComboBox cbCountries = null;
  private JTextField txtLatitude = null;

  private JTextField txtLongitude = null;
  private JTextField txtTimeZone = null;
  private JTextField txtCity = null;

  private JLabel lblLatitude = null;
  private JLabel lblLongitude = null;

  private JLabel lblMessage = null;
  private JButton btnOK = null;
  private JButton btnCancel = null;
  // private JButton btnCalcTZ = null;

  private Location location = null;
  private Latitude latitude = null;
  private Longitude longitude = null;
  private String action = null;

  private Country[] arrCountries = null;
  private List<Location> locations = null;
  private int index = 0;
  private float flLatitude = 0;
  private float flLongitude = 0;

  private String strCountry = null;
  private String strCity = null;
  private String strTimeZone = null;

  private String message = null;
  private Component component = null;
  private boolean errFlg = false;

  /**
   * Constructor. It takes the parent frame as a parameter.
   * 
   * @param DaylightChartGui
   *        frame
   */
  public LocationDialog(final DaylightChartGui frame)
  {
    parent = frame;

    setModal(true);
    setSize(400, 300);
    setTitle("Locations Editor");

    locations = parent.getLocations();

    txtLatitude = new JTextField(20);
    txtLongitude = new JTextField(20);
    lblLatitude = new JLabel();
    lblLongitude = new JLabel();

    final JPanel pnlLat = new JPanel();
    pnlLat.setLayout(new FlowLayout());
    pnlLat.add(new JLabel("Latitude :"));
    pnlLat.add(txtLatitude);
    pnlLat.add(lblLatitude);

    final JPanel pnlLon = new JPanel();
    pnlLon.setLayout(new FlowLayout());
    pnlLon.add(new JLabel("Longitude :"));
    pnlLon.add(txtLongitude);
    pnlLon.add(lblLongitude);

    txtCity = new JTextField(15);

    final JPanel pnlCity = new JPanel(new FlowLayout());
    pnlCity.add(new JLabel("City :"));
    pnlCity.add(txtCity);
    final JPanel pnlCon = new JPanel(new FlowLayout());
    pnlCon.add(new JLabel("Country :"));
    populateCountries();
    pnlCon.add(cbCountries);

    txtTimeZone = new JTextField(20);
    txtTimeZone.setEditable(false);
    final JPanel pnlTZ = new JPanel(new FlowLayout());
    pnlTZ.add(new JLabel("TimeZone :"));
    pnlTZ.add(txtTimeZone);
    // btnCalcTZ = new JButton("Calculate Time Zone");
    // pnlTZ.add(btnCalcTZ);

    final JPanel pnlButtons = new JPanel(new FlowLayout());

    btnOK = new JButton("      OK      ");
    btnCancel = new JButton("    Cancel    ");
    pnlButtons.add(btnOK);
    pnlButtons.add(btnCancel);

    btnOK.addActionListener(this);
    btnCancel.addActionListener(this);
    // btnCalcTZ.addActionListener(this);

    message = "";
    lblMessage = new JLabel(message);
    final JPanel pnlMessage = new JPanel();
    pnlMessage.add(lblMessage);

    final GridBagLayout gb = new GridBagLayout();
    final GridBagConstraints gc = new GridBagConstraints();
    setLayout(gb);

    gc.anchor = GridBagConstraints.WEST;
    gc.weightx = 1;
    gc.weighty = 1;

    add(pnlLat, gb, gc, 0, 0, 1, 1);
    add(pnlLon, gb, gc, 0, 1, 1, 1);
    add(pnlCity, gb, gc, 0, 2, 1, 1);
    add(pnlCon, gb, gc, 0, 3, 1, 1);
    add(pnlTZ, gb, gc, 0, 4, 1, 1);
    add((new JPanel()).add(new JLabel(" ")), gb, gc, 0, 5, 1, 1); // filler
    add(pnlButtons, gb, gc, 0, 6, 4, 1);
    add(new JPanel().add(new JLabel(" ")), gb, gc, 0, 7, 1, 1); // filler

    add(pnlMessage, gb, gc, 0, 8, 4, 1);

    // adding the focus listener
    final FocusListener listener = new FocusListener()
    {
      public void focusGained(FocusEvent e)
      {
        if (e.getComponent().equals(txtLatitude))
        {
          message = "Enter latitude in ±DDMMSS.SS format";
        }
        if (e.getComponent().equals(txtLongitude))
        {
          message = "Enter Longitude in ±DDDMMSS.SS format";
        }
        if (e.getComponent().equals(txtCity))
        {
          message = "Enter city";
        }
        if (e.getComponent().equals(cbCountries))
        {
          message = "Select a country from the list";
        }
        lblMessage.setText(message);

      }

      public void focusLost(FocusEvent e)
      {
        if (e.getComponent().equals(txtLatitude))
        {
          latitude = makeLatitude(txtLatitude.getText().trim());
          if (latitude != null)
          {
            lblLatitude.setText(latitude.toString());
          }
        }
        if (e.getComponent().equals(txtLongitude))
        {
          longitude = makeLongitude(txtLongitude.getText().trim());
          if (longitude != null)
          {
            lblLongitude.setText(longitude.toString());
          }
        }

        if (latitude != null && longitude != null)
        {
          System.out.println("Like Timezone btn pressed ");

          boolean flg = validateInformation();
          if (!flg)
          {
            calculateTimeZone();
            txtTimeZone.setText(strTimeZone);
          }
          else
          {
            component.requestFocus();
          }
        }
      }
    };

    txtLatitude.addFocusListener(listener);
    txtLongitude.addFocusListener(listener);
    txtCity.addFocusListener(listener);
    cbCountries.addFocusListener(listener);

    repaint();

  }

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
    this(frame);
    action = act;

    if (locn != null) // i.e if its not the ADD operation
    {
      location = locn;
      latitude = location.getPointLocation().getLatitude();
      longitude = location.getPointLocation().getLongitude();

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
      txtCity.setText(location.getCity());
      cbCountries.setSelectedItem(location.getCountry());
      txtTimeZone.setText(location.getTimeZoneId());
      index = parent.getSelectedLocationIndex();
    }

    if (act.equals(LocationDialog.DELETE))
    {
      txtLatitude.setEditable(false);
      txtLongitude.setEditable(false);
      txtCity.setEditable(false);
      cbCountries.setEnabled(false);
      // btnCalcTZ.setVisible(false);
      lblMessage.setVisible(false);
    }

    repaint();
  }

  public void actionPerformed(final ActionEvent actevt)
  {

    if (actevt.getSource() == btnOK)
    {
      if (action.equals(LocationDialog.ADD))
      {
        errFlg = validateInformation();
        System.out.println("After Validate " + errFlg);
        if (errFlg)
        {
          component.requestFocus();
          lblMessage.setText(message);
        }
        else
        {
          location = makeLocation();
          locations.add(location);
          parent.setLocations(locations);
          dispose();
        }
      }
      else if (action.equals(LocationDialog.EDIT))
      {
        errFlg = validateInformation();
        if (errFlg)
        {
          component.requestFocus();
          lblMessage.setText(message);
        }
        else
        {
          errFlg = validateInformation();
          System.out.println("After Validate modify " + errFlg);
          if (errFlg)
          {
            component.requestFocus();
            lblMessage.setText(message);
          }
          else
          {
            location = makeLocation();
            locations.remove(index);
            locations.add(index, location);

            parent.setLocations(locations);
            dispose();
          }
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
    /*
     * else // if (actevt.getSource() == (JButton)btnCalcTZ) {
     * System.out.println("Timezone btn pressed "); boolean flg =
     * validateInformation(); if (!flg) { calculateTimeZone();
     * txtTimeZone.setText(strTimeZone); } else
     * component.requestFocus(); }
     */
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

  private void add(final Component c,
                   final GridBagLayout gbl,
                   final GridBagConstraints gbc,
                   final int x,
                   final int y,
                   final int w,
                   final int h)
  {
    gbc.gridx = x;
    gbc.gridy = y;
    gbc.gridwidth = w;
    gbc.gridheight = h;
    gbl.setConstraints(c, gbc);
    add(c);
  }

  private void calculateTimeZone()
  {

    final Country country = (Country) cbCountries.getSelectedItem();
    strCountry = country.getName();

    System.out.println("Calc TZ **" + flLongitude);
    strTimeZone = DefaultTimezones.attemptTimeZoneMatch(strCity,
                                                        country,
                                                        longitude);

    System.out.println("TZ " + strTimeZone);
  }

  private Comparator getComparator()
  {
    return new Comparator()
    {
      public int compare(final Object c1, final Object c2)
      {
        return ((Country) c1).getName().compareTo(((Country) c2).getName());
      }
    };
  }

  private Latitude makeLatitude(final String lat)
  {
    try
    {
      flLatitude = new Float(lat).floatValue();
    }
    catch (final NumberFormatException e)
    {
      return null;
    }
    latitude = new Latitude(Angle.fromDegrees(flLatitude));
    return latitude;
  }

  private Location makeLocation()
  {

    if (strTimeZone == null)
    {
      calculateTimeZone();
    }

    System.out.println("Latitude Longitude " + latitude.toString() + " "
                       + longitude.toString());

    final PointLocation ploc = new PointLocation(latitude, longitude);
    String strLatLong = null;
    try
    {
      strLatLong = PointLocationFormatter
        .formatIso6709(ploc, PointLocationFormatType.DECIMAL);
    }
    catch (final FormatterException e)
    {
      System.out.println("Error in formatting");
    }

    final String strRepresentation = strCity + ";" + strCountry + ";"
                                     + strTimeZone + ";" + strLatLong;

    try
    {
      location = LocationParser.parseLocation(strRepresentation);
    }
    catch (final ParserException ex)
    {
    }

    return location;
  }

  private Longitude makeLongitude(final String lon)
  {
    try
    {
      flLongitude = new Float(lon).floatValue();
    }
    catch (final NumberFormatException e)
    {
      return null;
    }
    longitude = new Longitude(Angle.fromDegrees(flLongitude));
    return longitude;

  }

  private void populateCountries()
  {

    final Set<Country> hsCountries = Countries.getAllCountries();
    arrCountries = new Country[hsCountries.size()];
    hsCountries.toArray(arrCountries);

    Arrays.sort(arrCountries, getComparator());
    cbCountries = new JComboBox(arrCountries);

  }

  private boolean validateInformation()
  {
    boolean flg = false;

    String txtNum = txtLatitude.getText().trim();
    if (txtNum.length() != 0)
    {
      latitude = makeLatitude(txtNum);
      if (latitude == null)
      {
        message = "Enter numeric data for Longitude";
        component = txtLongitude;
        flg = true;
        return flg;
      }
    }
    else
    {
      txtLatitude.setText("0");
    }

    txtNum = txtLongitude.getText().trim();
    if (txtNum.length() != 0)
    {
      longitude = makeLongitude(txtNum);
      if (longitude == null)
      {
        message = "Enter numeric data for Longitude";
        component = txtLongitude;
        flg = true;
        return flg;
      }
    }
    else
    {
      txtLongitude.setText("0");
    }

    strCity = txtCity.getText().trim();
    if (strCity.length() == 0)
    {
      message = "Enter city";
      component = txtCity;
      flg = true;
      return flg;
    }

    System.out.println("Latitude " + flLatitude + " Longitude " + longitude);
    return flg;
  }

}
