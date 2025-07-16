package com.example.citygame.quests.cipherQuest

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun CipherScreen() {
    var location by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val content = LocalContext.current

    val destinationCoordinates = LatLng(52.399181, 16.955630)

    val decrypted_text = "Hello world! Thats my cipher quest! Keep going, you are on the right way!"
    var text by remember { mutableStateOf("") }

    var distance by remember { mutableStateOf(0.0) }

    startLocationUpdates(content) { newLocation ->
        if (newLocation != location) {
            location = newLocation
            text = encryptTextBasedOnDistance(decrypted_text, destinationCoordinates, location, 40)

            val latitudeDest = destinationCoordinates.latitude
            val longitudeDest = destinationCoordinates.longitude
            val latitudeCurrent = location.latitude
            val longitudeCurrent = location.longitude


            val x = (latitudeCurrent - latitudeDest) * 111320
            val y = (longitudeCurrent - longitudeDest) * 111320

            distance = sqrt(x * x + y * y)
        }
    }

    CipherDrawer(text, location, distance)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CipherDrawer(text: String, location: LatLng, distance: Double) {
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
            text = "Latitude: " + location.latitude.toString(),
            fontSize = 30.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Longitude: " + location.longitude.toString(),
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

@SuppressLint("MissingPermission")
fun startLocationUpdates(context: Context, onLocationFetched: (location: LatLng) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY, // Requests a tradeoff that is balanced between location accuracy and power usage.
        10000L // Minimum time interval between location updates (in milliseconds)
    ).setMinUpdateIntervalMillis(5000L)
        .setMinUpdateDistanceMeters(1F)
        .build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val loc = LatLng(latitude, longitude)
                    onLocationFetched(loc)
                }
            }
        }
    }

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        null
    )
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
    destCoords: LatLng,
    currentCoords: LatLng,
    DecryptionDistance: Int
): String {
    // Extracting latitude and longitude from desired coordinates
    val latitudeDest = destCoords.latitude
    val longitudeDest = destCoords.longitude
    // Extracting latitude and longitude from desired current coordinates
    val latitudeCurrent = currentCoords.latitude
    val longitudeCurrent = currentCoords.longitude

    // Calculate the difference in coordinates and convert to meters (approximate conversion)
    val x = (latitudeCurrent - latitudeDest) * 111320
    val y = (longitudeCurrent - longitudeDest) * 111320

    // Calculate the Euclidean distance between the current and destination coordinates
    val distance = sqrt(x * x + y * y)

    val max_distance =
        600 // Define a maximum distance beyond which the entire text will be encrypted
    if (distance <= DecryptionDistance) return text // If the current distance is within the decryption distance, return the original text
    else if (distance >= max_distance) return randomCharForType(text) // If the current distance is beyond the maximum distance, fully encrypt the text

    // Calculate the proportion of text to encrypt based on how far the distance is beyond the DecryptionDistance
    val distanceToMinDistance = (distance - DecryptionDistance) / distance
    val charToCipher = (text.length * distanceToMinDistance).toInt()
    Log.d("charToCipher", charToCipher.toString())

    // Encrypt the calculated portion of the text
    val encryptedText = if (charToCipher > 0) {
        val textToCipher = text.takeLast(charToCipher)
        text.dropLast(charToCipher) + randomCharForType(textToCipher)
    } else {
        text // If no characters need to be encrypted, return the original text
    }

    return encryptedText
}