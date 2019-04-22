package tigerapplication2.yomogi.co.jp.gps.Activity_Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.LinkedList;
import java.util.List;

import tigerapplication2.yomogi.co.jp.gps.Database.RestaurantDatabaseHelper;
import tigerapplication2.yomogi.co.jp.gps.Database.RestaurantEnum;
import tigerapplication2.yomogi.co.jp.gps.GPS_Service.GeofenceIntentService;
import tigerapplication2.yomogi.co.jp.gps.GPS_Service.GeofenceReceiver;
import tigerapplication2.yomogi.co.jp.gps.Preference.SettingsPreference;
import tigerapplication2.yomogi.co.jp.gps.R;

/**Geofence用Fragment*/
public class GeofenceFragment extends Fragment {
    private static final String LOG_TAG = GeofenceFragment.class.getSimpleName();
    private static final String CATEGORY_PARAM = "ラーメン";

    private Activity activity;
    private static PendingIntent mGeofencePendingIntent;
    //Geofence登録用情報があるかを判定する用のフラグ
    private static boolean GeofenceInfoInserted = false;

    public static GeofenceFragment newInstance() {
        return new GeofenceFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.geofence_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();

        //GeofenceのON・OFF用のスイッチをSharedPrefernceから取得し反映
        Switch sw = activity.findViewById(R.id.geodfence_switch);
        SharedPreferences sharedPreferences = SettingsPreference.getThisPreference(activity);
        boolean swEnabled = sharedPreferences.getBoolean(SettingsPreference.GEOFENCE_ENABLED.name(),false);
        sw.setChecked(swEnabled);

        sw.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    //アプリDBに登録済みGeofenceがあるか確認
                    RestaurantDatabaseHelper restaurantDatabaseHelper =
                            RestaurantDatabaseHelper.getInstance(activity);
                    Cursor cursor = restaurantDatabaseHelper.getRestaurantInfo("");

                    //フラグに反映
                    GeofenceInfoInserted = cursor.getCount() > 0;

                    SharedPreferences sharedPreferences1 = SettingsPreference.getThisPreference(activity);

                    if(isChecked) {
                        //GeofenceInfoInsertedを見てGeofenceOnにするか登録販促のダイアログを出すかを判定
                        if(GeofenceInfoInserted) {
                            //アプリ内DBの情報からGeofenceを登録
                            insertGeofence();
                            sharedPreferences1.edit().putBoolean(SettingsPreference.GEOFENCE_ENABLED.name(),true).apply();
                            Toast.makeText(activity, "Geofenceを有効にしました", Toast.LENGTH_LONG).show();
                        }else {
                            //Geofence登録用情報をぐるなびAPIから取得する通知のダイアログを表示する
                            new AlertDialog.Builder(activity)
                                    .setTitle("お知らせ")
                                    .setMessage("Geofence登録用のおすすめ『ラーメン情報をぐるなびAPIから取得します』。\n取得した情報はアプリに保存されいつでも有効にできます。 \n※パケット通信が発生します。")
                                    .setPositiveButton("登録する", (dialog, which) -> {
                                        //Geofence登録サービスを起動する
                                        GeofenceIntentService.startActionGeofence(activity,CATEGORY_PARAM);
                                    })
                                    .setNegativeButton("登録しない",  ((dialog, which) -> {}))
                                    .show();
                            sw.setChecked(false);
                        }
                    }else{
                        //Geofenceを削除
                        removeGeofence();
                        sharedPreferences1.edit().putBoolean(SettingsPreference.GEOFENCE_ENABLED.name(),false).apply();
                        Toast.makeText(activity,"Geofenceを無効にしました", Toast.LENGTH_LONG).show();
                    }
                }
        );

    }

    /**Geofenceの登録*/
    private void insertGeofence() {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(activity);

        //AndroidOreoのBackground Execution Limitの制限回避
        BroadcastReceiver receiver = new GeofenceReceiver();
        IntentFilter filter = new IntentFilter("com.example.geofence.ACTION_RECEIVE_GEOFENCE");
        activity.registerReceiver(receiver, filter);

        List<Geofence> geofences = new LinkedList<>();

        //アプリ内DBから保存済みお店情報を取得
        RestaurantDatabaseHelper restaurantDatabaseHelper =
                RestaurantDatabaseHelper.getInstance(activity);
        Cursor cursor = restaurantDatabaseHelper.getRestaurantInfo("");

        //Geofence登録用リストを生成
        while(cursor.moveToNext()) {
            Log.d(LOG_TAG,"GeofenceItem");
            Log.d(LOG_TAG,"ID;" + cursor.getString(RestaurantEnum.ID.getColumnId()));
            Log.d(LOG_TAG,"UPDATE;" + cursor.getString(RestaurantEnum.UPDATE.getColumnId()));
            Log.d(LOG_TAG,"NAME;" + cursor.getString(RestaurantEnum.NAME.getColumnId()));
            Log.d(LOG_TAG,"CATEGORY:" + cursor.getString(RestaurantEnum.CATEGORY.getColumnId()));
            Log.d(LOG_TAG,"LATITUDE:" + cursor.getString(RestaurantEnum.LATITUDE.getColumnId()));
            Log.d(LOG_TAG,"LONGITUDE:" + cursor.getString(RestaurantEnum.LONGITUDE.getColumnId()));

            geofences.add(new Geofence.Builder()
                    .setRequestId(cursor.getString(RestaurantEnum.ID.getColumnId()))
                    .setCircularRegion(
                            Double.parseDouble(cursor.getString(RestaurantEnum.LATITUDE.getColumnId())),
                            Double.parseDouble(cursor.getString(RestaurantEnum.LONGITUDE.getColumnId())), 10f)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofences)
                .build();

        final PendingIntent pendingIntent = getGeofencePendingIntent();

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_TAG, "Geofences addOnSuccessListener.onSuccess() Called");

                        //登録済みGeofenceリストに追加
                        mGeofencePendingIntent = pendingIntent;
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(LOG_TAG, "Geofences addOnFailureListener.onFailure() Called");
                    }
                });

    }

    /**Geofenceの削除*/
    private void removeGeofence() {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(activity);
        geofencingClient.removeGeofences(getGeofencePendingIntent());
    }

    /**PendingIntentを返却*/
    private PendingIntent getGeofencePendingIntent() {
        // mGeofencePendingIntentがからの場合は新規に生成して返却
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        mGeofencePendingIntent = PendingIntent.getBroadcast(
                activity,
                0,
                new Intent(activity, GeofenceReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        return mGeofencePendingIntent;
    }
}
