package com.example.citygame.quests.nfcQuest

import NFCViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NFCQuest(nfcViewModel: NFCViewModel) {
    val readedMsg by nfcViewModel.readedMsg.observeAsState(null)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (readedMsg != null) {
            Text(text = "NFC: $readedMsg")
        } else {
            Text(text = "Поднесите NFC-метку")
        }
    }
}