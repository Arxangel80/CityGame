import android.location.Location
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citygame.compassScreen.CompassViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CompassScreen(
    targetLatitude: Double,
    targetLongitude: Double,
    viewModel: CompassViewModel = viewModel()
) {
    val azimuth by viewModel.azimuth
    val bearing by viewModel.bearing
    val userLocation by viewModel.userLocation
    val isSensorsAvailable by viewModel.isSensorsAvailable

    LaunchedEffect(Unit) {
        viewModel.setTarget(targetLatitude, targetLongitude)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!isSensorsAvailable) {
            Text(
                text = "Датчик компаса не найден",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            // Вычисляем угол для стрелки (учитываем, что 0° - это север)
            val arrowAngle = bearing - azimuth

            CompassDisplay(arrowAngle, azimuth, bearing)

            LocationInfo(
                userLocation = userLocation,
                targetLatitude = targetLatitude,
                targetLongitude = targetLongitude,
                azimuth = azimuth,
                bearing = bearing,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun CompassDisplay(
    arrowAngle: Float,
    azimuth: Float,
    bearing: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(300.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2 * 0.9f

        // Фон компаса
        drawCircle(
            color = Color.LightGray.copy(alpha = 0.3f),
            radius = radius,
            center = center
        )

        // Метки сторон света
        val textPaint = Paint().asFrameworkPaint().apply {
            color = android.graphics.Color.BLACK
            textSize = 40f
            textAlign = android.graphics.Paint.Align.CENTER
        }

        listOf("N", "E", "S", "W").forEachIndexed { i, direction ->
            val angle = i * 90f - azimuth
            val x = center.x + radius * 0.8f * sin(Math.toRadians(angle.toDouble())).toFloat()
            val y = center.y - radius * 0.8f * cos(Math.toRadians(angle.toDouble())).toFloat()
            drawContext.canvas.nativeCanvas.drawText(direction, x, y + 15, textPaint)
        }

        // Стрелка к цели
        rotate(arrowAngle, pivot = center) {
            drawLine(
                start = center,
                end = Offset(center.x, center.y - radius + 20),
                color = Color.Red,
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = Color.Red,
                radius = 10f,
                center = center
            )
        }

        // Индикатор севера
        rotate(-azimuth, pivot = center) {
            drawLine(
                start = center,
                end = Offset(center.x, center.y - radius),
                color = Color.Blue,
                strokeWidth = 3f
            )
        }
    }
}

@Composable
private fun LocationInfo(
    userLocation: Location?,
    targetLatitude: Double,
    targetLongitude: Double,
    azimuth: Float,
    bearing: Float,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = "Азимут: ${"%.1f".format(azimuth)}°",
            fontSize = 18.sp
        )
        Text(
            text = "Направление на цель: ${"%.1f".format(bearing)}°",
            fontSize = 18.sp
        )
        userLocation?.let {
            Text(
                text = "Ваши координаты: ${"%.6f".format(it.latitude)}, ${"%.6f".format(it.longitude)}",
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