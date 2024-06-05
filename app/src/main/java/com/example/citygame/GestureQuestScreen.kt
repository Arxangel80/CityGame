package com.example.citygame

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutioncore.CameraInput
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView
import com.google.mediapipe.solutions.hands.HandLandmark
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsOptions
import com.google.mediapipe.solutions.hands.HandsResult

@Composable
fun GestureQuestScreen() {
    val context = LocalContext.current
    val handsOptions:HandsOptions =
    HandsOptions.builder()
        .setStaticImageMode(false)
        .setMaxNumHands(2)
        .setRunOnGpu(true).build();
    val hands = Hands(context, handsOptions)
    hands.setErrorListener({message, e -> Log.e(TAG, "MediaPipe Hands error:" + message})

    // Initializes a new CameraInput instance and connects it to MediaPipe Hands Solution.
    val cameraInput: CameraInput = CameraInput(this);
    cameraInput.setNewFrameListener { textureFrame -> hands.send(textureFrame) };

    // Initializes a new GlSurfaceView with a ResultGlRenderer<HandsResult> instance
    // that provides the interfaces to run user-defined OpenGL rendering code.
    // See mediapipe/examples/android/solutions/hands/src/main/java/com/google/mediapipe/examples/hands/HandsResultGlRenderer.java
    // as an example.
    val glSurfaceView: SolutionGlSurfaceView<HandsResult> = SolutionGlSurfaceView(context, hands.getGlContext(), hands.getGlMajorVersion());
    glSurfaceView.setSolutionResultRenderer(new HandsResultGlRenderer())
    glSurfaceView.setRenderInputImage(true);

    hands.setResultListener { handsResult ->
        if (handsResult.multiHandLandmarks().isEmpty()) {
            return@setResultListener
        }
        var wristLandmark: LandmarkProto.NormalizedLandmark =
        handsResult.multiHandLandmarks().get(0).getLandmarkList().get(HandLandmark.WRIST)
        Log.i(TAG,
            String.format(
                "MediaPipe Hand wrist normalized coordinates (value range: [0, 1]): x=%f, y=%f",
                wristLandmark.getX(), wristLandmark.getY()
            )
        )
        // Request GL rendering.
        glSurfaceView.setRenderData(handsResult);
        glSurfaceView.requestRender();
    }
}
