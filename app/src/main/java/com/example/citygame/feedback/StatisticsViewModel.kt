package com.example.citygame.feedback

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.citygame.CityGameApp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class StatisticsViewModel(app: Application) : AndroidViewModel(app) {
    private val myApp = app as CityGameApp
    val stepTracker = myApp.trackersManager.stepTracker
    val timeTracker = myApp.trackersManager.timeTracker

    var questTimes by mutableStateOf<Map<String, Long>>(emptyMap())
        private set
    var totalTime by mutableLongStateOf(0L)
        private set
    var lastQuestTime by mutableLongStateOf(0L)
        private set
    var currentQuestName = timeTracker.currentQuestName


    init {
        viewModelScope.launch {
            timeTracker.totalTimeFlow().collectLatest { time ->
                totalTime = time
                questTimes = timeTracker.completedQuestTimes.toMap()
            }
        }

        viewModelScope.launch {
            timeTracker.currentTimeFlow().collectLatest { time ->
                lastQuestTime = time
            }
        }
    }

    var questsCompleted by mutableIntStateOf(0)

    var percentile by mutableIntStateOf(96)

    var rating by mutableFloatStateOf(0f)
    var favoriteQuest by mutableStateOf("")
    var questOptions = listOf("Q 1", "Q 2", "Q 3", "Q 4", "Q 5")

    fun onRatingChange(newRating: Float) {
        rating = newRating
    }

    fun onFavoriteQuestSelected(quest: String) {
        favoriteQuest = quest
    }

    fun formatMillisToHMS(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun getStringTimes() = questTimes.mapValues { formatMillisToHMS(it.value) }

    fun sendFeedback() {
//      TODO: SEND FEEDBACK TO SERVER
        println("Stats: Quests: $questsCompleted, Steps $stepTracker.stepsSinceStart, Time: $totalTime")
        println("Rating: $rating")
        println("Favourite quest: $favoriteQuest")
    }
}
