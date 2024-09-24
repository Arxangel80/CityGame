package com.example.citygame.compassScreen

import android.annotation.SuppressLint
import android.app.Application
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.citygame.locationManager.LocationManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class CompassViewModel(application: Application) : AndroidViewModel(application),
    SensorEventListener {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(SensorManager::class.java)
    }

    private val _azimuth = mutableFloatStateOf(0f)
    val azimuth: State<Float> = _azimuth

    private val _userLocation = mutableStateOf<Location?>(null)
    val userLocation: State<Location?> = _userLocation

    private val _targetLocation = mutableStateOf(Location(""))

    private val _bearing = mutableFloatStateOf(0f)
    val bearing: State<Float> = _bearing

    init {
        startCompass()
        startLocationUpdates()
    }

    fun setTarget(latitude: Double, longitude: Double) {
        _targetLocation.value = Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }
        calculateBearing()
    }

    private val _isSensorsAvailable = mutableStateOf(true)
    val isSensorsAvailable: State<Boolean> = _isSensorsAvailable

    private var magneticDeclination = 0f // Магнитное склонение


    private fun startCompass() {
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
            ?: run {
                _isSensorsAvailable.value = false
                return
            }

        sensorManager.registerListener(
            this,
            rotationSensor,
            SensorManager.SENSOR_DELAY_GAME
        )

        _userLocation.value?.let {
            magneticDeclination = GeomagneticField(
                it.latitude.toFloat(),
                it.longitude.toFloat(),
                it.altitude.toFloat(),
                System.currentTimeMillis()
            ).declination
        }
    }

    private fun startLocationUpdates() {
        LocationManager.locationFlow
            .onEach { latLng ->
                if (latLng != null) {
                    _userLocation.value = Location("").apply {
                        latitude = latLng.latitude
                        longitude = latLng.longitude
                    }
                    calculateBearing()
                }
            }
            .launchIn(viewModelScope)
    }


    private fun calculateBearing() {
        val user = _userLocation.value ?: return
        val target = _targetLocation.value

        val bearing = user.bearingTo(target)
        _bearing.floatValue = (bearing + 360) % 360
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ROTATION_VECTOR,
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)

                val azimuthDegrees =
                    Math.toDegrees(orientation[0].toDouble()).toFloat() + magneticDeclination
                _azimuth.floatValue = (azimuthDegrees + 360) % 360
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onCleared() {
        sensorManager.unregisterListener(this)
        super.onCleared()
    }
}