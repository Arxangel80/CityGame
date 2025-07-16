package com.example.citygame.quests.nfcQuest

import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Composable
fun NFCQuest(readedMsg: String) {
    Text("Read Message: $readedMsg")
}