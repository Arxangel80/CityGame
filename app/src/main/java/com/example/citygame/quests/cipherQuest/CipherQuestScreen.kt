package com.example.citygame.quests.cipherQuest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citygame.utils.QuestScreenWrapper
import com.google.android.filament.utils.Bookmark
import com.google.android.gms.maps.model.LatLng

@Composable
fun CipherScreen(navController: NavController) {
    val viewModel: CipherViewModel = viewModel()
    val cipherText by viewModel.cipherText.collectAsState()
    val location by viewModel.location.collectAsState()
    val distance by viewModel.distance.collectAsState()
    val messageDecoded by viewModel.messageDecoded.collectAsState()

    QuestScreenWrapper(viewModel, navController) {
        CipherDrawer(
            cipherText,
            location,
            distance,
            messageDecoded,
            onContinueClicked = { viewModel.win() })
    }
}


@Composable
fun CipherDrawer(
    text: String,
    location: LatLng?,
    distance: Double,
    messageDecoded: Boolean,
    onContinueClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔐 Cipher",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center
            )

            Text(
                text = text,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF444444),
                textAlign = TextAlign.Center
            )

            Divider(color = Color.LightGray, thickness = 1.dp)

            location?.let {
                Text(
                    text = "📍 Latitude: ${it.latitude}",
                    fontSize = 18.sp,
                    color = Color(0xFF555555),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "📍 Longitude: ${it.longitude}",
                    fontSize = 18.sp,
                    color = Color(0xFF555555),
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "📏 Distance: ${"%.2f".format(distance)} m",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00695C),
                textAlign = TextAlign.Center
            )

            if (messageDecoded) {
                Button(
                    onClick = onContinueClicked,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(0.6f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("➡ Next", fontSize = 18.sp)
                }
            }
        }
    }
}
