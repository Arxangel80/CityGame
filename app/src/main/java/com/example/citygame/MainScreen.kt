package com.example.citygame

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val NFCQuest = Quest("NFC Quest", LatLng(52.400395, 16.955508))
    val RLEQuest = Quest("RLE Quest", LatLng(52.402395, 16.955508))
    val CipherQuest = Quest("Cipher Quest", LatLng(52.404395, 16.955508))
    val GestureQuest = Quest("Gesture quest", LatLng(52.406395, 16.955508))
    val CardanGrilleQuest = Quest("Cardan grille quest", LatLng(52.408395, 16.955508))
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
                loc = LatLng(latitude, longitude)
                onLocationFetched(loc)
            }
        }
        .addOnFailureListener { exception: Exception ->
            Log.d("MAP-EXCEPTION", exception.message.toString())
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(15.dp)
                ) {
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
fun MainScreen(
    debugMode: Boolean,
    navToQuestsMap: Map<String, () -> Unit>,
    navToChat: () -> Unit,
    navToStats: () -> Unit
) {
    MainDrawer(debugMode, navToQuestsMap, navToChat, navToStats)

}

@Composable
fun MainDrawer(
    debugMode: Boolean,
    navToQuestsMap: Map<String, () -> Unit>,
    navToChat: () -> Unit,
    navToStats: () -> Unit
) {
    var showMap by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val content = LocalContext.current

    // Get the last known location
    getCurrentLocation(content) {
        location = it
        showMap = true
    }

    Box(contentAlignment = Alignment.BottomStart, modifier = Modifier.fillMaxSize()) {
        if (showMap) {
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

                GoogleMap(
                    cameraPositionState = cameraPositionState,
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
                    navToQuestsMap["NFC Quest"]?.let { QuestMarker(Quests.NFCQuest, it) }
                    navToQuestsMap["RLE Quest"]?.let { QuestMarker(Quests.RLEQuest, it) }
                    navToQuestsMap["Cipher Quest"]?.let { QuestMarker(Quests.CipherQuest, it) }
                    navToQuestsMap["Gesture Quest"]?.let { QuestMarker(Quests.GestureQuest, it) }
                    navToQuestsMap["Cardan Grille Quest"]?.let {
                        QuestMarker(
                            Quests.CardanGrilleQuest,
                            it
                        )
                    }

                }
            }
        }
        BottomPullOutMenu(navToQuestsMap)
        ChatButton(navToChat)
    }
    if (debugMode) {
        Surface(
            color = Color.White,
            modifier = Modifier.padding(start = 130.dp)
        )
        {
            Column {
                Text("Current coordinates:")
                Text("Longitude: ${location.longitude}")
                Text("Latitude: ${location.latitude}")

                Button(onClick = {
                    navToStats()
                }) {
                    Text("Stats")
                }

            }
        }
    }
}

@Composable
fun ChatButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = modifier.size(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.chat),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomPullOutMenu(navigateToMap: Map<String, () -> Unit>) {
    val descriptionsList =
        mapOf(
            "RLE Quest" to "Solve the mystery of visual cypher using the principles of RLE",
            "Cipher Quest" to "Solve the cipher with shifting your geoposition",
            "Gesture Quest" to "Use your device camera to replicate a sequence gestures",
            "Cardan Grille Quest" to "Use Cardan Grille to uncover a secret message in a text",
            "NFC Quest" to "Find hidden NFC tags and read them with your device"
        )
    Box(modifier = Modifier.fillMaxWidth(0.8F)) {
        BottomSheetScaffold(
            modifier = Modifier.fillMaxWidth(0.5F),
            sheetContent = {
                navigateToMap.forEach { (label, onClick) ->
                    val description = descriptionsList[label]
                    if (description != null) {
                        NavigationItem(label = label, description = description, onClick = onClick)
                    }
                }
            },
            sheetPeekHeight = BottomSheetDefaults.SheetPeekHeight,
        ) {
        }
    }
}

@Composable
fun NavigationItem(label: String, description: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.LightGray
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
        ) {
            Text(
                text = label,
                color = Color.Black,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = description,
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

