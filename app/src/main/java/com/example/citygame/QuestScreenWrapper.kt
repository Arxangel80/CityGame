package com.example.citygame

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun QuestScreenWrapper(
    viewModel: BaseQuestViewModel,
    navController: NavController,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Collect toast events
    LaunchedEffect(viewModel) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Collect navigation events
    LaunchedEffect(viewModel) {
        viewModel.navigationEvent.collect { route ->
            navController.navigate(route) {
                popUpTo(route) { inclusive = true }
            }
        }
    }

    content()
}
