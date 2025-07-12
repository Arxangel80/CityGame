import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun StatisticsSection(
    questsCompleted: Int,
    totalSteps: Int,
    questTimes: List<String>
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
                StatisticRow(
                    icon = Icons.Filled.CheckCircle,
                    label = "Quests completed",
                    value = questsCompleted.toString(),
                    iconTint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                StatisticRow(
                    icon = Icons.Filled.DirectionsWalk,
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
                questTimes.forEach { time ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = "Time spent",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = time,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun StatisticRow(
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
            modifier = Modifier.size(28.dp) // Увеличим иконку
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen() {
    val context = LocalContext.current
//    val viewModel = remember { AuthViewModel(LocalContext.current) }
    val viewModel = remember { FeedbackViewModel(context) }
    val sessionManager = viewModel.app.questSessionManager

    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }

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
                totalSteps = sessionManager.stepTracker.stepsSinceStart.value,
                questTimes = viewModel.questTimes
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

            Divider(modifier = Modifier.padding(vertical = 16.dp))

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

@Preview(showBackground = true)
@Composable
fun FeedbackScreenPreview() {
    MaterialTheme {
        FeedbackScreen()
    }
}