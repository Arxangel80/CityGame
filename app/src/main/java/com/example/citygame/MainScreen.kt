package com.example.citygame

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
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
fun MainScreen(debugMode: Boolean, navigateToARQuest: () -> Unit, navigateToRLEQuest: () -> Unit, navigateToCesarQuest: () -> Unit,
               navigateToGestureQuest: () -> Unit,
               navigateToCardanGrilleQuest: () -> Unit, navigateToNFCQuest: () -> Unit) {
    MainDrawer(debugMode, navigateToARQuest, navigateToRLEQuest, navigateToCesarQuest, navigateToGestureQuest, navigateToCardanGrilleQuest, navigateToNFCQuest)
}

@Composable
fun MainDrawer(debugMode: Boolean, navigateToARQuest: () -> Unit, navigateToRLEQuest: () -> Unit,
               navigateToCesarQuest: () -> Unit, navigateToGestureQuest: () -> Unit,
               navigateToCardanGrilleQuest: () -> Unit, navigateToNFCQuest: () -> Unit) {
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

            var uiSettings by remember {
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
                QuestMarker(Quests.ARQuest, navigateToARQuest)
                QuestMarker(Quests.RLEQuest, navigateToRLEQuest)
                QuestMarker(Quests.CesarCipherQuest, navigateToCesarQuest)
                QuestMarker(Quests.GestureQuest, navigateToGestureQuest)
                QuestMarker(Quests.CardanGrilleQuest, navigateToCardanGrilleQuest)
                QuestMarker(Quests.NFCQuest, navigateToNFCQuest)
            }
        }
    }
        BottomPullOutMenu()
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
fun BottomPullOutMenu() {
    var offsetY by remember { mutableFloatStateOf(110f) }
    var expanded by remember { mutableStateOf(false) }

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
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .offset(y = offsetY.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = Color.White

            ) {
                HorizontalDivider(modifier = Modifier
                    .offset(y = 8.dp)
                    .padding(horizontal = 10.dp), thickness = 6.dp,color = Color.Black)
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                color = Color.White
            ) {
                Text(
                    text = "Menu Item 2",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    color = Color.Black
                )
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                color = Color.White
            ) {
                Text(
                    text = "Menu Item 3",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    color = Color.Black
                )
            }
        }
    }
}



@Composable
fun NavigationItem(label: String, onClick: () -> Unit) {
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
    }
}
