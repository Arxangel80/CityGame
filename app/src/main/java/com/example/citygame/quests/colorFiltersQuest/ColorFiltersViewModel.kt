package com.example.citygame.quests.colorFiltersQuest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.example.citygame.utils.BaseQuestViewModel
import com.example.citygame.utils.Quests
import com.example.citygame.navigation.AppScreens


enum class ColorQuestScreen {
    CAMERA,
    TEXT_FIELDS
}

enum class ColorChannel { RED, GREEN, BLUE, NONE }

class ColorFiltersViewModel : BaseQuestViewModel() {
    var isFilterEnabled by mutableStateOf(true)
        private set

    var selectedChannel by mutableStateOf(ColorChannel.RED)
        private set

    var frameBitmap by mutableStateOf<Bitmap?>(null)
        private set

    fun toggleFilter() {
        isFilterEnabled = !isFilterEnabled
    }

    fun setChannel(channel: ColorChannel) {
        selectedChannel = channel
    }

    fun analyzeImage(image: ImageProxy, targetWidth: Int, targetHeight: Int) {
        val bitmap = image.toBitmap()

        val matrix = Matrix().apply {
            postRotate(image.imageInfo.rotationDegrees.toFloat())
        }
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val scaledBitmap = rotatedBitmap.scale(targetWidth, targetHeight)

        val finalBitmap = if (isFilterEnabled && selectedChannel != ColorChannel.NONE) {
            val filteredBitmap =
                createBitmap(targetWidth, targetHeight)
            val paint = Paint().apply {
                colorFilter = ColorMatrixColorFilter(
                    ColorMatrix(
                        when (selectedChannel) {
                            ColorChannel.RED -> floatArrayOf(
                                1f, 0f, 0f, 0f, 0f,
                                0f, 0f, 0f, 0f, 0f,
                                0f, 0f, 0f, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f
                            )

                            ColorChannel.GREEN -> floatArrayOf(
                                0f, 0f, 0f, 0f, 0f,
                                0f, 1f, 0f, 0f, 0f,
                                0f, 0f, 0f, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f
                            )

                            ColorChannel.BLUE -> floatArrayOf(
                                0f, 0f, 0f, 0f, 0f,
                                0f, 0f, 0f, 0f, 0f,
                                0f, 0f, 1f, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f
                            )

                            else -> floatArrayOf(
                                1f, 0f, 0f, 0f, 0f,
                                0f, 1f, 0f, 0f, 0f,
                                0f, 0f, 1f, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f
                            )
                        }
                    )
                )
            }
            Canvas(filteredBitmap).drawBitmap(scaledBitmap, 0f, 0f, paint)
            filteredBitmap
        } else {
            scaledBitmap
        }

        frameBitmap = finalBitmap
        image.close()
    }

    fun checkAnswers(answers: List<String>) {
        val correctAnswers = listOf("RED", "GREEN", "BLUE")

        if (answers.map { it.uppercase() } == correctAnswers) {
            win()
        } else {
            lose()
        }
    }

    fun win() {
        onWin(
            nextQuestFinished = { Quests.markMiniQuestFinished(Quests.MainQuest2.miniQuest.name) },
            navigateTo = AppScreens.WinScreen.NAME,
            toast = "Вы правильно решили квест! 🎉"
        )
    }

    fun lose() {
        onLose(
            toast = "Не правильно, вы лох!"
        )
    }
}
