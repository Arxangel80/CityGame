package com.example.citygame

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "quest_progress")

class QuestProgressManager(private val context: Context) {

    companion object {
        private val KEY_CURRENT_MAIN_INDEX = intPreferencesKey("current_main_index")
        private val KEY_FINISHED_QUESTS = stringSetPreferencesKey("finished_quests")
    }

    // Получение текущего индекса главного квеста
    val currentMainQuestIndexFlow: Flow<Int> = context.dataStore.data.map {
        it[KEY_CURRENT_MAIN_INDEX] ?: 0
    }

    // Получение списка завершённых мини-квестов
    val finishedQuestsFlow: Flow<Set<String>> = context.dataStore.data.map {
        it[KEY_FINISHED_QUESTS] ?: emptySet()
    }

    // Сохранение текущего индекса главного квеста
    suspend fun saveCurrentMainQuestIndex(index: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_CURRENT_MAIN_INDEX] = index
        }
    }

    // Пометить мини-квест завершённым
    suspend fun markQuestFinished(questName: String) {
        context.dataStore.edit { prefs ->
            val updated = (prefs[KEY_FINISHED_QUESTS] ?: emptySet()) + questName
            prefs[KEY_FINISHED_QUESTS] = updated
        }
    }
}
