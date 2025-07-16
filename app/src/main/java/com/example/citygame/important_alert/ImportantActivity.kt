package com.example.citygame.faeture.important_alert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class ImportantActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Box(
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .background(Color.Companion.Red),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    Text(
                        text = "Внимание! Внимание! Говорит Германия!",
                        color = Color.Companion.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Companion.Bold
                    )
                }
            }
        }
    }
}