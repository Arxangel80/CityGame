package com.example.citygame.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citygame.data.SocketEvent
import com.example.citygame.data.SocketManager
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatViewModel : ViewModel() {

    var message by mutableStateOf("")
        private set

    var messages by mutableStateOf(listOf<String>())
        private set

    init {
        viewModelScope.launch {
            SocketManager.socketEvents.collect { event ->
                if (event is SocketEvent.ChatMessage) {
                    messages = messages + "${event.userName}: ${event.message}"
                }
            }
        }
    }

    fun onMessageChange(newMessage: String) {
        message = newMessage
    }

    fun sendMessage() {
        val trimmed = message.trim()
        if (trimmed.isNotEmpty()) {
            messages = messages + "Me: $trimmed"

            SocketManager.sendMessage(trimmed)
            message = ""
        }
    }

    override fun onCleared() {
        super.onCleared()
        SocketManager.disconnect()
    }

}
