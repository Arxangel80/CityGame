package com.example.citygame.SessionScreens

import android.app.Application
import android.util.Log.e
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.citygame.R
import com.example.citygame.data.NetworkModule.apiService
import com.example.citygame.data.NetworkModule.connectSocket
import com.example.citygame.data.remote.CreateSessionRequest
import kotlinx.coroutines.launch
import org.json.JSONObject


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

    fun createSession(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.createSession(CreateSessionRequest())
                if (response.isSuccessful) {
                    _toastMessage.value = "Session created!"
                    connectSocket()
                    onSuccess()
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorJson).getString("message")
                    } catch (e: Exception) {
                        "Error: ${response.code()}"
                    }
                    _toastMessage.value = errorMessage
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
