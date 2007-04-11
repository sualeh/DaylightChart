package daylightchart.gui;


import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

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

  public static void main(final String[] args)
  {
    new UserPreferences().clear();
  }

  private final Preferences preferences = Preferences.userNodeForPackage(this
    .getClass());

  public void clear()
  {
    try
    {
      preferences.clear();
    }
    catch (final BackingStoreException e)
    {
      e.printStackTrace();
    }
  }

  public String getLocations()
  {
    return preferences.get(PreferenceKeys.locations.getKey(), null);
  }

  public void setLocations(final String locations)
  {
    preferences.put(PreferenceKeys.locations.getKey(), locations);
  }
}
