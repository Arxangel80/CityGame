package com.example.citygame

import android.content.Context
import android.widget.Button
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CardanGrilleQuest() {
// Import necessary libraries and components
    val lensFacing = CameraSelector.LENS_FACING_BACK // Set the camera to use the rear-facing camera
    val lifecycleOwner = LocalLifecycleOwner.current // Get the current lifecycle owner, used to bind the camera lifecycle
    val context = LocalContext.current // Get the current context, needed for initializing camera and views
    val preview = Preview.Builder().build() // Create a camera preview instance
    val previewView = remember { PreviewView(context) } // Remember the PreviewView to display the camera preview

// Variable to track if the grid overlay (grille) is enabled or not
    var isGrilleEnabled by remember { mutableStateOf(true) }

// Build a camera selector to specify which camera lens to use (in this case, the back camera)
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

// LaunchedEffect to bind the camera to the lifecycle when lensFacing changes
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider() // Get the camera provider for the context
        cameraProvider.unbindAll() // Unbind any previously bound use cases
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview) // Bind the camera to the lifecycle with the specified selector and preview
        preview.setSurfaceProvider(previewView.surfaceProvider) // Set the surface provider for the preview to display the camera feed in the PreviewView
    }

// Box layout to contain the camera preview and the grid overlay, with the button at the bottom center
    Box(
        modifier = Modifier.fillMaxSize(), // Make the Box fill the entire available size
        contentAlignment = Alignment.BottomCenter, // Align the content to the bottom center of the Box
    ) {
        // Display the camera preview using AndroidView and fill the entire size of the Box
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // Conditionally display the grid overlay (grille) on top of the camera preview if enabled
        if (isGrilleEnabled) {
            Image(
                painter = painterResource(id = R.drawable.grille), // Load the grille image resource
                contentScale = ContentScale.FillBounds, // Scale the image to fill the bounds of the Box
                contentDescription = "", // No content description needed for the grille
                modifier = Modifier.fillMaxSize(), // Make the Image fill the entire available size
                alpha = 0.7f // Set the transparency of the grille overlay
            )
        }

        // Button to toggle the grid overlay (grille) on and off
        Button(onClick = { isGrilleEnabled = !isGrilleEnabled }, modifier = Modifier.padding(10.dp)) {
            Text(text = "Enable grille") // Button text
        }
    }
}
private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }