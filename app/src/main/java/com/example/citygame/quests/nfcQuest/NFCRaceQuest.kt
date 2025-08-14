package com.example.citygame.quests.nfcQuest

import NFCRaceViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.citygame.QuestScreenWrapper


@Composable
fun NFCRaceQuest(viewModel: NFCRaceViewModel, navController: NavController) {
    val readedMsg by viewModel.readedMsg.observeAsState(null)

    val currentIndex by viewModel.currentCheckpointIndex.observeAsState(0)
    val timeLeft by viewModel.timeLeft.observeAsState(0)
    QuestScreenWrapper(viewModel, navController) {


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
}
