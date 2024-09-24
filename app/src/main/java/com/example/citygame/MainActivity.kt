package com.example.citygame

import NFCRaceViewModel
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.auth0.android.jwt.JWT
import com.example.citygame.data.NetworkModule
import com.example.citygame.data.SocketManager
import com.example.citygame.data.local.UserPreferences
import com.example.citygame.utils.PermissionsHelper
import com.example.citygame.locationManager.LocationManager
import com.example.citygame.mainQuest.MainQuestNFCViewModel
import com.example.citygame.notification.LocationViewModel
import com.example.citygame.notification.NotificationUtils
import com.example.citygame.nfcHandler.NfcHandler
import com.example.citygame.navigation.AppNavGraph
import com.example.citygame.navigation.AppScreens
import com.example.citygame.navigation.AppScreens.routeForQuest
import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var navController: NavHostController
    private val nfcRaceViewModel: NFCRaceViewModel by viewModels()
    private val mainQuestNFCViewModel: MainQuestNFCViewModel by viewModels()
    private lateinit var nfcHandler: NfcHandler
    private val locationViewModel by viewModels<LocationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request for all permisionss
        PermissionsHelper.requestAllImportantPermissions(this)

        var startDestination = AppScreens.Login.NAME

        // Load token from storage
        val token = runBlocking { UserPreferences.getAccessToken(this@MainActivity) }
        token?.let {
            val jwt = JWT(it)

            if (!jwt.isExpired(10)) {
                NetworkModule.setToken(it)
                startDestination = AppScreens.SessionCreationScreen.NAME
            }
        }

        runBlocking {
            try {
                val response = NetworkModule.apiService.getCurrentSession()
                if (response.isSuccessful) {
                    response.body()?.let { sessionData ->
                        val sessionActive = sessionData.session_active
                        val questName = sessionData.quest_name

                        if (sessionActive) {
                            startDestination = routeForQuest(questName!!)
                        }

                        SocketManager.connect()
                    }
                } else {
                    Log.e("MainActivity", "Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Network error fetching session: ${e.message}")
            }
        }

        // Get intent from console to start app from specific destination !DEBUG ONLY!
        val consoleIntent = intent.getStringExtra("startDestination")
        if (!consoleIntent.isNullOrEmpty()) {
            startDestination = consoleIntent
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcHandler = NfcHandler(
            this, nfcAdapter,
            onMainTagScanned = { readMsg ->
                mainQuestNFCViewModel.onNFCTagScanned(readMsg)
            },
            onRaceTagScanned = { readMsg ->
                nfcRaceViewModel.onNFCTagScanned(readMsg)
            }
        )

        // Start tracking location
        LocationManager.startLocationTracking(this)

        // Track is player inside the playing zone
        locationViewModel.isOutsideZone.observe(this) { isOutside ->
            if (isOutside) {
                NotificationUtils.showLocationNotification(this, "You are out of playing zone!")
            } else {
                Log.d("LocationViewModel", "You are inside the zone")
            }
        }

        setContent {
            val context = LocalContext.current
            navController = rememberNavController()

            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentScreenName = currentBackStackEntry?.destination?.route?.let { route ->
                route.substringBefore('/')
            } ?: ""


            LaunchedEffect(currentScreenName) {
                nfcHandler.updateCurrentRoute(currentScreenName)

                if (currentScreenName == AppScreens.NFCRaceQuest.NAME || currentScreenName == AppScreens.CompassScreen.NAME || currentScreenName == AppScreens.ReturnScreen.NAME) {
                    nfcHandler.enableForegroundDispatch()
                } else {
                    nfcHandler.disableForegroundDispatch()
                }
            }

            // Processing navigation events
            LaunchedEffect(Unit) {
                mainQuestNFCViewModel.navigationEvent.collect { route ->
                    navController.navigate(route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            // Processing toast messages
            LaunchedEffect(Unit) {
                mainQuestNFCViewModel.toastEvent.collect { event ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }

            AppNavGraph(navController = navController, startDestination, nfcRaceViewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        nfcHandler.onNewIntent(intent)
    }


    override fun onResume() {
        super.onResume()
        nfcHandler.enableForegroundDispatch()
    }


    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocationManager.stopLocationUpdates()
    }
}