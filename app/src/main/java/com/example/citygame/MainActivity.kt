package com.example.citygame

import NFCRaceViewModel
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.citygame.locationManager.LocationManager
import com.example.citygame.notification.LocationViewModel
import com.example.citygame.notification.NotificationUtils
import com.example.citygame.mainQuest.mainQuestNFCViewModel
import com.example.citygame.nfcHandler.NfcHandler
import navigation.AppNavGraph


enum class Screens() {
    Login,
    Quests,
    Map,
    RLEQuest,
    CipherQuest,
    GestureQuest,
    CardanGrilleQuest,
    NFCRaceQuest,
    Chat,
    FeedBack,
    SuddenMessage,
    CompassScreen
}

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var navController: NavHostController
    private val nfcRaceViewModel: NFCRaceViewModel by viewModels()
    private val mainQuestNFCViewModel: mainQuestNFCViewModel by viewModels()
    private lateinit var nfcHandler: NfcHandler
    private val locationViewModel by viewModels<LocationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request for all permisionss
        PermissionsHelper.requestAllImportantPermissions(this)

        // Get intent from console to start app from specific destination
        val startDestination =
            intent.getStringExtra("startDestination")
                ?: Screens.CompassScreen.name // Only for Debug purposes

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
                NotificationUtils.showLocationNotification(this, "You are out of zone!")
            } else {
                Log.d("LocationViewModel", "You are inside the zone")
            }
        }

        setContent {
            navController = rememberNavController()

            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route
            LaunchedEffect(currentRoute) {
                nfcHandler.updateCurrentRoute(currentRoute)

                if (currentRoute == Screens.NFCRaceQuest.name || currentRoute == Screens.Map.name) {
                    nfcHandler.enableForegroundDispatch()
                } else {
                    nfcHandler.disableForegroundDispatch()
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