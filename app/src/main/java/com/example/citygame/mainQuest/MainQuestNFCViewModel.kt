package com.example.citygame.mainQuest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import navigation.AppScreens
import com.example.citygame.utils.Quests
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

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
        val currentQuest = Quests.mainQuests[Quests.currentMainQuestIndex]

        if (tagMessage == currentQuest.name) {
            // Mini quest not finished
            if (!currentQuest.miniQuest.isFinished) {
                // And main quest is not in progress start mini quest
                if (!currentQuest.isInProgress) {
                    viewModelScope.launch {
                        _navigationEvent.emit(currentQuest.route)
                        currentQuest.isInProgress = true
                        _toastEvent.emit(
                            ToastEvent(
                                message = "Квест '${currentQuest.name}' начат! ${currentQuest.miniQuest.description}"
                            )
                        )
                    }
                } else {
                    viewModelScope.launch {
                        _toastEvent.emit(
                            ToastEvent(
                                message = "Квест уже начат! Пройдите мини-квест '${currentQuest.miniQuest.name}'"
                            )
                        )
                    }
                }
            } else {
                viewModelScope.launch {
                    _navigationEvent.emit(AppScreens.HintScreen.route(currentQuest.hint))
                }
            }
        } else {
            viewModelScope.launch {
                _toastEvent.emit(
                    ToastEvent(message = "Эта метка пока не активна!")
                )
            }
        }
    }
}