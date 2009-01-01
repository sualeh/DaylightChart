package daylightchart.gui;


import java.util.List;

import org.geoname.data.Location;

public interface LocationOperations
{

  /**
   * Add a location to the list, in sorted order.
   * 
   * @param location
   *        Location to add
   */
  void addLocation(final Location location);

  /**
   * Gets all locations in the list.
   * 
   * @return Locations list.
   */
  List<Location> getLocations();

  /**
   * Get the currently selected location.
   * 
   * @return Currently selected location.
   */
  Location getSelectedLocation();

  /**
   * Removes the specified location.
   * 
   * @param location
   *        Location
   */
  void removeLocation(final Location location);

  /**
   * Replaces a location on the list with another.
   * 
   * @param location
   *        Location to replace
   * @param newLocation
   *        New location
   */
  void replaceLocation(final Location location, final Location newLocation);

  /**
   * Set the locations list.
   * 
   * @param locations
   *        Locations list.
   */
  void setLocations(final List<Location> locations);

  /**
   * Sort the locations in the list.
   */
  void sortLocations();

}
