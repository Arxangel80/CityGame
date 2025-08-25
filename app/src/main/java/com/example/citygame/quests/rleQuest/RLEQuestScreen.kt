package com.example.citygame.quests.rleQuest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citygame.utils.QuestScreenWrapper

@Composable
fun RLEQuestScreen(
    navController: NavController,
) {
    val viewModel: RLEQuestViewModel = viewModel()

    QuestScreenWrapper(viewModel, navController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LazyVerticalGrid(columns = GridCells.Fixed(RLEQuestViewModel.WIDTH)) {
                for (i in 0 until RLEQuestViewModel.HEIGHT * RLEQuestViewModel.WIDTH) {
                    item {
                        Column(
                            modifier = Modifier
                                .clickable { viewModel.toggleCell(i) }
                                .background(if (viewModel.gridState[i]) Color.Black else Color.Green)
                                .border(BorderStroke(1.dp, Color.Red))
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        ) {}
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Прогресс: ${viewModel.currentPatternIndex} / 3")
                LinearProgressIndicator(
                    progress = viewModel.currentPatternIndex / 3f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = Color.Cyan
                )
            }
        }
    }
}
