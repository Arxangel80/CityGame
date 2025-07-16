package com.example.citygame.faeture.important_alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.citygame.R

fun showFullScreenNotification(context: Context) {
    Log.d("Notification", "Showing full screen notification")


    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "important_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Important Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for very important alerts"
            enableLights(true)
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    val fullScreenIntent = Intent(context, ImportantActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    val fullScreenPendingIntent = PendingIntent.getActivity(
        context, 0, fullScreenIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.cit) // TODO: Заменить на значок
        .setContentTitle("Важное событие") // TODO: Заменить
        .setContentText("Ваша принцесса в другом замке!") // TODO: Заменить
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_CALL)
        .setFullScreenIntent(fullScreenPendingIntent, true)
        .build()

    notificationManager.notify(1001, notification)
}
