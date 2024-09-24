package com.example.citygame.mainQuest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.citygame.navigation.AppScreens
import com.example.citygame.utils.AppTopBar
import com.example.citygame.utils.Quests
import kotlinx.coroutines.launch

@Composable
fun HintScreen(hint: String, navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                navController
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    Quests.currentMainQuestIndex += 1
                    val nextQuest = Quests.mainQuests.first { it.miniQuest.isFinished }
                    navController.navigate(
                        AppScreens.CompassScreen.route(
                            nextQuest.coordinates.latitude.toFloat(),
                            nextQuest.coordinates.longitude.toFloat()
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("Next")
            }
        }
    }
}
