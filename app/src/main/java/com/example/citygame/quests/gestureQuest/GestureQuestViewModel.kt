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
import com.example.citygame.data.NetworkModule
import com.example.citygame.data.SocketManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import com.example.citygame.navigation.AppScreens
import com.example.citygame.utils.Quests

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

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent: SharedFlow<String> = _navigationEvent

    val recognizedGesture = MutableStateFlow("No gesture recognized")
    val gesturesList = mutableStateOf<List<String>>(emptyList())

    private var lastGestureJob: Job? = null
    private var lastGesture: String = "None"

    private val correctGestures = listOf("Thumb_Up", "Open_Palm", "Closed_Fist", "Victory")

    fun onGestureRecognized(currentGesture: String) {
        recognizedGesture.value = currentGesture // Update UI

        if (currentGesture == "None") {
            lastGestureJob?.cancel()
            lastGesture = "None"
            return
        }

        // If gesture remain the same we return
        if (currentGesture == lastGesture) {
            return
        } else {
            lastGesture = currentGesture
            lastGestureJob?.cancel()
        }

        lastGestureJob = viewModelScope.launch {
            delay(2000L)

            if (BuildConfig.DEBUG) {
                Log.i("GestureQuest", "Added after delay: $currentGesture")
                Log.i("GestureQuest", "Current gestures: ${gesturesList.value}")
                Log.i("GestureQuest", "Last gestures: $lastGesture")
                Log.i("GestureQuest", "Last job: $lastGestureJob")
            }

            if (lastGesture != currentGesture) {
                return@launch
            }

            gesturesList.value = gesturesList.value + currentGesture

            if (gesturesList.value.size == correctGestures.size) {
                if (gesturesList.value == correctGestures) {
                    win()
                } else {
                    gesturesList.value = emptyList()
                }
            }

            lastGesture = "None"
            lastGestureJob = null
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

    fun win() {
        viewModelScope.launch {
            _navigationEvent.emit(AppScreens.FinalScreen.NAME)

            SocketManager.emitQuestCompleted(Quests.MainQuest5.miniQuest.name)
        }
    }


    override fun onCleared() {
        super.onCleared()
        gestureRecognizer.shutdown()
    }
}
