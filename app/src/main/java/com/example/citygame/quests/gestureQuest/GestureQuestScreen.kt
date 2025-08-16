import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citygame.BuildConfig
import com.example.citygame.GestureViewModel

@Composable
fun GestureQuestScreen(
    viewModel: GestureViewModel = viewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val recognizedGesture by viewModel.recognizedGesture.collectAsState()
    val recognizedGestures by viewModel.recognizedGesturesUI

    val context = LocalContext.current
    val previewView = remember {
        PreviewView(context)
    }

    LaunchedEffect(Unit) {
        viewModel.startCamera(previewView, lifecycleOwner)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        Column() {
            Text(
                "Recognized gesture: $recognizedGesture",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                "Recognized gestures: $recognizedGestures",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(16.dp)
            )
            if (BuildConfig.DEBUG)
                Button(onClick = { viewModel.win() }) {
                    Text("Win")
                }
        }
    }
}
