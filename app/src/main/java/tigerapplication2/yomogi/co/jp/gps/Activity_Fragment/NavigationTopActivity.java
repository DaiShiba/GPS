package tigerapplication2.yomogi.co.jp.gps.Activity_Fragment;

import android.app.NotificationManager;
import android.app.Service;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.location.LocationResult;

import java.text.SimpleDateFormat;
import java.util.Date;

import static tigerapplication2.yomogi.co.jp.gps.Contents.GeofenceContent.GeofenceItem;
import tigerapplication2.yomogi.co.jp.gps.Contents.LocationContent;
import tigerapplication2.yomogi.co.jp.gps.Database.DatabaseHelper;
import tigerapplication2.yomogi.co.jp.gps.Database.RestaurantDatabaseHelper;
import tigerapplication2.yomogi.co.jp.gps.GPS_Service.FLPLocationManager;
import tigerapplication2.yomogi.co.jp.gps.GPS_Service.GeofenceIntentService;
import tigerapplication2.yomogi.co.jp.gps.GPS_Service.GeofenceReceiver;
import tigerapplication2.yomogi.co.jp.gps.GPS_Service.LocationIntentService;
import tigerapplication2.yomogi.co.jp.gps.GPS_Service.LocationService;
import tigerapplication2.yomogi.co.jp.gps.Intent.IntentUtility;
import tigerapplication2.yomogi.co.jp.gps.Preference.LastLocationPreference;
import tigerapplication2.yomogi.co.jp.gps.Preference.SettingsPreference;
import tigerapplication2.yomogi.co.jp.gps.R;

/**NavigationTop画面 */
public class NavigationTopActivity extends AppCompatActivity implements FLPLocationManager.OnLocationResultListener,
        NavigationView.OnNavigationItemSelectedListener, LocationInfoFragment.OnListFragmentInteractionListener, GeofenceListFragment.OnListFragmentInteractionListener {

    private static final String LOG_TAG = NavigationTopActivity.class.getSimpleName();
    private static final String CATEGORY_PARAM = "ラーメン";
    private FLPLocationManager locationManager;
    private static DatabaseHelper mDatabaseHelper;
    public static long lastDate = 0;
    public static double lastLatitude = 0;
    public static double lastLongitude = 0;
    public static double lastAltitude = 0;
    private static Context mContext;
    private static final int LIST_COUNT = 100;
    private static final String GEOFENCE_FLAG = GeofenceReceiver.GEOFENCE_FLAG;
    private static final String GEOFENCE_SERVICE_FLAG = GeofenceIntentService.GEOFENCE_SERVICE_FLAG;
    private MonitorViewModel mViewModel;
    private static final String IMAGE_PATH = "cat_sorry.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_top);
        mContext = this;
        //TOP画面Fragmentを表示
        if (savedInstanceState == null) {
            Log.d(LOG_TAG, "Fragment Init");

            FragmentManager fragmentManager = getSupportFragmentManager();
            MonitorFragment monitorFragment = MonitorFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.container, monitorFragment).commit();
            // FragmentのTransaction処理の完了同期待ち
            fragmentManager.executePendingTransactions();
        }

        //Activityにツールバーをセット
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ドロワーに各種設定
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //位置情報登録用DBの取得
        try {
            mDatabaseHelper = DatabaseHelper.getInstance(this);
            Log.d(LOG_TAG, "SqLiteDB Success");
        } catch (Exception e) {
            Log.d(LOG_TAG, "SqLiteDB Failed");
            finish();
        }

        //位置情報検知用
        mViewModel = ViewModelProviders.of(this).get(MonitorViewModel.class);
        mViewModel.location.observe(this, new Observer<Boolean>(){
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean == null)return;
                if(aBoolean) {
                    startLocationUpdate();
                }else {
                    stopLocationUpdate();
                }
            }
        });

//        //Geofence有効時にサービス開始
//        SharedPreferences sharedPreferences = SettingsPreference.getThisPreference(this);
//        boolean swEnabled = sharedPreferences.getBoolean(SettingsPreference.GEOFENCE_ENABLED.name(),false);
//        Log.d(LOG_TAG,"swEnabled" + swEnabled);
//        if(swEnabled)GeofenceIntentService.startActionGeofence(this,CATEGORY_PARAM);

    }

    /**フォアグラウンド遷移契機で位置情報設定値を取得し、開始の是非を判断*/
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG,"onResume Called");

        if(mBound) {
            return;
        }

        //位置情報検知をONにしている場合は、位置情報検知を再開
        SharedPreferences sharedPreferences = SettingsPreference.getThisPreference(this);
        if(sharedPreferences.getBoolean(SettingsPreference.LOCATION_ENABLED.name(),false)){
            startLocationUpdate();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(LOG_TAG,"onNewIntent Called");

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);

        //GeofenceのNotificationからのアプリ起動・復帰の場合は、位置情報一覧に遷移させる
        if(intent.getBooleanExtra(GEOFENCE_FLAG,false)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Log.d(LOG_TAG, "GEOFENCE_FLAG True");
            LocationInfoFragment locationInfoFragment = LocationInfoFragment.newInstance(LIST_COUNT);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, locationInfoFragment)
                    .commit();

        //Geofence登録通知からの起動はGeofence用一覧に飛ばす
        }else if(intent.getBooleanExtra(GEOFENCE_SERVICE_FLAG, false)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Log.d(LOG_TAG, "GEOFENCE_SERVICE_FLAG True");
            GeofenceListFragment geofenceListFragment = GeofenceListFragment.newInstance("");
            fragmentManager.beginTransaction()
                    .replace(R.id.container, geofenceListFragment)
                    .commit();
        }else{
            Log.d(LOG_TAG, "GEOFENCE_FLAG False");
        }
    }

    /**位置情報検知を開始*/
    public void startLocationUpdate() {
//        locationManager = new FLPLocationManager(this, this);
//        locationManager.startLocationUpdates();
//        Log.d(LOG_TAG,"locationManager.startLocationUpdates(); Called");

        Log.d(LOG_TAG, "位置情報検知サービスの開始");
//        Log.d(LOG_TAG, "LocationIntentService Start");
//        Intent intent = new Intent(mContext, LocationIntentService.class);
//        runOnUiThread(() -> {
//            Log.d(LOG_TAG, "LocationIntentService Start in UiThread");
//            startService(intent);
//        });

        // Bind to LocalService
        Intent intent = new Intent(this, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(LOG_TAG, "LocationService startForeground");
            startForegroundService(intent);
        }else {
            Log.d(LOG_TAG, "LocationService startService");
            startService(intent);
        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    LocationService mService;
    boolean mBound = false;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d(LOG_TAG, "onServiceConnected Called");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(LOG_TAG, "onServiceDisconnected Called");
            mBound = false;
        }
    };

    /**位置情報検知を停止*/
    public void stopLocationUpdate() {
        Log.d(LOG_TAG,"locationManager.stopLocationUpdates() Called");
        mService.stopSelf();
        unbindService(connection);
        mBound = false;
    }

    /**バックキー押下制限 ランチャー画面に遷移*/
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**位置情報検知時(FLPLocationManager)のコールバック*/
    @Override
    public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
            Log.e(LOG_TAG, "# No location data.");
            return;
        }

        // 緯度・経度・高度を取得
        lastDate = locationResult.getLastLocation().getTime();
        lastLatitude = locationResult.getLastLocation().getLatitude();
        lastLongitude = locationResult.getLastLocation().getLongitude();
        lastAltitude = locationResult.getLastLocation().getAltitude();

        //DBに保存
        mDatabaseHelper.insertLocation(locationResult);

        //最終検知情報をSharedPrefernceに保存
        SharedPreferences sharedPreferences = LastLocationPreference.getThisPreference(mContext);
        sharedPreferences.edit().putLong(LastLocationPreference.UPDATE_DATE.name(),lastDate).apply();
        sharedPreferences.edit().putLong(LastLocationPreference.LATITUDE.name(),
                Double.doubleToRawLongBits(lastLatitude)).apply();
        sharedPreferences.edit().putLong(LastLocationPreference.LONGITUDE.name(),
                Double.doubleToRawLongBits(lastLongitude)).apply();
        sharedPreferences.edit().putLong(LastLocationPreference.ALTITUDE.name(),
                Double.doubleToRawLongBits(lastAltitude)).apply();

        //TOP画面のTextViewに検知情報を反映
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TextView textView = findViewById(R.id.fragmentTopText);
        if (textView != null) {
            textView.setText("更新日時" + fmt.format(new Date(lastDate)) +
                    "\nLatitude     : N" + String.valueOf(lastLatitude).replace(".","°") +
                    "\nLongitude  : N" + String.valueOf(lastLongitude).replace(".","°") +
                    "\nAltitude      :  " + String.valueOf(lastAltitude));
        }
    }

    /**Navigationのアイテム選択時のコールバック*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment nextFragment = MonitorFragment.newInstance();

        if (id == R.id.nav_home) {
            Log.d(LOG_TAG, "nav_top");
            nextFragment = MonitorFragment.newInstance();

        } else if (id == R.id.nav_list) {
            Log.d(LOG_TAG, "nav_list");
            nextFragment = LocationInfoFragment.newInstance(LIST_COUNT);

        } else if (id == R.id.nav_geofence) {
            Log.d(LOG_TAG, "nav_geofence");
            nextFragment = GeofenceFragment.newInstance();

        } else if (id == R.id.nav_licence) {
            Log.d(LOG_TAG, "nav_licence");
            nextFragment = OSSLicenceFragment.newInstance();

        } else if (id == R.id.nav_geofence_list) {
            Log.d(LOG_TAG, "nav_geofence_list");
            RestaurantDatabaseHelper restaurantDatabaseHelper =
                    RestaurantDatabaseHelper.getInstance(mContext);
            Cursor cursor = restaurantDatabaseHelper.getRestaurantInfo("");

            if(cursor.getCount() == 0) {
                Log.d(LOG_TAG, "Restaurant Info Mo Inserted yet");
                nextFragment = ImageFragment.newInstance(IMAGE_PATH);
            } else {
                Log.d(LOG_TAG, "Restaurant Info Get Success");
                nextFragment = GeofenceListFragment.newInstance(CATEGORY_PARAM);
            }
        }
        transaction.replace(R.id.container, nextFragment);
        transaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**位置情報一覧Fragmentからのコールバック*/
    @Override
    public void onListFragmentInteraction(LocationContent.LocationItem item) {
        Log.d(LOG_TAG, "onListFragmentInteraction Called");
        IntentUtility.openMapApplication(this,item.latitude,item.longitude);
    }

    /**Geofence一覧からのコールバック*/
    @Override
    public void onListFragmentInteraction(GeofenceItem item) {
        Log.d(LOG_TAG, "onListFragmentInteraction Called By Geofence");
        IntentUtility.openMapApplicationByInfo(this,item.name,item.latitude,item.longitude);
    }
}
