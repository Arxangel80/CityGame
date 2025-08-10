package com.example.citygame

import android.app.Application
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GestureViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    private val gestureRecognizer = GestureRecognizer(
        context = context,
        onResult = { onGestureRecognized(it) },
        onError = { error, _ -> { Log.e("gestureRecognizer", error) } }
    )

    val preview = Preview.Builder().build()
    var previewView: PreviewView? = null

    val recognizedGesture = MutableStateFlow("No gesture recognized")
    val recognizedGestures = mutableListOf<String>()
    val recognizedGesturesUI = mutableStateOf<List<String>>(emptyList())

    private var lastGestureJob: Job? = null
    private var lastGesture: String = "None"

    fun onGestureRecognized(gesture: String) {
        recognizedGesture.value = gesture
        if (gesture != "None") {
            if (BuildConfig.DEBUG) {
                Log.i("GestureQuest", recognizedGestures.toString())
            }

            if (lastGesture != gesture) {
                lastGesture = gesture

                lastGestureJob?.cancel()
                lastGestureJob = viewModelScope.launch {
                    delay(2000L)
                    recognizedGestures.add(gesture)
                    recognizedGesturesUI.value = recognizedGesturesUI.value + gesture
                    if (BuildConfig.DEBUG) {
                        Log.i("Gesture", "Added after delay: $gesture")
                    }
                    lastGesture = "None"
                }
            }
        } else {
            lastGestureJob?.cancel()
        }
    }

    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        this.previewView = previewView

        cameraProvider = ProcessCameraProvider.getInstance(context).get()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(previewView.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also {
                it.setAnalyzer(gestureRecognizer.backgroundExecutor) { image ->
                    gestureRecognizer.recognizeHand(image)
                }
            }

        cameraProvider?.unbindAll()
        cameraProvider?.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    override fun onCleared() {
        super.onCleared()
        gestureRecognizer.shutdown()
    }
}
