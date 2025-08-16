package com.example.citygame.quests.cipherQuest

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.citygame.BaseQuestViewModel
import com.example.citygame.Quests
import com.example.citygame.locationManager.LocationManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import navigation.AppScreens
import kotlin.math.sqrt
import kotlin.random.Random


class CipherViewModel : BaseQuestViewModel() {

    companion object {
        private const val DECRYPTION_DISTANCE = 40
        private const val MAX_DISTANCE = 600
    }

    private val destinationCoordinates = LatLng(52.399181, 16.955630)
    private val decryptedText =
        "Hello world! That's my cipher quest! Keep going, you are on the right way!"

    private val _cipherText = MutableStateFlow("")
    val cipherText: StateFlow<String> = _cipherText.asStateFlow()

    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> = _location.asStateFlow()

    private val _distance = MutableStateFlow(0.0)
    val distance: StateFlow<Double> = _distance.asStateFlow()

    init {
        viewModelScope.launch {
            LocationManager.locationFlow.collect { newLocation ->
                _location.value = newLocation
                newLocation?.let {
                    _cipherText.value = encryptTextBasedOnDistance(
                        decryptedText,
                        destinationCoordinates,
                        it,
                        DECRYPTION_DISTANCE
                    )

                    val x = (it.latitude - destinationCoordinates.latitude) * 111320
                    val y = (it.longitude - destinationCoordinates.longitude) * 111320
                    _distance.value = sqrt(x * x + y * y)
                }
            }
        }
    }

    private fun randomCharForType(text: String): String {
        val randomText = text.map { char ->
            when {
                char.isLowerCase() -> Random.nextInt(26).let { 'a' + it }
                char.isUpperCase() -> Random.nextInt(26).let { 'A' + it }
                else -> char
            }
        }
        return randomText.joinToString("")
    }

    private fun encryptTextBasedOnDistance(
        text: String,
        destCords: LatLng,
        currentCords: LatLng,
        decryptionDistance: Int
    ): String {
        val latitudeDest = destCords.latitude
        val longitudeDest = destCords.longitude
        val latitudeCurrent = currentCords.latitude
        val longitudeCurrent = currentCords.longitude

        val x = (latitudeCurrent - latitudeDest) * 111320
        val y = (longitudeCurrent - longitudeDest) * 111320
        val distance = sqrt(x * x + y * y)

        if (distance <= decryptionDistance) return text
        else if (distance >= MAX_DISTANCE) return randomCharForType(text)

        val distanceToMinDistance = (distance - decryptionDistance) / distance
        val charToCipher = (text.length * distanceToMinDistance).toInt()
        Log.d("charToCipher", charToCipher.toString())

        return if (charToCipher > 0) {
            val textToCipher = text.takeLast(charToCipher)
            text.dropLast(charToCipher) + randomCharForType(textToCipher)
        } else {
            text
        }
    }

    fun win() {
        onWin(
            nextQuestFinished = { Quests.markMiniQuestFinished(Quests.MainQuest4.miniQuest.name) },
            navigateTo = AppScreens.WinScreen.NAME,
            toast = "Вы успешно справились с заданием 4!"
        )
    }
}
