package com.example.citygame.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserPreferences {
    private val Context.dataStore by preferencesDataStore(name = "user_prefs")
    private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")

    suspend fun saveAccessToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = token
        }
    }

    val accessTokenFlow: (Context) -> Flow<String?> = {
        it.dataStore.data.map { prefs ->
            prefs[KEY_ACCESS_TOKEN]
        }
    }
}