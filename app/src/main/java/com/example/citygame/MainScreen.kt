package com.example.citygame

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

data class Quest(
    val name: String,
    val coordinates: LatLng
)

object Quests {
    val ARQuest = Quest("AR Quest", LatLng(52.400395, 16.955508))
    val RLEQuest = Quest("RLE Quest", LatLng(52.402395, 16.955508))
    val CesarCipherQuest = Quest("Cesar cipher", LatLng(52.404395, 16.955508))
    val GestureQuest = Quest("Gesture quest", LatLng(52.406395, 16.955508))
    val CardanGrilleQuest = Quest("Cardan grille quest", LatLng(52.408395, 16.955508))
    val NFCQuest = Quest("NFC Quest", LatLng(52.410395, 16.955508))
}
@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, onLocationFetched: (location: LatLng) -> Unit) {
    var loc: LatLng
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                loc = LatLng(latitude,longitude)
                onLocationFetched(loc)
            }
        }
        .addOnFailureListener { exception: Exception ->
            Log.d("MAP-EXCEPTION",exception.message.toString())
        }
}


@Composable
fun QuestMarker(quest: Quest, navTo: () -> Unit) {
    MarkerInfoWindow(
        state = MarkerState(position = quest.coordinates),
        title = quest.name,
        snippet = "Placeholder for quest description",
        onInfoWindowClick = { navTo() },
        content = {
            Surface(shape = RoundedCornerShape(10.dp), color = Color.White) {
                Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(15.dp)) {
                    Text(text = "${it.title}")
                    Text(text = "${it.snippet}")
                    Button(onClick = { }) {
                        Text(text = "Open quest description")
                    }
                }
            }
        }
    )
}
@Composable
fun MainScreen(debugMode: Boolean, navigateToMap: Map<String, () -> Unit>) {
    MainDrawer(debugMode, navigateToMap)

}

@Composable
fun MainDrawer(debugMode: Boolean, navigateToMap: Map<String, () -> Unit>) {
    var showMap by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val content = LocalContext.current

    // Get the last known location
    getCurrentLocation(content) {
        location = it
        showMap = true
    }

    Box (contentAlignment = Alignment.BottomStart, modifier = Modifier.fillMaxSize()) {
    if(showMap) {
        Column {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location, 16f)
            }

            val uiSettings by remember {
                mutableStateOf(
                    MapUiSettings(
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = false
                    )
                )
            }

            GoogleMap(cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = uiSettings,
                onMyLocationButtonClick = {
                    getCurrentLocation(content) {
                        location = it
                        showMap = true
                    }
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 16f)
                    true
                }
            ) {
                navigateToMap["AR Quest"]?.let { QuestMarker(Quests.ARQuest, it) }
                navigateToMap["RLE Quest"]?.let { QuestMarker(Quests.RLEQuest, it) }
                navigateToMap["Cesar Quest"]?.let { QuestMarker(Quests.CesarCipherQuest, it) }
                navigateToMap["Gesture Quest"]?.let { QuestMarker(Quests.GestureQuest, it) }
                navigateToMap["Cardan Grille Quest"]?.let { QuestMarker(Quests.CardanGrilleQuest, it) }
                navigateToMap["NFC Quest"]?.let { QuestMarker(Quests.NFCQuest, it) }

            }
        }
    }
        BottomPullOutMenu(navigateToMap)
    }
    if (debugMode) {
        Surface(color = Color.White) {
                Column {
                    Text("Current coordinates:")
                    Text("Longitude: ${location.longitude}")
                    Text("Latitude: ${location.latitude}")
                }
        }
    }
}

@Composable
fun BottomPullOutMenu(navigateToMap: Map<String, () -> Unit>) {
    var offsetY by remember { mutableFloatStateOf(110f) }
    var expanded by remember { mutableStateOf(false) }
    val descriptionsList =
        arrayOf("AR Quest", "Solve the mystery of visual cypher using the principles of RLE",
            "Solve the cipher with shifting your geoposition",
            "Use your device camera to replicate a sequence gestures",
            "Use Cardan Grille to uncover a secret message in a text",
            "Find hidden NFC tags and read them with your device")

    Box(
        Modifier
            .background(Color.Transparent)
            .alpha(0.8f)
            .fillMaxWidth(0.75f)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    offsetY = (offsetY + dragAmount.y).coerceAtMost(110f)
                    if (offsetY > 0.1f) {
                        expanded = true
                    } else {
                        expanded = false
                        offsetY = 0.1f
                    }
                    change.consumePositionChange()
                }
            }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(navigateToMap.entries.toList()) { (label, onClick) ->
                val index = descriptionsList.indexOf(label)
                val description = if (index >= 0) descriptionsList.getOrElse(index + 1) { "" } else ""
                NavigationItem(label = label, description = description, onClick = onClick)
            }
        }
        }
    }



@Composable
fun NavigationItem(label: String, description: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            color = Color.DarkGray,
            style = MaterialTheme.typography.bodyMedium
        )
    }

}

