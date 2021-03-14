package com.example.remindr

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.maps.model.LatLng

class GeofenceReceiver : BroadcastReceiver() {
    lateinit var text: String

    override fun onReceive(context: Context?, intent: Intent?) {

        if (context != null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            val geofencingTransition = geofencingEvent.geofenceTransition

            if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                // Retrieve data from intent
                if (intent != null) {
                    text = intent.getStringExtra("message")!!
                    println(text)

                    val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.wire)
                    val smallIcon = BitmapFactory.decodeResource(context.resources, R.drawable.cat)


                    val notificationBuilder = NotificationCompat.Builder(context, FullReminderFragment.CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setContentText("Right on time!")
                            .setLargeIcon(smallIcon)
                            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(largeIcon))
                            //.setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setGroup(FullReminderFragment.CHANNEL_ID)

                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val name = "notification_title"
                        val descriptionText = "Notification_description"
                        val importance = NotificationManager.IMPORTANCE_DEFAULT
                        val channel = NotificationChannel(FullReminderFragment.CHANNEL_ID, name, importance).apply { description=descriptionText }
                        notificationManager.createNotificationChannel(channel)
                    }
                    notificationManager.notify(232, notificationBuilder.build())


                }


            }
        }
    }
}