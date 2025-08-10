package com.example.citygame.quests.cipherQuest

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citygame.locationManager.LocationManager
import com.google.android.gms.maps.model.LatLng
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun CipherScreen() {
    val defaultLocation = LatLng(52.40013034832539, 16.955722716173344)
    val location by LocationManager.locationFlow.collectAsState(initial = defaultLocation)

    val destinationCoordinates = LatLng(52.399181, 16.955630)

    val decryptedText = "Hello world! That's my cipher quest! Keep going, you are on the right way!"
    var text by remember { mutableStateOf("") }

    var distance by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(location) {
        location?.let { newLocation ->
            text =
                encryptTextBasedOnDistance(decryptedText, destinationCoordinates, newLocation, 40)

            val x = (newLocation.latitude - destinationCoordinates.latitude) * 111320
            val y = (newLocation.longitude - destinationCoordinates.longitude) * 111320
            distance = sqrt(x * x + y * y)
        }
    }

    CipherDrawer(text, location, distance)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CipherDrawer(text: String, location: LatLng?, distance: Double) {
    Column(verticalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Cipher: $text",
            fontSize = 30.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Latitude: " + location?.latitude.toString(),
            fontSize = 30.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Longitude: " + location?.longitude.toString(),
            fontSize = 30.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Distance: $distance",
            fontSize = 30.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}


fun randomCharForType(text: String): String {
    val randomText = text.map { char ->
        when {
            char.isLowerCase() -> Random.nextInt(26).let { 'a' + it }
            char.isUpperCase() -> Random.nextInt(26).let { 'A' + it }
            else -> char
        }
    }
    return randomText.joinToString("")
}


fun encryptTextBasedOnDistance(
    text: String,
    destCords: LatLng,
    currentCords: LatLng,
    decryptionDistance: Int
): String {
    // Extracting latitude and longitude from desired coordinates
    val latitudeDest = destCords.latitude
    val longitudeDest = destCords.longitude
    // Extracting latitude and longitude from desired current coordinates
    val latitudeCurrent = currentCords.latitude
    val longitudeCurrent = currentCords.longitude

    // Calculate the difference in coordinates and convert to meters (approximate conversion)
    val x = (latitudeCurrent - latitudeDest) * 111320
    val y = (longitudeCurrent - longitudeDest) * 111320

    // Calculate the Euclidean distance between the current and destination coordinates
    val distance = sqrt(x * x + y * y)

    val maxDistance =
        600 // Define a maximum distance beyond which the entire text will be encrypted
    if (distance <= decryptionDistance) return text // If the current distance is within the decryption distance, return the original text
    else if (distance >= maxDistance) return randomCharForType(text) // If the current distance is beyond the maximum distance, fully encrypt the text

    // Calculate the proportion of text to encrypt based on how far the distance is beyond the DecryptionDistance
    val distanceToMinDistance = (distance - decryptionDistance) / distance
    val charToCipher = (text.length * distanceToMinDistance).toInt()
    Log.d("charToCipher", charToCipher.toString())

    // Encrypt the calculated portion of the text
    val encryptedText = if (charToCipher > 0) {
        val textToCipher = text.takeLast(charToCipher)
        text.dropLast(charToCipher) + randomCharForType(textToCipher)
    } else {
        text // If no characters need to be encrypted, return the original text
    }

    //-\frac{x}{3}+30

    return encryptedText
}