package com.example.citygame.mainQuest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

data class ToastEvent(
    val message: String,
    val hint: String,
    val buttonText: String = "Next"
)


class mainQuestNFCViewModel : ViewModel() {
    private var questTag: String? = null
    private var RLEQuestFinished = false
    private var NFCRaceQuestFinished = false
    private var ColorFilterQuestFinished = false

    enum class QuestType {
        RLEQuest,
        NFCRaceQuest,
        ColorFilterQuest
    }

    data class Quest(
        val type: QuestType,
        var isFinished: Boolean = false,
        val hint: String
    )

    private val quests = mutableMapOf(
        QuestType.RLEQuest to Quest(
            QuestType.RLEQuest,
            false,
            "Quest 1 hint."
        ),
        QuestType.NFCRaceQuest to Quest(
            QuestType.NFCRaceQuest,
            false,
            "Quest 2 hint."
        ),
        QuestType.ColorFilterQuest to Quest(
            QuestType.ColorFilterQuest,
            false,
            "Quest 3 hint."
        )
    )


    private val _toastEvent = MutableSharedFlow<ToastEvent>()
    val toastEvent: SharedFlow<ToastEvent> = _toastEvent

    fun onNFCTagScanned(tagMessage: String) {
        when (tagMessage) {
            QuestType.RLEQuest.name -> {
                val quest = quests[QuestType.RLEQuest] ?: return
                emitQuestStatusToast(quest)
            }

            QuestType.NFCRaceQuest.name -> {
                val quest = quests[QuestType.NFCRaceQuest] ?: return
                emitQuestStatusToast(quest)
            }

            QuestType.ColorFilterQuest.name -> {
                val quest = quests[QuestType.ColorFilterQuest] ?: return
                emitQuestStatusToast(quest)
            }

            else -> Log.i("mainQuestNFCViewModel", "Unknown tag: $tagMessage")
        }
    }


    private fun emitQuestStatusToast(quest: Quest) {
        viewModelScope.launch {
            val statusMessage = if (!quest.isFinished) {
                "Квест начат! Выполните мини-задачу"
            } else {
                "Квест завершён! Получите награду 🎉"
            }
            _toastEvent.emit(
                ToastEvent(
                    message = statusMessage,
                    hint = quest.hint,
                    buttonText = "Далее"
                )
            )
        }
    }
}