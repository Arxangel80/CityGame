import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CompassViewModel(application: Application) : AndroidViewModel(application),
    SensorEventListener {

    private val context = application.applicationContext
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(SensorManager::class.java)
    }
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Состояние View
    private val _azimuth = mutableStateOf(0f)
    val azimuth: State<Float> = _azimuth

    private val _userLocation = mutableStateOf<Location?>(null)
    val userLocation: State<Location?> = _userLocation

    private val _targetLocation = mutableStateOf(Location("").apply {
        latitude = 55.755826 // Москва по умолчанию
        longitude = 37.617300
    })

    // Угол между севером и целью
    private val _bearing = mutableStateOf(0f)
    val bearing: State<Float> = _bearing

    init {
        startCompass()
        requestLocation()
    }

    fun setTarget(latitude: Double, longitude: Double) {
        _targetLocation.value = Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }
        calculateBearing()
    }

    private fun startCompass() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    private fun requestLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    _userLocation.value = location
                    calculateBearing()
                }
            }
        } catch (e: SecurityException) {
            // Обработка ошибки разрешений
        }
    }

    private fun calculateBearing() {
        val user = _userLocation.value ?: return
        val target = _targetLocation.value

        val lat1 = user.latitude.toRadians()
        val lon1 = user.longitude.toRadians()
        val lat2 = target.latitude.toRadians()
        val lon2 = target.longitude.toRadians()

        val y = sin(lon2 - lon1) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(lon2 - lon1)
        var bearing = atan2(y, x).toDegrees()

        // Нормализация (0-360)
        _bearing.value = ((bearing + 360) % 360).toFloat()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)

            // Конвертация радианы -> градусы + нормализация
            val azimuthDegrees = Math.toDegrees(orientation[0].toDouble()).toFloat()
            _azimuth.value = (azimuthDegrees + 360) % 360
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onCleared() {
        sensorManager.unregisterListener(this)
        super.onCleared()
    }

    // Расширения для конвертации
    private fun Double.toRadians() = Math.toRadians(this)
    private fun Double.toDegrees() = Math.toDegrees(this)
}