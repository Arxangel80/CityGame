package com.example.citygame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun GestureQuestScreen() {
    val lensFacing = CameraSelector.LENS_FACING_FRONT
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = Preview.Builder().build()
    val previewView = remember {
        PreviewView(context)
    }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    lateinit var backgroundExecutor: ExecutorService
    var gestureRecognizerHelper by remember { mutableStateOf<GestureRecognizerHelper?>(null) }
    var imageAnalyzer: ImageAnalysis? = null

    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

    var recognizerGest by remember { mutableStateOf("No gesture recognized") }

    LaunchedEffect(lensFacing) {
        backgroundExecutor = Executors.newSingleThreadExecutor()

        backgroundExecutor.execute {
            gestureRecognizerHelper = GestureRecognizerHelper(
                context = context,
                runningMode = RunningMode.LIVE_STREAM,
                minHandDetectionConfidence = 0.5F,
                minHandTrackingConfidence = 0.5F,
                minHandPresenceConfidence = 0.5F,
                currentDelegate = 0,
                gestureRecognizerListener = object : GestureRecognizerHelper.GestureRecognizerListener {
                    override fun onError(error: String, errorCode: Int) {
                        Log.e("AAA", "GestureRecognizerHelper onError: $error")
                    }

                    override fun onResults(resultBundle: GestureRecognizerHelper.ResultBundle) {
                        GlobalScope.launch(Dispatchers.Main) {
                            if (resultBundle.results[0].gestures().size > 0) recognizerGest = resultBundle.results[0].gestures()[0][0].categoryName()
                            else recognizerGest = "No gesture recognized"
                        }
                    }
                }
            )
        }

        cameraProvider = context.getCameraProvider()

        imageAnalyzer =
            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(previewView.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(backgroundExecutor) { image ->
                        recognizeHand(image, gestureRecognizerHelper)
                    }
                }

        cameraProvider!!.unbindAll()
        cameraProvider!!.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageAnalyzer)
        preview.setSurfaceProvider(previewView.surfaceProvider)


    }




    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        Text("Recognized gesture: $recognizerGest", color = Color.Black, style = MaterialTheme.typography.titleSmall)
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


fun recognizeHand(imageProxy: ImageProxy, gestureRecognizerHelper: GestureRecognizerHelper?) {
    gestureRecognizerHelper?.recognizeLiveStream(
        imageProxy = imageProxy,
    )
}

class GestureRecognizerHelper(
    var minHandDetectionConfidence: Float = DEFAULT_HAND_DETECTION_CONFIDENCE,
    var minHandTrackingConfidence: Float = DEFAULT_HAND_TRACKING_CONFIDENCE,
    var minHandPresenceConfidence: Float = DEFAULT_HAND_PRESENCE_CONFIDENCE,
    var currentDelegate: Int = DELEGATE_CPU,
    var runningMode: RunningMode = RunningMode.IMAGE,
    val context: Context,
    val gestureRecognizerListener: GestureRecognizerListener? = null
) {

    private var gestureRecognizer: GestureRecognizer? = null

    init {
        setupGestureRecognizer()
    }

    fun setupGestureRecognizer() {
        val baseOptionBuilder = BaseOptions.builder()

        when (currentDelegate) {
            DELEGATE_CPU -> {
                baseOptionBuilder.setDelegate(Delegate.CPU)
            }
            DELEGATE_GPU -> {
                baseOptionBuilder.setDelegate(Delegate.GPU)
            }
        }

        baseOptionBuilder.setModelAssetPath(MP_RECOGNIZER_TASK)

        try {
            val baseOptions = baseOptionBuilder.build()
            val optionsBuilder =
                GestureRecognizer.GestureRecognizerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setMinHandDetectionConfidence(minHandDetectionConfidence)
                    .setMinTrackingConfidence(minHandTrackingConfidence)
                    .setMinHandPresenceConfidence(minHandPresenceConfidence)
                    .setRunningMode(runningMode)

            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder
                    .setResultListener(this::returnLivestreamResult)
                    .setErrorListener(this::returnLivestreamError)
            }
            val options = optionsBuilder.build()
            gestureRecognizer =
                GestureRecognizer.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            gestureRecognizerListener?.onError(
                "Gesture recognizer failed to initialize. See error logs for " + "details"
            )
            Log.e(
                TAG,
                "MP Task Vision failed to load the task with error: " + e.message
            )
        } catch (e: RuntimeException) {
            gestureRecognizerListener?.onError(
                "Gesture recognizer failed to initialize. See error logs for " + "details",
                GPU_ERROR
            )
            Log.e(
                TAG,
                "MP Task Vision failed to load the task with error: " + e.message
            )
        }
    }

    fun recognizeLiveStream(
        imageProxy: ImageProxy,
    ) {
        val frameTime = SystemClock.uptimeMillis()

        val bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
        )
        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

            postScale(
                -1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat()
            )
        }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            matrix,
            true
        )

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        recognizeAsync(mpImage, frameTime)
    }

    @VisibleForTesting
    fun recognizeAsync(mpImage: MPImage, frameTime: Long) {
        gestureRecognizer?.recognizeAsync(mpImage, frameTime)
    }

    fun isClosed(): Boolean {
        return gestureRecognizer == null
    }

    private fun returnLivestreamResult(
        result: GestureRecognizerResult, input: MPImage
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        gestureRecognizerListener?.onResults(
            ResultBundle(
                listOf(result), inferenceTime, input.height, input.width
            )
        )
    }

    private fun returnLivestreamError(error: RuntimeException) {
        gestureRecognizerListener?.onError(
            error.message ?: "An unknown error has occurred"
        )
    }

    companion object {
        val TAG = "GestureRecognizerHelper ${this.hashCode()}"
        private const val MP_RECOGNIZER_TASK = "gesture_recognizer.task"

        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_HAND_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_PRESENCE_CONFIDENCE = 0.5F
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
    }

    data class ResultBundle(
        val results: List<GestureRecognizerResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    interface GestureRecognizerListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResults(resultBundle: ResultBundle)
    }
}
