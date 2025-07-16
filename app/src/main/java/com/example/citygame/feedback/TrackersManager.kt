import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.collections.set

class TrackersManager(private val context: Context) {
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
    private var totalStartTime = 0L
    private var currentQuestStartTime = 0L
    var currentQuestName: String? = null

    val completedQuestTimes = mutableMapOf<String, Long>()

    fun startTracking() {
        totalStartTime = System.currentTimeMillis()
    }

    fun stopTracking() {
        completedQuestTimes.clear()
        currentQuestStartTime = 0L
        currentQuestName = null
        totalStartTime = 0L
    }

    fun addQuestToTrack(questId: String) {
        currentQuestStartTime = System.currentTimeMillis()
        currentQuestName = questId
        completedQuestTimes[questId] = 0L
    }

    fun completeQuest(questId: String) {
        val duration = System.currentTimeMillis() - currentQuestStartTime
        completedQuestTimes[questId] = duration
        currentQuestStartTime = 0L
        currentQuestName = null
    }

    fun totalTime(): Long {
        return System.currentTimeMillis() - totalStartTime
    }

    fun currentQuestElapsedTime(): Long {
        return if (currentQuestStartTime != 0L) {
            System.currentTimeMillis() - currentQuestStartTime
        } else {
            0L
        }
    }

    fun totalTimeFlow(): Flow<Long> = flow {
        while (true) {
            emit(totalTime())
            delay(1000L)
        }
    }

    fun currentTimeFlow(): Flow<Long> = flow {
        while (true) {
            emit(currentQuestElapsedTime())
            delay(1000L)
        }
    }
}

