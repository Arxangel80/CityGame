package com.example.citygame.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citygame.data.NetworkModule
import com.example.citygame.data.local.UserPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    fun logout(context: Context) {
        viewModelScope.launch {
            try {
                UserPreferences.clear(context)

                val response = NetworkModule.apiService.userLogout()
                if (response.isSuccessful) {
                    Log.d("Logout", "Server logout successful")
                } else {
                    Log.e("Logout", "Server logout failed: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("Logout", "Network error: ${e.message}")
            }

            _logoutEvent.emit(Unit)
        }
    }
}
