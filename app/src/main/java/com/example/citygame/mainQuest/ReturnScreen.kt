package com.example.citygame.mainQuest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.citygame.BuildConfig
import com.example.citygame.navigation.AppScreens
import com.example.citygame.utils.AppTopBar
import com.example.citygame.utils.Quests

@Composable
fun ReturnScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                navController
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFEDF7ED))
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "🎉 Congratulation! 🎉",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                Text(
                    text = "You completed the task!\nReturn to the NFC tag.",
                    fontSize = 18.sp,
                    color = Color(0xFF1B5E20),
                    textAlign = TextAlign.Center
                )
                if (BuildConfig.DEBUG) {
                    Button(onClick = {
                        val currentQuest = Quests.mainQuests[Quests.currentMainQuestIndex]

                        navController.navigate(AppScreens.HintScreen.route(currentQuest.hint))
                    }) {
                        Text("Next")
                    }
                }
            }
        }
    }
}
