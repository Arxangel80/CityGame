package com.example.citygame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class BaseQuestViewModel : ViewModel() {
    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent: SharedFlow<String> = _navigationEvent

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent

    open fun onWin(
        nextQuestFinished: (() -> Unit)? = null,
        navigateTo: String? = null,
        toast: String? = null
    ) {
        viewModelScope.launch {
            toast?.let { _toastEvent.emit(it) }
            nextQuestFinished?.invoke()
            navigateTo?.let { _navigationEvent.emit(it) }
        }
    }

    open fun onLose(toast: String? = null) {
        viewModelScope.launch {
            toast?.let { _toastEvent.emit(it) }
        }
    }
}
