package com.example.citygame.mapScreen

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.citygame.BuildConfig
import com.example.citygame.CityGameApp
import com.example.citygame.locationManager.LocationManager
import com.example.citygame.locationManager.LocationManager.getLastLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun MainScreen(
) {
    val app = LocalContext.current.applicationContext as CityGameApp

    LaunchedEffect(Unit) {
        app.trackersManager.startQuest()
    }

    MainDrawer()
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun MainDrawer() {
    val defaultLocation = LatLng(52.40013034832539, 16.955722716173344)
    var lastLocation by remember { mutableStateOf<LatLng?>(defaultLocation) }
    val location by LocationManager.locationFlow.collectAsState(initial = null)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        getLastLocation(
            context = context,
            onSuccess = { lastLocation = it },
            onNullLocation = { lastLocation = defaultLocation },
            onFailure = { lastLocation = defaultLocation }
        )
    }

    Box(contentAlignment = Alignment.BottomStart, modifier = Modifier.fillMaxSize()) {
        Column {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location ?: lastLocation!!, 16f)
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
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(location ?: lastLocation!!, 16f)
                    true
                }
            )
        }
    }

    if (BuildConfig.DEBUG) {
        Surface(
            color = Color.White,
            modifier = Modifier.padding(start = 130.dp)
        )
        {
            Column {
                Text("Current coordinates:")
                Text("Longitude: ${location?.longitude ?: lastLocation!!.longitude}")
                Text("Latitude: ${location?.latitude ?: lastLocation!!.latitude}")
            }
        }
    }
}

