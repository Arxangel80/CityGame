package com.example.citygame

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageProxy
import com.example.citygame.quests.gestureQuest.GestureRecognizerHelper
import com.google.mediapipe.tasks.vision.core.RunningMode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class GestureRecognizer(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String, Int) -> Unit
) {
    private var gestureRecognizerHelper: GestureRecognizerHelper? = null
    var backgroundExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        setupRecognizer()
    }

    private fun setupRecognizer() {
        backgroundExecutor.execute {
            gestureRecognizerHelper = GestureRecognizerHelper(
                context = context,
                runningMode = RunningMode.LIVE_STREAM,
                gestureRecognizerListener = object :
                    GestureRecognizerHelper.GestureRecognizerListener {
                    override fun onError(error: String, errorCode: Int) {
                        Log.e("GestureRecognizer", "Error: $error")
                        onError(error, errorCode)
                    }

                    override fun onResults(resultBundle: GestureRecognizerHelper.ResultBundle) {
                        val gesture = resultBundle.results.getOrNull(0)
                            ?.gestures()?.firstOrNull()?.firstOrNull()?.categoryName()
                        onResult(gesture ?: "None")
                    }
                }
            )
        }
    }

    fun recognizeHand(imageProxy: ImageProxy) {
        gestureRecognizerHelper?.recognizeLiveStream(imageProxy)
    }

    fun shutdown() {
        backgroundExecutor.shutdown()
    }
}
