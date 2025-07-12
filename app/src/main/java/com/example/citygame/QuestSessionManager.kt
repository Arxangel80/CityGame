import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.AndroidViewModel
import java.util.concurrent.TimeUnit

class QuestSessionManager(private val context: Context) {
    val stepTracker = QuestStepTracker(context as Application)
    val timeTracker = QuestTimeTracker()

    fun startQuest() {
        stepTracker.startTracking()
        timeTracker.startTracking()
    }

    fun stopQuest() {
        stepTracker.stopTracking()
        timeTracker.stopTracking()
    }
}

class QuestStepTracker(app: Application) : AndroidViewModel(app) {
    private val sensorManager = app.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    var stepsSinceStart = mutableIntStateOf(0)
        private set

    private var initialSteps = -1

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                if (initialSteps == -1) {
                    initialSteps = event.values[0].toInt()
                }
                stepsSinceStart.intValue = event.values[0].toInt() - initialSteps
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun startTracking() {
        initialSteps = -1
        stepsSinceStart.intValue = 0
        stepCounterSensor?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopTracking() {
        sensorManager.unregisterListener(sensorListener)
    }

    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }
}


class QuestTimeTracker {
    private var startTime: Long = 0
    private var elapsedSeconds: Long = 0
    private var isTracking: Boolean = false

    fun startTracking() {
        if (!isTracking) {
            startTime = System.currentTimeMillis()
            isTracking = true
        }
    }

    fun stopTracking() {
        if (isTracking) {
            val endTime = System.currentTimeMillis()
            elapsedSeconds += (endTime - startTime)
            isTracking = false
        }
    }

    fun reset() {
        startTime = 0
        elapsedSeconds = 0
        isTracking = false
        println("Tracker reset")
    }

    fun getElapsedSeconds(): Long {
        return if (isTracking) {
            val current = System.currentTimeMillis()
            elapsedSeconds + TimeUnit.MILLISECONDS.toSeconds(current - startTime)
        } else {
            elapsedSeconds
        }
    }

    fun getFormattedElapsed(): String {
        return formatSeconds(getElapsedSeconds())
    }

    private fun formatSeconds(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hrs, mins, secs)
    }
}

