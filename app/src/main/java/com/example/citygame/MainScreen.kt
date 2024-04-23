package com.example.citygame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MainScreen() {
    MainDrawer()
}
@Composable
fun MainDrawer() {
    Column(verticalArrangement = Arrangement.Center) {
    Text(
        text = "Ты лох!",
        fontSize = 30.sp,
        color = Color.Black,
        textAlign = TextAlign.Center
    )
    }
}

@Preview(showBackground = true, device = "id:Nexus One", showSystemUi = true)
@Composable
fun MainDrawerPreview() {
    MainDrawer()
}