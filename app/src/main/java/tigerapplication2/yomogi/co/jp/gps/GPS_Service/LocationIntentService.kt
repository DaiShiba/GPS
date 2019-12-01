package tigerapplication2.yomogi.co.jp.gps.GPS_Service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log
import com.google.android.gms.location.LocationResult
import tigerapplication2.yomogi.co.jp.gps.GPS_Service.FLPLocationManager.OnLocationResultListener
import java.text.SimpleDateFormat
import java.util.*

class LocationIntentService : IntentService("LocationIntentService"), OnLocationResultListener {
    @SuppressLint("SimpleDateFormat")
    override fun onLocationResult(locationResult: LocationResult?) {
        if (locationResult == null) {
            Log.d(LOG_TAG, "取得した位置情報がからのためReturn")
            return
        }
        locationManager!!.stopLocationUpdates()
        Log.d(LOG_TAG, "位置情報取得成功")
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
        Log.d(LOG_TAG, logStr)
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(LOG_TAG, "onHandleIntent Called")
        locationAction()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var locationManager: FLPLocationManager? = null

        private val LOG_TAG = "LocationIntentService"
    }

    private fun locationAction() {
        Log.d(LOG_TAG, "位置情報検知サービスの開始")
        Log.d(LOG_TAG, "locationAction Start")
        locationManager = FLPLocationManager(this, this)
        locationManager!!.startLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "onDestroy Start")
    }
}
