package com.example.citygame.mainQuest

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HintScreen(hint: String, onClick: () -> Unit) {
    Column() {
        Text(hint)
        Button(onClick = onClick) {
            Text("Next")
        }
    }
}