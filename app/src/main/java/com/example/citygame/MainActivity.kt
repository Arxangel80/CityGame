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
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
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
    private val nfcViewModel: NFCViewModel by viewModels()
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
        setContent {
            val readedMsg by nfcViewModel.readedMsg.observeAsState("No data")

            val navController = rememberNavController()
            DisposableEffect(navController) {
                val callback = NavController.OnDestinationChangedListener { _, destination, _ ->
                    if (destination.route == Screens.NFCQuest.name) {
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
            AppNavGraph(navController = navController, readedMsg, startDestination)
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

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNfcIntent(intent)
    }

    private fun handleNfcIntent(intent: Intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            nfcViewModel.setTagToWrite(intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG))
            if (rawMessages != null) {
                val messages: Array<NdefMessage> =
                    rawMessages.map { it as NdefMessage }.toTypedArray()
                val text = extractTextFromMessage(messages)
                nfcViewModel.setReadedMsg(text)
            }
        }
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


    private fun extractTextFromMessage(messages: Array<NdefMessage>): String {
        for (message in messages) {
            for (record in message.records) {
                if (record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(NdefRecord.RTD_TEXT)) {
                    return decodeText(record)
                }
            }
        }
        return ""
    }

    private fun decodeText(record: NdefRecord): String {
        val payload = record.payload
        val textEncoding =
            if ((payload[0].toInt() and 128) == 0) Charset.forName("UTF-8") else Charset.forName("UTF-16")
        val languageCodeLength = payload[0].toInt() and 63
        return String(
            payload,
            languageCodeLength + 1,
            payload.size - languageCodeLength - 1,
            textEncoding
        )
    }
}