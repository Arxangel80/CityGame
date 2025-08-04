package com.example.citygame.quests.nfcQuest

import NFCRaceViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext


@Composable
fun NFCRaceQuest(nfcRaceViewModel: NFCRaceViewModel) {
    val readedMsg by nfcRaceViewModel.readedMsg.observeAsState(null)
    val context = LocalContext.current

    val currentIndex by nfcRaceViewModel.currentCheckpointIndex.observeAsState(0)
    val timeLeft by nfcRaceViewModel.timeLeft.observeAsState(0)

    LaunchedEffect(Unit) {
        nfcRaceViewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

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

        Text("Текущая метка: ${currentIndex + 1}")
        Text("Оставшееся время: $timeLeft сек")
    }
}
