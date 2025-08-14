package com.example.citygame.mainQuest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import navigation.AppScreens
import com.example.citygame.Quests
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder

data class ToastEvent(
    val message: String,
)


class MainQuestNFCViewModel : ViewModel() {
    private val _toastEvent = MutableSharedFlow<ToastEvent>()
    val toastEvent: SharedFlow<ToastEvent> = _toastEvent

    // LiveData for navigation
    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent: SharedFlow<String> = _navigationEvent

    fun onNFCTagScanned(tagMessage: String) {
        val quest = Quests.mainQuests.find { it.name == tagMessage }

        if (quest != null) {
            if (!quest.miniQuest.isFinished) {
                if (!quest.isInProgress) {
                    viewModelScope.launch {
                        _navigationEvent.emit(quest.route)
                        quest.isInProgress = true
                        _toastEvent.emit(
                            ToastEvent(
                                message = "Квест '${quest.name}' начат! ${quest.miniQuest.description}"
                            )
                        )
                    }
                } else {
                    viewModelScope.launch {
                        _toastEvent.emit(
                            ToastEvent(
                                message = "Квест уже начат! Пройдите мини-квест '${quest.miniQuest.name}' перед повторным чтением NFC тега"
                            )
                        )
                    }
                }
            } else {
                viewModelScope.launch {
                    _navigationEvent.emit(AppScreens.HintScreen.route(quest.hint))
                }
            }
        } else {
            Log.i("MainQuestNFCViewModel", "Unknown tag: $tagMessage")
        }
    }
}
