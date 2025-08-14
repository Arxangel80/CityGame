package com.example.citygame.nfcHandler

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import navigation.AppScreens
import java.nio.charset.Charset

class NfcHandler(
    private val activity: Activity,
    private val nfcAdapter: NfcAdapter?,
    private val onRaceTagScanned: (String) -> Unit,
    private val onMainTagScanned: (String) -> Unit
) {
    var currentScreenName: String? = null

    fun updateCurrentRoute(route: String?) {
        currentScreenName = route
    }

    fun onNewIntent(intent: Intent) {
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMessages != null) {
                val messages = rawMessages.mapNotNull { it as? NdefMessage }.toTypedArray()
                val text = extractTextFromMessages(messages)

                if (text.isBlank()) return

                when (currentScreenName) {
                    AppScreens.NFCRaceQuest.NAME -> onRaceTagScanned(text)
                    AppScreens.CompassScreen.NAME, AppScreens.WinScreen.NAME -> onMainTagScanned(
                        text
                    )
                }

            }
        }

        enableForegroundDispatch()
    }

    fun enableForegroundDispatch() {
        nfcAdapter?.takeIf { it.isEnabled }?.let { adapter ->
            val intentFilters = arrayOf(
                IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
            )
            val pendingIntent = PendingIntent.getActivity(
                activity,
                0,
                Intent(activity, activity.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE
            )

            adapter.enableForegroundDispatch(activity, pendingIntent, intentFilters, null)
        }
    }

    fun disableForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    private fun extractTextFromMessages(messages: Array<NdefMessage>): String {
        for (message in messages) {
            for (record in message.records) {
                if (record.tnf == NdefRecord.TNF_WELL_KNOWN &&
                    record.type.contentEquals(NdefRecord.RTD_TEXT)
                ) {
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