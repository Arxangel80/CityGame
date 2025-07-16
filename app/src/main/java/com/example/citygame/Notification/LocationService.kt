package com.example.citygame.Notification

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.example.citygame.LocationManager


class LocationService : Service() {

    private val targetLat = 37.4220
    private val targetLon = -122.0841

    override fun onCreate() {
        super.onCreate()
        startForeground(1, NotificationUtils.buildForegroundNotification(this))

        LocationManager.startLocationUpdates(this) { location ->
            if (isClose(location.latitude, location.longitude)) {
                val notification = NotificationUtils.buildProximityNotification(this)
                NotificationManagerCompat.from(this).notify(2, notification)
            }
        }
    }

    private fun isClose(lat: Double, lon: Double): Boolean {
        val result = FloatArray(1)
        Location.distanceBetween(lat, lon, targetLat, targetLon, result)
        return result[0] < 50
    }

    override fun onDestroy() {
        LocationManager.stopLocationUpdates()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
