package com.example.citygame

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient


enum class QuestScreen() {
    Login,
    Main,
    Quest,
    RLEQuesth,
    Contact
}

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!checkLocationPermission()) {
            requestLocationPermission()
        }

        super.onCreate(savedInstanceState)
        setContent {
            val navController: NavHostController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = QuestScreen.Main.name
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