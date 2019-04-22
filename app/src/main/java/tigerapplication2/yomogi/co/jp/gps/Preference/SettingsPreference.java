package tigerapplication2.yomogi.co.jp.gps.Preference;

import android.content.Context;
import android.content.SharedPreferences;

/**設定値管理用SharedPrefernce*/
public enum SettingsPreference {
    LOCATION_ENABLED(false),
    GEOFENCE_ENABLED(false);

    public final Object DEFAULT;

    SettingsPreference(Object object) {
        DEFAULT = object;
    }

    // このenum専用のSharedPreference
    public static synchronized SharedPreferences getThisPreference(final Context context) {
        return context.getSharedPreferences(SettingsPreference.class.getSimpleName(), Context.MODE_PRIVATE);
    }
}
