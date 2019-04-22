package tigerapplication2.yomogi.co.jp.gps.Intent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

public class IntentUtility {
    private final static String LOG_TAG = IntentUtility.class.getSimpleName();

    /**設定アプリを起動する*/
    public static void openSettings(final Context context) {
        Log.d(LOG_TAG,"openSettings Called");
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**位置情報設定画面を起動する*/
    public static void openLocationSettings(final Context context) {
        Log.d(LOG_TAG,"openLocationSettings Called");
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /**端末内の地図アプリを起動する*/
    public static void openMapApplication(final Activity activity,
                                          String lastLatitude,
                                          String lastLongitude) {
        Log.d(LOG_TAG,"openMapApplication Called");
        Uri uri = Uri.parse("geo:" + lastLatitude + "," + lastLongitude + "?z=17");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    /**端末内の地図アプリに検索ワードを渡して起動する*/
    public static void openMapApplicationByInfo(final Activity activity,
                                                String name, String latitude, String longitude) {

        Log.d(LOG_TAG,"openMapApplicationByInfo Called");
        Uri uri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + name);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }
}
