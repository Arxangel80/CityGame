package navigation

import CompassScreen
import GestureQuestScreen
import NFCRaceViewModel
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.citygame.chat.ChatScreen
import com.example.citygame.quests.cipherQuest.CipherScreen
import com.example.citygame.feedback.StatisticsScreen
import com.example.citygame.auth.LoginScreen
import com.example.citygame.MainScreen
import com.example.citygame.quests.nfcQuest.NFCRaceQuest
import com.example.citygame.questsScreen.QuestsScreen
import com.example.citygame.quests.rleQuest.RLEQuestScreen
import com.example.citygame.HintScreen
import com.example.citygame.Quests
import com.example.citygame.WelcomeScreen
import com.example.citygame.WinScreen
import com.example.citygame.quests.colorFiltersQuest.ColorFiltersQuest

@SuppressLint("MissingPermission")
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    nfcRaceViewModel: NFCRaceViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = AppScreens.Login.NAME) {
            LoginScreen(onNextButtonClicked = {
                navController.navigate(AppScreens.Quests.NAME) {
                    popUpTo(0)
                }
            })
        }
        composable(route = AppScreens.Quests.NAME) {
            QuestsScreen(navigateToMain = { navController.navigate(AppScreens.Welcome.NAME) })
        }
        // Welcome
        composable(route = AppScreens.Welcome.NAME) {
            WelcomeScreen(onClick = {
                val quest = Quests.MainQuest1
                val coordinates = quest.coordinates
                val targetLatitude = coordinates.latitude.toFloat()
                val targetLongitude = coordinates.longitude.toFloat()

                navController.navigate(
                    AppScreens.CompassScreen.route(
                        targetLatitude,
                        targetLongitude
                    )
                )
            })
        }
        // MapScreen
        composable(route = AppScreens.MapScreen.NAME) @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
            val navigateToQuestMap = mapOf(
                "RLE Quest" to { navController.navigate(AppScreens.RLEQuest.NAME) },
                "Cipher Quest" to { navController.navigate(AppScreens.CipherQuest.NAME) },
                "Gesture Quest" to { navController.navigate(AppScreens.GestureQuest.NAME) },
                "Color Filter Quest" to { navController.navigate(AppScreens.ColorFilterQuest.NAME) },
                "NFC Quest" to { navController.navigate(AppScreens.NFCRaceQuest.NAME) },
            )
            MainScreen(
                navigateToQuestMap,
                navToChat = { navController.navigate(AppScreens.ChatScreen.NAME) },
                navToStats = { navController.navigate(AppScreens.FeedBackScreen.NAME) }
            )
        }
        composable(route = AppScreens.ChatScreen.NAME) {
            ChatScreen()
        }
        composable(route = AppScreens.RLEQuest.NAME) {
            RLEQuestScreen(8, 8)
        }
        composable(route = AppScreens.CipherQuest.NAME) {
            CipherScreen()
        }
        composable(route = AppScreens.GestureQuest.NAME) {
            GestureQuestScreen()
        }
        composable(route = AppScreens.ColorFilterQuest.NAME) {
            ColorFiltersQuest(navController = navController)
        }
        composable(route = AppScreens.NFCRaceQuest.NAME) {
            NFCRaceQuest(nfcRaceViewModel, navController)
        }
        // CompassScreen
        composable(
            route = AppScreens.CompassScreen.ROUTE_WITH_ARGS,
            arguments = listOf(
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val latitude = backStackEntry.arguments?.getFloat("latitude")?.toDouble() ?: 0.0
            val longitude = backStackEntry.arguments?.getFloat("longitude")?.toDouble() ?: 0.0

            CompassScreen(targetLatitude = latitude, targetLongitude = longitude)
        }

        composable(route = AppScreens.FeedBackScreen.NAME) {
            StatisticsScreen()
        }
        composable(route = AppScreens.WinScreen.NAME) {
            WinScreen()
        }
        // HintScreen
        composable(
            route = AppScreens.HintScreen.ROUTE_WITH_ARGS,
            arguments = listOf(
                navArgument("hint") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val hint = backStackEntry.arguments?.getString("hint") ?: ""

            HintScreen(
                hint = hint,
                onClick = {
                    Quests.currentMainQuestIndex++
                    Quests.mainQuests.first { it.miniQuest.isFinished }.let { nextQuest ->
                        navController.navigate(
                            AppScreens.CompassScreen.route(
                                nextQuest.coordinates.latitude.toFloat(),
                                nextQuest.coordinates.longitude.toFloat()
                            )
                        )
                    }
                }
            )
        }
    }
}

