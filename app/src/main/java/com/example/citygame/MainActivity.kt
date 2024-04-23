package com.example.citygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


enum class QuestScreen() {
    Login,
    Main,
    Quest,
    Info,
    Contact
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController: NavHostController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = QuestScreen.Login.name,
            ) {
                composable(route = QuestScreen.Main.name) {
                    MainScreen()
                }
                composable(route = QuestScreen.Login.name) {
                    LoginScreen(onNextButtonClicked = {navController.navigate(QuestScreen.Main.name)})
                }
            }
        }
    }
}