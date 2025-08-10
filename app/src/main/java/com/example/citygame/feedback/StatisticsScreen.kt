package com.example.citygame.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.collections.iterator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen() {
    val viewModel: StatisticsViewModel = viewModel()

    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }

    val totalSteps by viewModel.stepTracker.stepsSinceStart
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Feedback and stats") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatisticsSection(
                questsCompleted = viewModel.questsCompleted,
                totalSteps = totalSteps,
                totalTime = viewModel.formatMillisToHMS(viewModel.totalTime),
                questTimes = viewModel.getStringTimes(),
                lastQuestTime = viewModel.formatMillisToHMS(viewModel.lastQuestTime),
                currentQuest = viewModel.currentQuestName
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Text(
                    text = "You're the best, you have completed the tasks faster than: ${viewModel.percentile}% of users!",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )

            Text(
                "Give your rating to the overall performace",
                style = MaterialTheme.typography.headlineSmall
            )
            Text("Your rating: ${viewModel.rating.toInt()}/5")
            Slider(
                value = viewModel.rating,
                onValueChange = { viewModel.onRatingChange(it) },
                valueRange = 0f..5f,
                steps = 4
            )

            Text(
                "Which of the tasks was the best",
                style = MaterialTheme.typography.headlineSmall
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = viewModel.favoriteQuest,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Choose quest") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    viewModel.questOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                viewModel.onFavoriteQuestSelected(selectionOption)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = {
                viewModel.sendFeedback()
            }) {
                Text("Send feedback")
            }
        }
    }
}


@Composable
fun StatisticsSection(
    questsCompleted: Int,
    totalSteps: Int,
    questTimes: Map<String, String>,
    totalTime: String,
    lastQuestTime: String,
    currentQuest: String?,
) {
    Column {
        Text(
            "Your achievements",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                TrackerStatRow(
                    icon = Icons.Filled.CheckCircle,
                    label = "Quests completed",
                    value = questsCompleted.toString(),
                    iconTint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                TrackerStatRow(
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    label = "Total steps",
                    value = totalSteps.toString(),
                    iconTint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Time:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                for ((quest, time) in questTimes) {
                    if (quest != currentQuest) {
                        QuestTimeRow(quest, time)
                    }
                }
                lastQuestTime.let { time ->
                    QuestTimeRow("Current quest", time)
                }
                Text(
                    "Total time: $totalTime",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

            }
        }
    }
}

@Composable
fun TrackerStatRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color = LocalContentColor.current
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun QuestTimeRow(quest: String, time: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Timer,
            contentDescription = "Time spent",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$quest: $time",
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}


@Preview(showBackground = true, name = "Feedback Screen Preview")
@Composable
fun FeedbackScreenPreview() {
    MaterialTheme {
        StatisticsScreen(
        )
    }
}