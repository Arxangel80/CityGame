import androidx.lifecycle.MutableLiveData
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import com.example.citygame.utils.BaseQuestViewModel
import com.example.citygame.utils.Quests
import navigation.AppScreens

class NFCRaceViewModel : BaseQuestViewModel() {
    private var _readedMsg = MutableLiveData<String>()
    val readedMsg = _readedMsg

    fun setReadedMsg(msg: String) {
        _readedMsg.value = msg
    }

    private val checkpoints = listOf("NFC1", "NFC2", "NFC3", "NFC4")
    private val CHECKPOINT_TIMERS = listOf(
        30,  // Time from NFC1 to NFC2
        45,  // Time from NFC2 to NFC3
        60,  // Time from NFC3 to NFC4
    )

    private val _currentCheckpointIndex = MutableLiveData(0)
    val currentCheckpointIndex: LiveData<Int> = _currentCheckpointIndex

    private val _timeLeft = MutableLiveData<Int>()
    val timeLeft: LiveData<Int> = _timeLeft

    private var timer: CountDownTimer? = null

    fun onNFCTagScanned(tagContent: String) {
        setReadedMsg(tagContent)

        val index = _currentCheckpointIndex.value ?: 0

        if (tagContent == checkpoints.getOrNull(index)) {
            val nextIndex = index + 1
            if (nextIndex < checkpoints.size) {
                // Continue playing if not the last checkpoint
                _currentCheckpointIndex.value = nextIndex
                startTimerForCheckpoint(index)
            } else {
                _currentCheckpointIndex.value = checkpoints.size
                stopTimer()
                win()
            }
        }
    }

    private fun startTimerForCheckpoint(index: Int) {
        val seconds = CHECKPOINT_TIMERS.getOrNull(index) ?: return
        timer?.cancel()
        _timeLeft.value = seconds

        timer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                lose()
            }
        }.start()
    }

    fun win() {
        onWin(
            nextQuestFinished = { Quests.markMiniQuestFinished(Quests.MainQuest1.miniQuest.name) },
            navigateTo = AppScreens.WinScreen.NAME,
            toast = "Вы успели — вы не лох 😎 \n Идите на следующий квест "
        )
    }

    fun lose() {
        onLose(
            toast = "Вы не успели — вы лох 😢 \n Начинайте заново"
        )
    }

    private fun stopTimer() {
        timer?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}

