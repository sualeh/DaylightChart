package daylightchart.gui;


import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * User preferences for the GUI.
 * 
 * @author Sualeh Fatehi
 */
public class UserPreferences
{

  private enum PreferenceKeys
  {
    locations("daylightchart.locations");

    private final String key;

    private PreferenceKeys(final String key)
    {
      this.key = key;
    }

    /**
     * @return the key
     */
    public String getKey()
    {
      return key;
    }

  }

  /**
   * Main method. Clears all user preferences.
   * 
   * @param args
   *        Command line arguments
   */
  public static void main(final String[] args)
  {

    String command = "list";
    if (args.length > 0)
    {
      command = args[0];
    }
    if ("clear".equals(command))
    {
      new UserPreferences().clear();
    }
    else if ("list".equals(command))
    {
      new UserPreferences().listAllPreferences();
    }
    else
    {
      new UserPreferences().listAllPreferences();
    }

  }

  private final Preferences preferences = Preferences.userNodeForPackage(this
    .getClass());

  void clear()
  {
    try
    {
      preferences.clear();
    }
    catch (final BackingStoreException e)
    {
      e.printStackTrace();
    }
    System.out.println("User preferences cleared.");
  }

  String getLocations()
  {
    return preferences.get(PreferenceKeys.locations.getKey(), null);
  }

  void listAllPreferences()
  {
    System.out.println("User preferences:");
    try
    {
      preferences.exportNode(System.out);
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }
    catch (final BackingStoreException e)
    {
      e.printStackTrace();
    }
  }

  void setLocations(final String locations)
  {
    preferences.put(PreferenceKeys.locations.getKey(), locations);
  }

}
