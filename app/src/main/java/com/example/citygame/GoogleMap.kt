package com.example.citygame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

var destinations: MutableList<LatLng> = listOf(
    LatLng(52.403591, 16.949860),
    LatLng(52.400065, 16.951362),
    LatLng(52.405003, 16.951345)
).toMutableList()
@Composable
fun GoogleMapDrawer() {
    Column {
        val singapore = LatLng(1.35, 103.87)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(singapore, 10f)
        }
        GoogleMap(cameraPositionState = cameraPositionState) {}
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
fun GoogleMapPreview() {
    GoogleMapDrawer()
}