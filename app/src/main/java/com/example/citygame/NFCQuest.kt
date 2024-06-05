package com.example.citygame

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
class NFCActivity : ComponentActivity() {
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                NFCReader()
            }
        }

        // Initialize NFC adapter and pending intent
        val nfcManager = getSystemService(NFC_SERVICE) as NfcManager
        nfcAdapter = nfcManager.defaultAdapter
        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED || intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            tag?.let {
                handleNfcTag(it)
            }
        }
    }

    private fun handleNfcTag(tag: Tag) {
        // Process the NFC tag
        // For simplicity, let's just log the tag information
        // In a real app, you would parse the tag data here
        val tagInfo = tag.id.joinToString(":") { String.format("%02X", it) }
        nfcTagInfo = "NFC Tag ID: $tagInfo"
    }

    companion object {
        var nfcTagInfo by mutableStateOf("")
    }
}

@Composable
fun NFCReader() {
    var nfcTagInfo by remember { mutableStateOf(NFCActivity.nfcTagInfo) }

    // Update the NFC tag info when it changes
    LaunchedEffect(NFCActivity.nfcTagInfo) {
        nfcTagInfo = NFCActivity.nfcTagInfo
    }

    Column {
        Text(text = "NFC Reader")
        Text(text = nfcTagInfo)
    }
}
