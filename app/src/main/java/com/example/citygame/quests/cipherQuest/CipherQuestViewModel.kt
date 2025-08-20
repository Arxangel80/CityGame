package com.example.citygame.quests.cipherQuest

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.citygame.utils.BaseQuestViewModel
import com.example.citygame.utils.Quests
import com.example.citygame.locationManager.LocationManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.citygame.navigation.AppScreens
import kotlin.math.sqrt
import kotlin.random.Random


class CipherViewModel : BaseQuestViewModel() {
    private val _messageDecoded = MutableStateFlow(false)
    val messageDecoded: StateFlow<Boolean> = _messageDecoded


    companion object {
        private const val DECRYPTION_DISTANCE = 40
        private const val MAX_DISTANCE = 600
    }

    private val destinationCoordinates = LatLng(52.399181, 16.955630)
    private val decryptedText =
        "Hello world! That's my cipher quest! Keep going, you are on the right way! The key is the year of the plane's production"

    private val _cipherText = MutableStateFlow("")
    val cipherText: StateFlow<String> = _cipherText.asStateFlow()

    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> = _location.asStateFlow()
    private var lastUpdatedLocation: LatLng? = null
    private val MIN_DISTANCE_METERS = 10.0

    private val _distance = MutableStateFlow(0.0)
    val distance: StateFlow<Double> = _distance.asStateFlow()

    init {
        viewModelScope.launch {
            LocationManager.locationFlow.collect { newLocation ->
                newLocation?.let { location ->
                    val shouldUpdate = lastUpdatedLocation?.let { last ->
                        // Convert latitude/longitude differences to approximate meters (111_320 coef to meters)
                        val dx = (location.latitude - last.latitude) * 111_320
                        val dy = (location.longitude - last.longitude) * 111_320
                        sqrt(dx * dx + dy * dy) >= MIN_DISTANCE_METERS
                    } ?: true // Compute distance and check against threshold

                    if (shouldUpdate) {
                        lastUpdatedLocation = location
                        _location.value = location

                        _cipherText.value = encryptTextBasedOnDistance(
                            decryptedText,
                            destinationCoordinates,
                            location,
                            DECRYPTION_DISTANCE
                        )

                        val dx = (location.latitude - destinationCoordinates.latitude) * 111_320
                        val dy = (location.longitude - destinationCoordinates.longitude) * 111_320
                        _distance.value = sqrt(dx * dx + dy * dy)
                    }
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

        if (distance <= decryptionDistance) {
            _messageDecoded.value = true
            return text
        } else if (distance >= MAX_DISTANCE) return randomCharForType(text)

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
