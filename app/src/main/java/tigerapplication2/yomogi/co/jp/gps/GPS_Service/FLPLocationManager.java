package tigerapplication2.yomogi.co.jp.gps.GPS_Service;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.location.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import tigerapplication2.yomogi.co.jp.gps.Intent.IntentUtility;

/**位置情報検知用クラス
 * Google開発者サービスの位置情報サービスに接続*/
public class FLPLocationManager extends LocationCallback {
    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private OnLocationResultListener mListener;
    private static final String LOG_TAG = FLPLocationManager.class.getSimpleName();

    public interface OnLocationResultListener {
        void onLocationResult(LocationResult locationResult);
    }

    public FLPLocationManager(Context context, OnLocationResultListener mListener) {
        this.context = context;
        this.mListener = mListener;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        Log.i(LOG_TAG, "onLocationResult called");
        super.onLocationResult(locationResult);
        mListener.onLocationResult(locationResult);

//        if (locationResult == null) {
//            Log.e(LOG_TAG, "# No location data.");
//            return;
//        }
//
//        // 緯度・経度・高度を取得
//        long lastDate = locationResult.getLastLocation().getTime();
//        double lastLatitude = locationResult.getLastLocation().getLatitude();
//        double lastLongitude = locationResult.getLastLocation().getLongitude();
//        double lastAltitude = locationResult.getLastLocation().getAltitude();
//
//        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String str = "更新日時" + fmt.format(new Date(lastDate)) +
//                "\nLatitude     : N" + String.valueOf(lastLatitude).replace(".","°") +
//                "\nLongitude  : N" + String.valueOf(lastLongitude).replace(".","°") +
//                "\nAltitude      :  " + String.valueOf(lastAltitude);
//        Log.e(LOG_TAG, str);
    }

    public void startLocationUpdates() {
        // パーミッションの確認
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG,"パーミッション非許可");
            showPermissionSettingDialog();
            return;
        }

        // 端末の位置情報サービスが無効になっている場合、設定画面を表示して有効化を促す
        if (!isGPSEnabled()) {
            showLocationSettingDialog();
            Log.d(LOG_TAG,"isGPSEnabled Disable");
            return;
        }

        LocationRequest request = new LocationRequest();
        //位置情報取得のインターバルを設定(不安定)
        request.setInterval(50000);
        //位置情報取得の最速インターバルを設定(安定)
        //ここで指定した秒数より早く検知しても、本アプリに通知を送らない
        request.setFastestInterval(10000);
        //位置情報検知のモードを指定
        request.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        fusedLocationProviderClient.requestLocationUpdates(request, this,null);
        Log.d(LOG_TAG,"fusedLocationProviderClient.requestLocationUpdates Called");
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(this);
    }

    /**位置情報検知用プロバイダの取得結果を返却*/
    private Boolean isGPSEnabled() {
        android.location.LocationManager locationManager =
                (android.location.LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    /**位置情報設定をONに推奨する*/
    private void showLocationSettingDialog() {
        new AlertDialog.Builder(context)
                .setTitle("お知らせ")
                .setMessage("位置情報設定画面に遷移します。")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //位置情報設定画面に遷移する
                        IntentUtility.openLocationSettings(context);
                    }
                }).show();
    }

    /**Permission設定画面に遷移させる*/
    private void showPermissionSettingDialog() {
        new AlertDialog.Builder(context)
                .setTitle("お知らせ")
                .setMessage("アプリに必要な権限が付与されていないため、設定画面に遷移します。")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //位置情報設定画面に遷移する
                        IntentUtility.openSettings(context);
                    }
                }).show();
    }
}
