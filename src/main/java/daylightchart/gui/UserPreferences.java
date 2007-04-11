package daylightchart.gui;


import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class UserPreferences
{

  private enum PreferenceKeys
  {
    locations("daylightchart.locations");

    private String key;

    /**
     * @return the key
     */
    public String getKey()
    {
      return key;
    }

    private PreferenceKeys(String key)
    {
      this.key = key;
    }

  }

  private final Preferences preferences = Preferences.userNodeForPackage(this
    .getClass());

  public String getLocations()
  {
    return preferences.get(PreferenceKeys.locations.getKey(), null);
  }

  public void setLocations(String locations)
  {
    preferences.put(PreferenceKeys.locations.getKey(), locations);
  }

  public void clear()
  {
    try
    {
      preferences.clear();
    }
    catch (BackingStoreException e)
    {
      e.printStackTrace();
    }
  }

  public static void main(String[] args)
  {
    new UserPreferences().clear();
  }
}
