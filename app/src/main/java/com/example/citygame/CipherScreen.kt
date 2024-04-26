package com.example.citygame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CipherScreen(navController: NavController) {
    CipherDrawer()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CipherDrawer() {
    var answer by rememberSaveable {mutableStateOf("")}
    Column(verticalArrangement = Arrangement.Center) {
        Text(
            text = "Lorem Ipsum",
            fontSize = 30.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        TextField(
            value = answer,
            placeholder = { Text("Your answer", color = Color.White, fontFamily = FontFamily.SansSerif)},
            onValueChange = { data -> answer},
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent,
                textColor = Color.White),
        )

    }
}