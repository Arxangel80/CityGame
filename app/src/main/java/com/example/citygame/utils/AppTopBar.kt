package com.example.citygame.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citygame.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
) {
    TopAppBar(
        title = { Text("Politechnika Quest") },
        actions = {
            IconButton(onClick = { navController.navigate(AppScreens.MapScreen.NAME) }) {
                Icon(Icons.Default.Map, contentDescription = "Map")
            }
            IconButton(onClick = { navController.navigate(AppScreens.FeedBackScreen.NAME) }) {
                Icon(Icons.Default.BarChart, contentDescription = "Statistics")
            }
            IconButton(onClick = { navController.navigate(AppScreens.ChatScreen.NAME) }) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat")
            }
            IconButton(onClick = { navController.navigate(AppScreens.SettingsScreen.NAME) }) {
                Icon(Icons.Default.Settings, contentDescription = "Logout")
            }
        }
    )
}
