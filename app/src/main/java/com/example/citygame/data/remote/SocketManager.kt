package com.example.citygame.data

import com.example.citygame.data.NetworkModule.authToken
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed class SocketEvent {
    data class ChatMessage(val userName: String, val message: String) : SocketEvent()
    data class SystemEvent(val description: String) : SocketEvent()
}


object SocketManager {
    private const val BASE_URL = "http://192.168.0.13:5000/"
    private var socket: Socket? = null

    private val socketScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _socketEvents = MutableSharedFlow<SocketEvent>(extraBufferCapacity = 50)
    val socketEvents: SharedFlow<SocketEvent> = _socketEvents.asSharedFlow()

    fun connect() {
        if (socket?.connected() == true) return

        val opts = IO.Options().apply { query = "token=$authToken" }
        socket = IO.socket(BASE_URL.removeSuffix("/"), opts)
        socket?.connect()

        socket?.on(Socket.EVENT_CONNECT) {
            socketScope.launch { _socketEvents.emit(SocketEvent.SystemEvent("Connected")) }
        }

        socket?.on(Socket.EVENT_DISCONNECT) {
            socketScope.launch { _socketEvents.emit(SocketEvent.SystemEvent("Disconnected")) }
        }

        socket?.on("chatMessage") { args ->
            if (args.isNotEmpty() && args[0] is JSONObject) {
                val json = args[0] as JSONObject
                val user = json.optString("userName", "unknown")
                val message = json.optString("message", "")
                socketScope.launch { _socketEvents.emit(SocketEvent.ChatMessage(user, message)) }
            }
        }
    }

    fun emitQuestCompleted(questName: String) {
        socket?.let { sock ->
            val send = {
                val json = JSONObject()
                json.put("quest_name", questName)
                sock.emit("QuestCompleted", json)
            }

            if (sock.connected()) {
                send()
            } else {
                sock.connect()
                sock.once(Socket.EVENT_CONNECT) { send() }
            }
        }
    }

    fun sendMessage(message: String) {
        val json = JSONObject().put("message", message)
        socket?.let { sock ->
            val send = { sock.emit("chatMessage", json) }

            if (sock.connected()) {
                send()
            } else {
                sock.connect()
                sock.once(Socket.EVENT_CONNECT) { send() }
            }
        }
    }


    fun emit(event: String, data: Any) {
        socket?.emit(event, data)
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }
}
