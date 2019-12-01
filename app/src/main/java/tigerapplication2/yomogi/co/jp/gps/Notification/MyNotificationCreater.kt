package tigerapplication2.yomogi.co.jp.gps.Notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.google.android.gms.location.LocationResult
import tigerapplication2.yomogi.co.jp.gps.R

class MyNotificationCreater {

    companion object {
        fun createForeNotification(context: Context): Notification {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel
                val name = context.getString(R.string.channel_name)
                val descriptionText = context.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel("LocationService", name, importance)
                mChannel.description = descriptionText
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)

                val name1 = context.getString(R.string.channel_name)
                val descriptionText1 = context.getString(R.string.channel_description)
                val importance1 = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel1 = NotificationChannel("LocationCategory", name1, importance1)
                mChannel.description = descriptionText1
                notificationManager.createNotificationChannel(mChannel1)
            }

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(context, "LocationService")
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setContentText(context.getString(R.string.notification_message))
                        .setSmallIcon(R.drawable.icon)
                        .setTicker(context.getString(R.string.ticker_text))
                        .build()
            } else {
                NotificationCompat.Builder(context, "LocationService")
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setContentText(context.getString(R.string.notification_message))
                        .setSmallIcon(R.drawable.icon)
                        .setTicker(context.getString(R.string.ticker_text))
                        .build()
            }

        }

        fun createLocationInfoNotification(context: Context, locationResult: LocationResult): Notification {

            val mapOpenIntent = createGoogleMapIntent(
                    latitude = locationResult.lastLocation.latitude.toString() ,
                    longitude = locationResult.lastLocation.longitude.toString())
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, mapOpenIntent, 0)

            return NotificationCompat.Builder(context, "LocationCategory")
                    .setSmallIcon(R.drawable.ic_geo)
                    .setContentTitle("位置情報を検知しました")
                    .setContentText("Hello World!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()
        }

        private fun createGoogleMapIntent(latitude : String, longitude : String): Intent {
            val uri = Uri.parse("geo:$latitude,$longitude?z=17")
            return Intent(Intent.ACTION_VIEW, uri)
        }
    }
}