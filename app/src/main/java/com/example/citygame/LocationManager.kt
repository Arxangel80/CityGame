package com.example.citygame

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

object LocationManager {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, onLocationFetched: (LatLng) -> Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val loc = LatLng(location.latitude, location.longitude)
                    onLocationFetched(loc)
                } else {
                    Log.d("LocationManager", "Last location is null")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("LocationManager", "Failed to get location: ${exception.message}")
            }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context, onLocationChanged: (LatLng) -> Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.Builder(5000L)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val loc = LatLng(location.latitude, location.longitude)
                    onLocationChanged(loc)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, null)
    }

    fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }
}
