package com.example.citygame.quests.nfcQuest

import NFCRaceViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.citygame.BuildConfig
import com.example.citygame.utils.QuestScreenWrapper


@Composable
fun NFCRaceQuest(viewModel: NFCRaceViewModel, navController: NavController) {
    val readedMsg by viewModel.readedMsg.observeAsState(null)

    val currentIndex by viewModel.currentCheckpointIndex.observeAsState(0)
    val timeLeft by viewModel.timeLeft.observeAsState(0)
    QuestScreenWrapper(viewModel, navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (readedMsg != null) {
                Text(text = "NFC: $readedMsg")
            } else {
                Text(text = "Scan the NFC-tag")
            }

            Text("Current race stage (tag): ${currentIndex + 1}")
            Text("Time remaining: $timeLeft s.")

            if (BuildConfig.DEBUG) {
                Button(onClick = {
                    val fakeTag = viewModel.checkpoints.getOrNull(currentIndex)
                    if (fakeTag != null) {
                        viewModel.onNFCTagScanned(fakeTag)
                    }
                }) {
                    Text("Next")
                }
            }
        }
    }
}
