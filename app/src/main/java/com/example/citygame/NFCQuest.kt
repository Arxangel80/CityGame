package com.example.citygame

import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import java.nio.charset.Charset

@Composable
fun NFCQuest(readedMsg: String, tag: Tag?) {
    var messageToWrite by remember { mutableStateOf("") }

        Text("Read Message: $readedMsg")
    }

//fun writeNfcTag(tag: Tag?, message: String): Boolean {
//        val ndefRecord = NdefRecord.createTextRecord(null, message)
//        val ndefMessage = NdefMessage(arrayOf(ndefRecord))
//
//        val ndef = Ndef.get(tag)
//        ndef.connect()
//        ndef.writeNdefMessage(ndefMessage)
//        ndef.close()
//
//        return true
//    }
//
