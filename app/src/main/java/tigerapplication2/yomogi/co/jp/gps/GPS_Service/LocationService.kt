package tigerapplication2.yomogi.co.jp.gps.GPS_Service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.google.android.gms.location.LocationResult
import tigerapplication2.yomogi.co.jp.gps.GPS_Service.FLPLocationManager.OnLocationResultListener
import tigerapplication2.yomogi.co.jp.gps.Notification.MyNotificationCreater
import java.text.SimpleDateFormat
import java.util.*

class LocationService : Service() , OnLocationResultListener {
    @SuppressLint("SimpleDateFormat")
    override fun onLocationResult(locationResult: LocationResult?) {
        if (locationResult == null) {
            Log.i(LOG_TAG, "取得した位置情報がからのためReturn")
            return
        }
        Log.i(LOG_TAG, "位置情報取得成功")
        // 緯度・経度・高度を取得
        val lastDate = locationResult.lastLocation.time
        val lastLatitude = locationResult.lastLocation.latitude
        val lastLongitude = locationResult.lastLocation.longitude
        val lastAltitude = locationResult.lastLocation.altitude
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val logStr = "更新日時" + fmt.format(Date(lastDate)) +
                "\nLatitude     : N" + lastLatitude.toString().replace(".", "°") +
                "\nLongitude  : N" + lastLongitude.toString().replace(".", "°") +
                "\nAltitude      :  " + lastAltitude.toString()
        Log.i(LOG_TAG, logStr)

        val notification = MyNotificationCreater.createLocationInfoNotification(this, locationResult)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(3, notification)
        }
    }

    companion object {
        val LOG_TAG = LocationService::class.java.name
        @SuppressLint("StaticFieldLeak")
        private var locationManager: FLPLocationManager? = null
    }

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService() : LocationService = this@LocationService
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(LOG_TAG, "onBind Called")
        locationManager = FLPLocationManager(this, this)
        locationManager!!.startLocationUpdates()
        return binder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "onStartCommand Called")
//        locationManager = FLPLocationManager(this, this)
//        locationManager!!.startLocationUpdates()

        val notification = MyNotificationCreater.createForeNotification(this)
        startForeground(2, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(LOG_TAG, "onUnbind Called")
        locationManager!!.stopLocationUpdates()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy Called")
        super.onDestroy()
    }
}
