package com.example.citygame

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MainScreen() {
    MainDrawer()
}

@Composable
fun MainDrawer() {
    Column {
        val locationClient: FusedLocationProviderClient

        val singapore = LatLng(1.35, 103.87)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(singapore, 10f)
        }

        var uiSettings by remember { mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false
            )
        ) }


        GoogleMap(cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = uiSettings,
            onMyLocationButtonClick = {
                true
            }
        ) {
            Marker(
                state = MarkerState(position = singapore),
                title = "Singapore",
                snippet = "Marker in Singapore"
            )
        }
        Row {
            Column {
                Text("Current coordinates:")
                Text("Longitude:")
                Text("Latitude:")
                Button(onClick = { /*TODO*/ }) {
                    Text("Open navigation")
                }
            }
            Column {
                Text("Next coordinates:")
                Text("Longitude:")
                Text("Latitude:")
                Button(onClick = { /*TODO*/ }) {
                    Text("Next destination")
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:Nexus One", showSystemUi = true)
@Composable
fun MainDrawerPreview() {
    MainDrawer()
}