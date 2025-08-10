package com.example.citygame.notification

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.citygame.locationManager.LocationManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val _isOutsideZone = MutableLiveData<Boolean>()
    val isOutsideZone: LiveData<Boolean> = _isOutsideZone

    private var wasOutside = false

    private val center = LatLng(52.40013034832539, 16.955722716173344)
    private val radius = 100f

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        viewModelScope.launch {
            LocationManager.locationFlow
                .filterNotNull()
                .collect { currentLocation ->
                    checkZoneBoundary(currentLocation)
                }
        }
    }

    private fun checkZoneBoundary(currentLocation: LatLng) {
        val outside = isOutside(currentLocation)
        when {
            outside && !wasOutside -> {
                _isOutsideZone.postValue(true)
                wasOutside = true
            }

            !outside && wasOutside -> {
                _isOutsideZone.postValue(false)
                wasOutside = false
            }
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