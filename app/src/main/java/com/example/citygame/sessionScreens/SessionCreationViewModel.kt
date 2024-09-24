package com.example.citygame.sessionScreens

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.citygame.R
import com.example.citygame.data.NetworkModule
import com.example.citygame.data.SocketManager
import com.example.citygame.data.remote.CreateSessionRequest
import com.example.citygame.navigation.AppScreens.routeForQuest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class SessionCreationViewModel(
    app: Application,
) : AndroidViewModel(app) {

    val items = mutableStateListOf<GridItem>()
    private val _toastMessage = mutableStateOf<String?>(null)
    val toastMessage: State<String?> = _toastMessage

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent: SharedFlow<String> = _navigationEvent

    init {
        loadGridItems()
    }

    private fun loadGridItems() {
        val pictures = listOf(R.drawable.cit, R.drawable.pp, R.drawable.cit)
        val titles = listOf("EiT Faculty game", "Campus game", "Text 2", "Text 3")
        val descriptions = listOf(
            "Experience an immersive mobile quest through the rich history of your alma mater.",
            "Dive into a captivating journey through history of PUT.",
            "Description Text 2",
            "Description Text 3"
        )

        repeat(10) { index ->
            items.add(
                GridItem(
                    picture = pictures[index % pictures.size],
                    title = titles[index % titles.size],
                    description = descriptions[index % descriptions.size]
                )
            )
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun createSession(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.createSession(CreateSessionRequest())
                if (response.isSuccessful) {
                    _toastMessage.value = "Session created!"
                    SocketManager.connect()
                    onSuccess()
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorJson).getString("message")
                    } catch (e: Exception) {
                        "Error: ${response.code()}"
                    }

                    if (errorMessage == "You already have an active session") {
                        try {
                            val currentSession = NetworkModule.apiService.getCurrentSession()
                            if (currentSession.isSuccessful) {
                                currentSession.body()?.let { sessionData ->
                                    val questName = sessionData.quest_name
                                    _navigationEvent.emit(routeForQuest(questName!!))
                                    SocketManager.connect()
                                }
                            } else {
                                Log.e("MainActivity", "Server error: ${currentSession.code()}")
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Network error fetching session: ${e.message}")
                        }
                    } else {
                        _toastMessage.value = errorMessage
                    }
                }
            } catch (e: UnknownHostException) {
                _toastMessage.value = "Нет подключения к интернету"
                onSuccess()
            } catch (e: SocketTimeoutException) {
                _toastMessage.value = "Server does not respond"
                onSuccess()
            } catch (e: IOException) {
                _toastMessage.value = "Ошибка сети: ${e.message}"
                onSuccess()
            } catch (e: Exception) {
                _toastMessage.value = "Network error: ${e.message}"
            }
        }
    }


    data class GridItem(
        val picture: Int,
        val title: String,
        val description: String
    )
}
