package com.example.citygame.SessionScreens

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.citygame.R
import com.example.citygame.data.NetworkModule.apiService
import com.example.citygame.data.remote.CreateSessionRequest
import kotlinx.coroutines.launch


class SessionCreationViewModel(
    app: Application,
) : AndroidViewModel(app) {

    val items = mutableStateListOf<GridItem>()
    private val _toastMessage = mutableStateOf<String?>(null)
    val toastMessage: State<String?> = _toastMessage

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

    fun createSession(jwtToken: String) {
        viewModelScope.launch {
            try {
                val response = apiService.createSession(
                    CreateSessionRequest(),
                    token = "Bearer $jwtToken"
                )
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        _toastMessage.value = "Session created! Code: ${body.session.code}"
                    } ?: run {
                        _toastMessage.value = "Empty response from server"
                    }
                } else {
                    _toastMessage.value = "Error creating session: ${response.code()}"
                }
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
