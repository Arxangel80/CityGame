package com.example.citygame

import NFCViewModel
import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.nio.charset.Charset
import androidx.compose.runtime.livedata.observeAsState



enum class QuestScreen() {
    Login,
    Quests,
    Main,
    ARQuest,
    RLEQuest,
    CipherQuest,
    GestureQuest,
    CardanGrilleQuest,
    NFCQuest
}

class MainActivity : ComponentActivity() {
    val debugMode: Boolean = false
    private var nfcAdapter : NfcAdapter? = null

    private val nfcViewModel: NFCViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!checkLocationPermission()) {
            requestLocationPermission()
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        var tag: Tag?

        super.onCreate(savedInstanceState)
        setContent {
            val readedMsg by nfcViewModel.readedMsg.observeAsState("No data")

            val navController = rememberNavController()
            DisposableEffect(navController) {
                val callback = NavController.OnDestinationChangedListener { _, destination, _ ->
                    if (destination.route == QuestScreen.NFCQuest.name) {
                        enableNfcForegroundDispatch()
                    } else {
                        disableNfcForegroundDispatch()
                    }
                }
                navController.addOnDestinationChangedListener(callback)
                onDispose {
                    navController.removeOnDestinationChangedListener(callback)
                }
            }
            
            NavHost(
                navController = navController,
                startDestination = QuestScreen.Main.name
            ) {
                composable(route = QuestScreen.Login.name) {
                    LoginScreen(onNextButtonClicked = {navController.navigate(QuestScreen.Quests.name) {
                        popUpTo(0) }
                    })
                }
                composable(route = QuestScreen.Quests.name) {
                    QuestsScreen(navigateToMain = { navController.navigate(QuestScreen.Main.name) })
                }
                composable(route = QuestScreen.Main.name) {
                    MainScreen(debugMode,
                        navigateToGestureQuest = {navController.navigate(QuestScreen.GestureQuest.name)},
                        navigateToARQuest = {navController.navigate(QuestScreen.ARQuest.name)},
                        navigateToRLEQuest = {navController.navigate(QuestScreen.RLEQuest.name)},
                        navigateToCesarQuest = {navController.navigate(QuestScreen.CipherQuest.name)},
                        navigateToCardanGrilleQuest = {navController.navigate(QuestScreen.CardanGrilleQuest.name)},
                        navigateToNFCQuest = {navController.navigate(QuestScreen.NFCQuest.name)})
                }
                composable(route = QuestScreen.ARQuest.name) {
                    ARQuestScreen()
                }
                composable(route = QuestScreen.RLEQuest.name) {
                    RLEQuestScreen(8, 8, debugMode)
                }
                composable(route = QuestScreen.CipherQuest.name) {
                    CipherScreen()
                }
                composable(route = QuestScreen.GestureQuest.name) {
                    GestureQuestScreen()
                }
                composable(route = QuestScreen.CardanGrilleQuest.name) {
                    CardanGrilleQuest()
                }
                composable(route = QuestScreen.NFCQuest.name) {
                    NFCQuest(tag = nfcViewModel.getTagToWrite(), readedMsg = readedMsg)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    private fun enableNfcForegroundDispatch() {
        nfcAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                val nfcIntentFilter = arrayOf(
                    IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
                )

                val pendingIntent =
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                adapter.enableForegroundDispatch(
                    this, pendingIntent, nfcIntentFilter, null
                )
            }
        }
    }

    private fun disableNfcForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }


    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            nfcViewModel.setTagToWrite(intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG))
            if (rawMessages != null) {
                val messages: Array<NdefMessage> = rawMessages.map { it as NdefMessage }.toTypedArray()
                val text = readTextFromMessages(messages)
                nfcViewModel.setReadedMsg(text)
            }
        }
    }

    private fun readTextFromMessages(messages: Array<NdefMessage>): String {
        for (message in messages) {
            for (record in message.records) {
                if (record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(NdefRecord.RTD_TEXT)) {
                    return readText(record)
                }
            }
        }
        return ""
    }

    private fun readText(record: NdefRecord): String {
        val payload = record.payload
        val textEncoding = if ((payload[0].toInt() and 128) == 0) Charset.forName("UTF-8") else Charset.forName("UTF-16")
        val languageCodeLength = payload[0].toInt() and 63
        return String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1, textEncoding)
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
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
            ),
            PERMISSION_REQUEST_CODE
        )
    }
}