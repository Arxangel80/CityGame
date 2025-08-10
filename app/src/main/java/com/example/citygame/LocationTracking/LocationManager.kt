package com.example.citygame.LocationTracking

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

object LocationManager {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
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
}
