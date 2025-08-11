package com.example.citygame.mainQuest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citygame.Quests
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

data class ToastEvent(
    val message: String,
    val hint: String,
    val buttonText: String = "Next"
)


class MainQuestNFCViewModel : ViewModel() {
    private val _toastEvent = MutableSharedFlow<ToastEvent>()
    val toastEvent: SharedFlow<ToastEvent> = _toastEvent

    fun onNFCTagScanned(tagMessage: String) {
        val quest = Quests.allQuests.find { it.name == tagMessage }
        if (quest != null) {
            emitQuestStatusToast(quest)
        } else {
            Log.i("MainQuestNFCViewModel", "Unknown tag: $tagMessage")
        }
    }

    private fun emitQuestStatusToast(quest: Quests.Quest) {
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

    fun markQuestAsFinished(questName: String) {
        Quests.allQuests.find { it.name == questName }?.isFinished = true
    }
}
