package com.example.citygame.navigation

import android.net.Uri
import com.example.citygame.utils.Quests
import com.google.android.gms.maps.model.LatLng

object AppScreens {
    object Login {
        const val NAME = "Login"
    }

    object Registration {
        const val NAME = "Registration"
    }

    object Welcome {
        const val NAME = "Welcome"
    }

    object SessionCreationScreen {
        const val NAME = "SessionCreationScreen"
    }

    object CompassScreen {
        const val NAME = "CompassScreen"
        const val ROUTE_WITH_ARGS = "CompassScreen/{latitude}/{longitude}"

        fun route(latitude: Float, longitude: Float): String =
            "$NAME/$latitude/$longitude"

        fun route(coordinates: LatLng): String =
            "$NAME/${coordinates.latitude}/${coordinates.longitude}"
    }


    object ReturnScreen {
        const val NAME = "ReturnScreen"
    }

    object HintScreen {
        const val ROUTE = "hint_screen"
        const val ROUTE_WITH_ARGS = "$ROUTE/{hint}"

        fun route(hint: String): String {
            val encodedHint = Uri.encode(hint)
            return "$ROUTE/$encodedHint"
        }
    }

    object RLEQuest {
        const val NAME = "RLEQuest"
    }

    object GeopositionCipherQuest {
        const val NAME = "GeopositionCipherQuest"
    }

    object GestureQuest {
        const val NAME = "GestureQuest"
    }

    object NFCRaceQuest {
        const val NAME = "NFCRaceQuest"
    }

    object ColorFilterQuest {
        const val NAME = "ColorFiltersQuest"
    }

    object MapScreen {
        const val NAME = "MapScreen"
    }

    object ChatScreen {
        const val NAME = "ChatScreen"
    }

    object FeedBackScreen {
        const val NAME = "FeedBackScreen"
    }

    object SettingsScreen {
        const val NAME = "Settings"
    }

    object FinalScreen {
        const val NAME = "FinalScreen"
    }

    fun routeForQuest(questName: String): String {
        return when (questName) {
            "RLEQuest" -> RLEQuest.NAME
            "GeopositionCipherQuest" -> GeopositionCipherQuest.NAME
            "GestureQuest" -> GestureQuest.NAME
            "NFCRaceQuest" -> NFCRaceQuest.NAME
            "ColorFilterQuest" -> ColorFilterQuest.NAME
            "MainQuest1" -> {
                Quests.currentMainQuestIndex = 1
                CompassScreen.route(Quests.MainQuest1.coordinates)
            }

            "MainQuest2" -> {
                Quests.currentMainQuestIndex = 2
                CompassScreen.route(Quests.MainQuest2.coordinates)
            }

            "MainQuest3" -> {
                Quests.currentMainQuestIndex = 3
                CompassScreen.route(Quests.MainQuest3.coordinates)
            }

            "MainQuest4" -> {
                Quests.currentMainQuestIndex = 4
                CompassScreen.route(Quests.MainQuest4.coordinates)
            }

            "MainQuest5" -> {
                Quests.currentMainQuestIndex = 5
                CompassScreen.route(Quests.MainQuest5.coordinates)
            }

            else -> throw IllegalArgumentException("Unknown quest name: $questName")
        }
    }
}
