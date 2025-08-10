package com.example.citygame.locationManager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


object LocationManager {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val _locationFlow = MutableStateFlow<LatLng?>(null)
    val locationFlow: StateFlow<LatLng?> = _locationFlow
    private lateinit var locationCallback: LocationCallback
    private var isTracking = false
    val interval = 5000L //every 5 seconds


    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    fun getLastLocation(
        context: Context,
        onSuccess: (LatLng) -> Unit,
        onFailure: (Exception) -> Unit = {},
        onNullLocation: () -> Unit = {}
    ) {
        LocationServices.getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    onSuccess(LatLng(it.latitude, it.longitude))
                } ?: run {
                    Log.d("LocationManager", "Last location is unavailable")
                    onNullLocation()
                }
            }
            .addOnFailureListener { e ->
                Log.w("LocationManager", "Failed to get last location", e)
                onFailure(e)
            }
    }


    @SuppressLint("MissingPermission")
    fun startLocationTracking(context: Context) {
        if (isTracking) return

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        isTracking = true

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            interval
        ).apply {
            setMinUpdateDistanceMeters(1F)
            setMinUpdateIntervalMillis(5000L)
            setWaitForAccurateLocation(true)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    _locationFlow.value = LatLng(location.latitude, location.longitude)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        if (::locationCallback.isInitialized && isTracking) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            isTracking = false
        }
    }
}