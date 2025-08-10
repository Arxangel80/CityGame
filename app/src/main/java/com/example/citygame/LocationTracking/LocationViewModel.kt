package com.example.citygame.LocationTracking

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val _isOutsideZone = MutableLiveData<Boolean>()
    val isOutsideZone: LiveData<Boolean> = _isOutsideZone

    private var wasOutside = false

    private val center = LatLng(52.40013034832539, 16.955722716173344)
    private val radius = 100f
    private val intervalMillis = 60_000L

    init {
        startLocationTracking()
    }

    private fun startLocationTracking() {
        viewModelScope.launch {
            while (isActive) {
                try {
                    val current = suspendGetCurrentLocation()
                    if (current != null) {
                        val outside = isOutside(current)
                        if (outside && !wasOutside) {
                            _isOutsideZone.postValue(true)
                            wasOutside = true
                        } else if (!outside && wasOutside) {
                            _isOutsideZone.postValue(false)
                            wasOutside = false
                        }
                    }
                } catch (e: Exception) {
                    Log.e("LocationViewModel", "Error getting location: ${e.message}")
                }
                delay(intervalMillis)
            }
        }
    }

    private suspend fun suspendGetCurrentLocation(): LatLng? =
        suspendCancellableCoroutine { cont ->
            LocationManager.getCurrentLocation(context) {
                cont.resume(it)
            }
        }

    private fun isOutside(current: LatLng): Boolean {
        val result = FloatArray(1)
        Location.distanceBetween(
            current.latitude, current.longitude,
            center.latitude, center.longitude,
            result
        )
        return result[0] > radius
    }
}