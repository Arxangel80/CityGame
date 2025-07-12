import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.citygame.CityGameApp

class FeedbackViewModel(private val context: Context) : ViewModel() {
    val app = context.applicationContext as CityGameApp

    var questsCompleted by mutableIntStateOf(0)
    var totalSteps by mutableIntStateOf(0)
    var questTimes by mutableStateOf(
        listOf("Quest 1: 15 min", "Quest 2: 25 min", "Quest 3: 10 min")
    )
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

    fun sendFeedback() {
//      TODO: SEND FEEDBACK TO SERVER
        println("Stats: Quests: $questsCompleted, Steps $totalSteps")
        println("Rating: $rating")
        println("Favourite quest: $favoriteQuest")
    }
}
