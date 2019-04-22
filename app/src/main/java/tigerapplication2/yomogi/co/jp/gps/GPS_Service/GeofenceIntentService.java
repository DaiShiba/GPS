package tigerapplication2.yomogi.co.jp.gps.GPS_Service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tigerapplication2.yomogi.co.jp.gps.Activity_Fragment.NavigationTopActivity;
import tigerapplication2.yomogi.co.jp.gps.Preference.LastLocationPreference;
import tigerapplication2.yomogi.co.jp.gps.R;
import tigerapplication2.yomogi.co.jp.gps.Database.RestaurantDatabaseHelper;
import tigerapplication2.yomogi.co.jp.gps.Json.RestaurantInfo;
import tigerapplication2.yomogi.co.jp.gps.Json.RestaurantJsonParser;

/**
 * Geofence登録用情報を取得するIntentService
 */
public class GeofenceIntentService extends IntentService {
    /**IntentService判別用アクション名 */
    private static final String ACTION_GET_GEOFENCE = "tigerapplication2.yomogi.co.jp.gps.action.FOO";

    private static final String EXTRA_PARAM1 = "tigerapplication2.yomogi.co.jp.gps.extra.PARAM1";

    private static final String LOG_TAG = GeofenceIntentService.class.getSimpleName();
    public static final String GEOFENCE_SERVICE_FLAG = "geofenceServiceFlag";

    private static Context mContext;
    private static final String CHANNEL_ID = "channelId";

    public GeofenceIntentService() {
        super("DestroyIntentService");
        Log.d(LOG_TAG,"Constructor Created");
    }

    /**
     * 指定されたカテゴリをもとにGeofence登録用位置情報をぐるなびAPIを使用して取得
     * @see IntentService
     */
    public static void startActionGeofence(Context context, String param1) {
        Log.d(LOG_TAG,"startActionGeofence Called");
        Intent intent = new Intent(context, GeofenceIntentService.class);
        mContext = context;
        intent.setAction(ACTION_GET_GEOFENCE);
        intent.putExtra(EXTRA_PARAM1, param1);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG,"onHandleIntent Called");
        if (intent != null) {
            Log.d(LOG_TAG,"Intent hands data");
            final String action = intent.getAction();
            if (ACTION_GET_GEOFENCE.equals(action)) {
                Log.d(LOG_TAG,"ACTION_GET_GEOFENCE Called");
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handleActionGetGeofence(param1);
            } else {
                Log.d(LOG_TAG,"onHandleIntent No Action");
            }
        }
    }

    /**
     * Geofence登録用レストラン情報をぐるなびAPIから取得する
     * paramCategory;お店検索に使用するワード
     */
    private void handleActionGetGeofence(String paramCategory) {
        Log.d(LOG_TAG, "handleActionGetGeofence Called");

        //接続先APIとクエリを生成
        String url = "https://api.gnavi.co.jp/RestSearchAPI/v3/";
        String keyid = "?keyid=";
        String hit_per_page = "&hit_per_page=100";
        String category = "&name=" + paramCategory;
        String range = "&range=5";

        SharedPreferences sharedPreferences = LastLocationPreference.getThisPreference(mContext);
        String latitude = "&latitude=" + Double.longBitsToDouble(sharedPreferences.getLong(LastLocationPreference.LATITUDE.name(), 0));
        String longitude = "&longitude=" + Double.longBitsToDouble(sharedPreferences.getLong(LastLocationPreference.LONGITUDE.name(), 0));

        String queryString = keyid + hit_per_page + latitude + longitude + category + range;
        Log.d(LOG_TAG,"query;" + queryString);

        //HTTP処理用オプジェクト作成
        OkHttpClient client = new OkHttpClient();

        //送信用リクエストを作成
        Request request = new Request.Builder().url(url + queryString).get().build();

        //受信用オブジェクトを作成
        Call call = client.newCall(request);
        String result = "";

        int responseCode = 0;

        //送信と受信
        try {
            Response response = call.execute();
            responseCode = response.code();
            Log.d(LOG_TAG,"ResponseCode:" + response.code());

            if(responseCode != 200) {
                Log.d(LOG_TAG,"responseError");
                return;
            }

            ResponseBody body = response.body();
            if (body != null) {
                result = body.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**JSONから配列を生成*/
        RestaurantJsonParser geofenceJsonParser = new RestaurantJsonParser();
        ArrayList<RestaurantInfo> restaurantInfoArrayList = geofenceJsonParser.jsonRestParser(result);

        /**配列をGeofence情報DBに保存*/
        RestaurantDatabaseHelper restrauntDatabaseHelper = RestaurantDatabaseHelper.getInstance(mContext);
        restrauntDatabaseHelper.insertInformation(restaurantInfoArrayList);

        /**保存完了をNotificationで通知する*/
        NotificationManager manager = mContext.getSystemService(NotificationManager.class);
        createNotificationChannel(manager);

        // プロセスがあればそのまま復帰、なければ起動画面から開始する
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(mContext.getPackageName(), NavigationTopActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(GEOFENCE_SERVICE_FLAG,true);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("Geofence登録用情報の保存が完了しました")
                .setContentText("一覧で使用可能なGeofenceを確認しましょう。")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_geo);
        NotificationManagerCompat.from(mContext).notify(0, notification.build());
    }

    //AndroidOreo 向けにNotificationChannelを設定
    private void createNotificationChannel(NotificationManager manager) {
        Log.d(LOG_TAG,"createNotificationChannel Called");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("description");
                manager.createNotificationChannel(channel);
            }
        }
    }
}
