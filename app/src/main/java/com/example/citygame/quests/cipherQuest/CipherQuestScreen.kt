package com.example.citygame.quests.cipherQuest

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citygame.QuestScreenWrapper
import com.example.citygame.locationManager.LocationManager
import com.google.android.gms.maps.model.LatLng
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun CipherScreen(navController: NavController) {
    val viewModel: CipherViewModel = viewModel()
    val cipherText by viewModel.cipherText.collectAsState()
    val location by viewModel.location.collectAsState()
    val distance by viewModel.distance.collectAsState()

    QuestScreenWrapper(viewModel, navController) {
        CipherDrawer(cipherText, location, distance, onContinueClicked = { viewModel.win() })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CipherDrawer(text: String, location: LatLng?, distance: Double, onContinueClicked: () -> Unit) {
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
        Button(onClick = onContinueClicked) { Text("Next") }
    }
}