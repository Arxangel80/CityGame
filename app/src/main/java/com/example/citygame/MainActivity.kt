package com.example.citygame

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


enum class QuestScreen() {
    Login,
    Main,
    ARQuest,
    RLEQuest,
    CesarQuest,
    GestureQuest
}

class MainActivity : ComponentActivity() {
    val debugMode: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!checkLocationPermission()) {
            requestLocationPermission()
        }

        super.onCreate(savedInstanceState)
        setContent {
            val navController: NavHostController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = QuestScreen.Login.name
            ) {
                composable(route = QuestScreen.Login.name) {
                    LoginScreen(onNextButtonClicked = {navController.navigate(QuestScreen.Main.name)})
                }
                composable(route = QuestScreen.Main.name) {
                    MainScreen(debugMode,
                        navigateToGestureQuest = {navController.navigate(QuestScreen.GestureQuest.name)},
                        navigateToARQuest = {navController.navigate(QuestScreen.ARQuest.name)},
                        navigateToRLEQuest = {navController.navigate(QuestScreen.RLEQuest.name)},
                        navigateToCesarQuest = {navController.navigate(QuestScreen.CesarQuest.name)})
                }
                composable(route = QuestScreen.ARQuest.name) {
                    ARQuestScreen()
                }
                composable(route = QuestScreen.RLEQuest.name) {
                    RLEQuestScreen(15, 10)
                }
                composable(route = QuestScreen.CesarQuest.name) {
                    CesarQuestScreen()
                }
                composable(route = QuestScreen.GestureQuest.name) {
                    GestureQuestScreen()
                }
            }
        }
    }
    private val PERMISSION_REQUEST_CODE = 123
    private fun checkLocationPermission(): Boolean {
        return (this.checkSelfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && this.checkSelfPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }
    private fun requestLocationPermission() {
        this.requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }
}