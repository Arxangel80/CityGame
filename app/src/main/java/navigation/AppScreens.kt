package navigation

import java.net.URLEncoder

object AppScreens {

    object Login {
        const val NAME = "Login"
    }

    object Quests {
        const val NAME = "Quests"
    }

    object Welcome {
        const val NAME = "Welcome"
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
        const val NAME = "HintScreen"
        const val ROUTE_WITH_ARGS = "$NAME/{hint}"

        fun route(hint: String): String = "$NAME/${URLEncoder.encode(hint, "UTF-8")}"
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
