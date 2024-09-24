package com.example.citygame.quests.rleQuest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.citygame.utils.BaseQuestViewModel
import com.example.citygame.utils.Quests
import com.example.citygame.navigation.AppScreens

class RLEQuestViewModel(
) : BaseQuestViewModel() {
    companion object {
        const val HEIGHT: Int = 1
        const val WIDTH: Int = 3
    }

    private val patterns = listOf(
        RLEPatterns.P,
        RLEPatterns.U,
        RLEPatterns.T
    )

    val gridState: SnapshotStateList<Boolean> = mutableStateListOf()
    var currentPatternIndex by mutableIntStateOf(0)
        private set


    init {
        resetGrid()
    }

    fun toggleCell(index: Int) {
        gridState[index] = !gridState[index]
        checkPattern()
    }

    private fun resetGrid() {
        gridState.clear()
        repeat(HEIGHT * WIDTH) {
            gridState.add(false)
        }
    }

    private fun checkPattern() {
        val pattern = patterns[currentPatternIndex]
        if (gridState.size == pattern.size && gridState.zip(pattern)
                .all { it.first == it.second }
        ) {
            if (currentPatternIndex == patterns.size - 1) {
                win()
            } else {
                currentPatternIndex++
                resetGrid()
            }
        }
    }

    fun win() {
        onWin(
            nextQuestFinished = { Quests.markMiniQuestFinished(Quests.MainQuest3.miniQuest.name) },
            navigateTo = AppScreens.ReturnScreen.NAME,
            toast = "Вы успешно справились с заданием!",
            quest = Quests.MainQuest3.miniQuest.name
        )
    }
}