package com.example.citygame

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import navigation.AppScreens

@Composable
fun FinalScreen(navController: NavController) {
    Text("Вы победили совсем полностью жёстко")
    Button(onClick = {
        navController.navigate(AppScreens.FeedBackScreen.NAME)
    }) { Text("Статистика") }
}