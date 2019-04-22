package tigerapplication2.yomogi.co.jp.gps.GPS_Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import tigerapplication2.yomogi.co.jp.gps.Activity_Fragment.NavigationTopActivity;
import tigerapplication2.yomogi.co.jp.gps.R;
import tigerapplication2.yomogi.co.jp.gps.Database.RestaurantDatabaseHelper;
import tigerapplication2.yomogi.co.jp.gps.Database.RestaurantEnum;

/**Geofenceイベント受信用レシーバー*/
public class GeofenceReceiver extends BroadcastReceiver {

    private Context mContext;
    private static final String CHANNEL_ID = "channelId";
    private static final String LOG_TAG = GeofenceReceiver.class.getSimpleName();
    public static String GEOFENCE_FLAG = "geofenceFlag";
    private static Cursor cursor;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG,"onReceive Called");

        if(context == null)return;
        mContext = context;

        GeofencingEvent mGeofencingEvent = GeofencingEvent.fromIntent(intent);
        //Geofenceの名前を取得
        List<Geofence> triggeringGeofences = mGeofencingEvent.getTriggeringGeofences();
        String geofenceName = triggeringGeofences.get(0).getRequestId();

        //IDに一致する情報を取得
        RestaurantDatabaseHelper restaurantDatabaseHelper =
                RestaurantDatabaseHelper.getInstance(mContext);
        cursor = restaurantDatabaseHelper.getRestaurantInfoByID(geofenceName);

        if(cursor == null) {
            Log.e(LOG_TAG,"cursor is null");
            return;
        }

        //Geofence 進入・出場かを取得
        int transition = mGeofencingEvent.getGeofenceTransition();

        switch (transition){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                sendNotification("が近い！！");
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                sendNotification("が遠い！！");
                break;
            default:
                sendNotification("error");
        }
    }

    private void sendNotification(String transition){
        Log.d(LOG_TAG,"sendNotification Called");
        NotificationManager manager = mContext.getSystemService(NotificationManager.class);
        createNotificationChannel(manager);

        // プロセスがあればそのまま復帰、なければ起動画面から開始する
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(mContext.getPackageName(), NavigationTopActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(GEOFENCE_FLAG,true);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        cursor.moveToNext();

        //Notificationを生成
        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext,CHANNEL_ID)
                .setContentTitle(cursor.getString(RestaurantEnum.NAME.getColumnId()) + "  " + transition)
                .setContentText("カテゴリ:" + cursor.getString(RestaurantEnum.CATEGORY.getColumnId()))
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
