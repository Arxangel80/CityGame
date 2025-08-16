package com.example.citygame.quests.colorFiltersQuest

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citygame.utils.QuestScreenWrapper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
fun ColorFiltersQuest(
    viewModel: ColorFiltersViewModel = viewModel(),
    navController: NavController
) {
    var currentScreen by remember { mutableStateOf(ColorQuestScreen.CAMERA) }

    QuestScreenWrapper(viewModel, navController) {
        when (currentScreen) {
            ColorQuestScreen.CAMERA -> CameraScreen(viewModel) {
                currentScreen = ColorQuestScreen.TEXT_FIELDS
            }

            ColorQuestScreen.TEXT_FIELDS -> TextFieldsScreen(
                onBack = { currentScreen = ColorQuestScreen.CAMERA },
                onCheck = { answers -> viewModel.checkAnswers(answers) }
            )
        }
    }
}

@Composable
fun CameraScreen(
    viewModel: ColorFiltersViewModel,
    onGoToTextFields: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var boxWidth by remember { mutableIntStateOf(0) }
    var boxHeight by remember { mutableIntStateOf(0) }

    LaunchedEffect(boxWidth, boxHeight) {
        if (boxWidth == 0 || boxHeight == 0) return@LaunchedEffect

        val cameraProvider = context.getCameraProvider()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        val analyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        analyzer.setAnalyzer(ContextCompat.getMainExecutor(context)) { image ->
            viewModel.analyzeImage(
                image,
                targetWidth = boxWidth,
                targetHeight = boxHeight
            )
        }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, analyzer)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                boxWidth = size.width
                boxHeight = size.height
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        viewModel.frameBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Filters
            Button(onClick = { viewModel.toggleFilter() }, modifier = Modifier.padding(4.dp)) {
                Text(if (viewModel.isFilterEnabled) "Filter ON" else "Filter OFF")
            }

            Row {
                Button(
                    onClick = { viewModel.setChannel(ColorChannel.RED) },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Red")
                }
                Button(
                    onClick = { viewModel.setChannel(ColorChannel.GREEN) },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Green")
                }
                Button(
                    onClick = { viewModel.setChannel(ColorChannel.BLUE) },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Blue")
                }
            }

            Button(onClick = onGoToTextFields, modifier = Modifier.padding(4.dp)) {
                Text("Go to Text Fields")
            }
        }
    }
}

@Composable
fun TextFieldsScreen(
    onBack: () -> Unit,
    onCheck: (List<String>) -> Unit
) {
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    var text3 by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.OutlinedTextField(
            value = text1,
            onValueChange = { text1 = it },
            label = { Text("Field 1") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        androidx.compose.material3.OutlinedTextField(
            value = text2,
            onValueChange = { text2 = it },
            label = { Text("Field 2") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        androidx.compose.material3.OutlinedTextField(
            value = text3,
            onValueChange = { text3 = it },
            label = { Text("Field 3") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onCheck(listOf(text1.trim(), text2.trim(), text3.trim())) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Check answers")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Back to Camera")
        }

    }
}


suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCancellableCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { provider ->
            provider.addListener({
                continuation.resume(provider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }


