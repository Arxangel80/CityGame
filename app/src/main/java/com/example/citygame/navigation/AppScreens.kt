package com.example.citygame.navigation

import android.net.Uri

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

    object SessionJoinScreen {
        const val NAME = "SessionJoinScreen"
    }

    object CompassScreen {
        const val NAME = "CompassScreen"
        const val ROUTE_WITH_ARGS = "CompassScreen/{latitude}/{longitude}"

        fun route(latitude: Float, longitude: Float): String =
            "$NAME/$latitude/$longitude"
    }


    object WinScreen {
        const val NAME = "WinScreen"
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

    object CipherQuest {
        const val NAME = "CipherQuest"
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

    object FinalScreen {
        const val NAME = "FinalScreen"
    }
}
