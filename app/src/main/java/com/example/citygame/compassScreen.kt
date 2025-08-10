import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CompassScreen(
    targetLatitude: Double,
    targetLongitude: Double,
    viewModel: CompassViewModel = viewModel()
) {
    val context = LocalContext.current
    val azimuth by viewModel.azimuth
    val bearing by viewModel.bearing
    val userLocation by viewModel.userLocation

    // Устанавливаем цель при первом запуске
    LaunchedEffect(Unit) {
        viewModel.setTarget(targetLatitude, targetLongitude)
    }

    // Вычисляем финальный угол для стрелки
    val arrowAngle = bearing - azimuth

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Компас
        Canvas(modifier = Modifier.size(300.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2 * 0.9f

            // Внешний круг
            drawCircle(
                color = Color.LightGray,
                radius = radius,
                center = center
            )

            // Стрелка (поворачивается к цели)
            rotate(arrowAngle.toFloat(), pivot = center) {
                drawLine(
                    start = center,
                    end = Offset(center.x, center.y - radius + 20),
                    color = Color.Red,
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }
        }

        // Информация
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = "Азимут: ${"%.1f".format(azimuth)}°",
                fontSize = 18.sp
            )
            Text(
                text = "Направление: ${"%.1f".format(bearing)}°",
                fontSize = 18.sp
            )
            userLocation?.let {
                Text(
                    text = "Ваше местоположение: ${"%.6f".format(it.latitude)}, ${"%.6f".format(it.longitude)}",
                    fontSize = 14.sp
                )
            }
            Text(
                text = "Цель: ${"%.6f".format(targetLatitude)}, ${"%.6f".format(targetLongitude)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}