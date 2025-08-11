package com.example.citygame

import com.google.android.gms.maps.model.LatLng

object Quests {
    data class Quest(
        val name: String,
        var isFinished: Boolean = false,
        val coordinates: LatLng,
        val hint: String,
        val description: String // Добавляем описание квеста
    )

    data class MainQuest(
        val name: String,
        var isFinished: Boolean = false,
        val coordinates: LatLng,
        val hint: String,
    )

    val NFCQuest = Quest(
        name = "NFC Quest",
        isFinished = false,
        coordinates = LatLng(52.400395, 16.955508),
        hint = "Find and scan hidden NFC tags",
        description = "Find hidden NFC tags and read them with your device"
    )

    val RLEQuest = Quest(
        name = "RLE Quest",
        isFinished = false,
        coordinates = LatLng(52.402395, 16.955508),
        hint = "Decode the RLE encoded image",
        description = "Solve the mystery of visual cypher using the principles of RLE"
    )

    val CipherQuest = Quest(
        name = "Cipher Quest",
        isFinished = false,
        coordinates = LatLng(52.404395, 16.955508),
        hint = "Shift your position to decode the message",
        description = "Solve the cipher with shifting your geoposition"
    )

    val GestureQuest = Quest(
        name = "Gesture Quest",
        isFinished = false,
        coordinates = LatLng(52.406395, 16.955508),
        hint = "Repeat the gestures shown on screen",
        description = "Use your device camera to replicate a sequence gestures"
    )

    val Quest1 = MainQuest(
        name = "Quest 1",
        isFinished = false,
        coordinates = LatLng(52.408395, 16.955508),
        hint = "Hint for Quest 1",
    )

    val Quest2 = MainQuest(
        name = "Quest 2",
        isFinished = false,
        coordinates = LatLng(52.408395, 16.955508),
        hint = "Hint for Quest 2"
    )

    val Quest3 = MainQuest(
        name = "Quest 3",
        isFinished = false,
        coordinates = LatLng(52.408395, 16.955508),
        hint = "Hint for Quest 3"
    )
    
    val allQuests = listOf(NFCQuest, RLEQuest, CipherQuest, GestureQuest/*, CardanGrilleQuest*/)

    fun getDescriptionForQuest(questName: String): String? {
        return allQuests.find { it.name == questName }?.description
    }
}
