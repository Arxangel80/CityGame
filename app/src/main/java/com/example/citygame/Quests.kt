package com.example.citygame

import com.google.android.gms.maps.model.LatLng
import navigation.AppScreens

object Quests {
    var currentMainQuestIndex = 0

    data class MiniQuest(
        val name: String,
        var isFinished: Boolean = false,
        val description: String
    )

    data class MainQuest(
        val name: String,
        var isInProgress: Boolean = false,
        val coordinates: LatLng,
        val route: String,
        val hint: String,
        val miniQuest: MiniQuest
    )

    private val NFCRaceQuest = MiniQuest(
        name = "NFC Quest",
        isFinished = false,
        description = "Find hidden NFC tags and read them with your device"
    )

    private val RLEQuest = MiniQuest(
        name = "RLE Quest",
        isFinished = false,
        description = "Solve the mystery of visual cypher using the principles of RLE"
    )

    private val CipherQuest = MiniQuest(
        name = "Cipher Quest",
        isFinished = false,
        description = "Solve the cipher with shifting your geoposition"
    )

    private val GestureQuest = MiniQuest(
        name = "Gesture Quest",
        isFinished = false,
        description = "Use your device camera to replicate a sequence gestures"
    )

    private val ColorFilterQuest = MiniQuest(
        name = "Color Filter Quest",
        isFinished = false,
        description = "Use your device camera to replicate a sequence gestures"
    )

    val MainQuest1 = MainQuest(
        name = "Main quest 1",
        coordinates = LatLng(52.408395, 16.955508),
        route = AppScreens.NFCRaceQuest.NAME,
        hint = "Hint 1",
        miniQuest = NFCRaceQuest
    )

    val MainQuest2 = MainQuest(
        name = "Main quest 2",
        coordinates = LatLng(52.408395, 16.955508),
        route = AppScreens.ColorFilterQuest.NAME,
        hint = "Hint 2",
        miniQuest = ColorFilterQuest
    )

    val MainQuest3 = MainQuest(
        name = "Main quest 3",
        coordinates = LatLng(52.408395, 16.955508),
        route = AppScreens.RLEQuest.NAME,
        hint = "Hint 3",
        miniQuest = RLEQuest
    )

    val MainQuest4 = MainQuest(
        name = "Main quest 4",
        coordinates = LatLng(52.408395, 16.955508),
        route = AppScreens.CipherQuest.NAME,
        hint = "Hint 4",
        miniQuest = CipherQuest
    )

    val MainQuest5 = MainQuest(
        name = "Main quest 5",
        coordinates = LatLng(52.408395, 16.955508),
        route = AppScreens.GestureQuest.NAME,
        hint = "Hint 5",
        miniQuest = GestureQuest
    )

    val miniQuests = listOf(NFCRaceQuest, RLEQuest, CipherQuest, GestureQuest, ColorFilterQuest)
    val mainQuests = listOf(MainQuest1, MainQuest2, MainQuest3, MainQuest4, MainQuest5)

    fun getDescriptionForQuest(questName: String): String? {
        return miniQuests.find { it.name == questName }?.description
    }
}
