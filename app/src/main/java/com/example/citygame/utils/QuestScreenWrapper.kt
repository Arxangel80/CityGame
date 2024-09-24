package com.example.citygame.utils

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.citygame.navigation.AppScreens

@Composable
fun QuestScreenWrapper(
    viewModel: BaseQuestViewModel,
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
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

    Scaffold(
        topBar = {
            AppTopBar(
                navController
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
