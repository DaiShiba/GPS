package tigerapplication2.yomogi.co.jp.gps.Preference;

import android.content.Context;
import android.content.SharedPreferences;

/**最終検知位置情報管理用のSharedPrefernce*/
public enum LastLocationPreference {
    UPDATE_DATE(0),
    LATITUDE(0),
    LONGITUDE(0),
    ALTITUDE(0);

    public final Object DEFAULT;

    LastLocationPreference(Object object){
        DEFAULT = object;
    }

    public static synchronized SharedPreferences getThisPreference(final Context context) {
        return context.getSharedPreferences(LastLocationPreference.class.getSimpleName(), Context.MODE_PRIVATE);
    }
}
