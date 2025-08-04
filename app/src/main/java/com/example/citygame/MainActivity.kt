package com.example.citygame

import NFCViewModel
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.citygame.Notification.LocationViewModel
import com.example.citygame.Notification.NotificationUtils
import navigation.AppNavGraph
import java.nio.charset.Charset


enum class Screens() {
    Login,
    Quests,
    Main,
    RLEQuest,
    CipherQuest,
    GestureQuest,
    CardanGrilleQuest,
    NFCQuest,
    Chat,
    FeedBack,
    SuddenMessage
}

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var navController: NavHostController
    private val nfcViewModel: NFCViewModel by viewModels()
    private lateinit var nfcHandler: NfcHandler
    private val locationViewModel by viewModels<LocationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request for all permisions
        PermissionsHelper.requestAllImportantPermissions(this)

        // Get intent from console to start app from specific destination
        val startDestination =
            intent.getStringExtra("startDestination")
                ?: Screens.Login.name // Only for Debug purposes

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcHandler = NfcHandler(this, nfcAdapter) { readMsg ->
            nfcViewModel.setReadedMsg(readMsg)
        }

        setContent {
            navController = rememberNavController()

            val currentBackStackEntry by navController.currentBackStackEntryAsState()

            LaunchedEffect(currentBackStackEntry?.destination?.route) {
                if (currentBackStackEntry?.destination?.route == Screens.NFCQuest.name) {
                    nfcHandler.enableForegroundDispatch()
                } else {
                    nfcHandler.disableForegroundDispatch()
                }
            }

            AppNavGraph(navController = navController, startDestination, nfcViewModel)
        }


        // Track is player inside the playing zone
        locationViewModel.isOutsideZone.observe(this) { isOutside ->
            if (isOutside) {
                NotificationUtils.showLocationNotification(this, "You are out of zone!")
            } else {
                Log.d("LocationViewModel", "You are inside the zone")
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        nfcHandler.onNewIntent(intent)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }
}