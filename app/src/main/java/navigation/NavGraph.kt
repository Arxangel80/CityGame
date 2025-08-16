package navigation

import CompassScreen
import GestureQuestScreen
import NFCRaceViewModel
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.citygame.mainQuest.FinalScreen
import com.example.citygame.chat.ChatScreen
import com.example.citygame.quests.cipherQuest.CipherScreen
import com.example.citygame.feedback.StatisticsScreen
import com.example.citygame.auth.LoginScreen
import com.example.citygame.mapScreen.MainScreen
import com.example.citygame.quests.nfcQuest.NFCRaceQuest
import com.example.citygame.questsScreen.QuestsScreen
import com.example.citygame.quests.rleQuest.RLEQuestScreen
import com.example.citygame.mainQuest.HintScreen
import com.example.citygame.utils.Quests
import com.example.citygame.mainQuest.WelcomeScreen
import com.example.citygame.mainQuest.WinScreen
import com.example.citygame.quests.colorFiltersQuest.ColorFiltersQuest
import kotlinx.coroutines.launch

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
            RLEQuestScreen(navController)
        }
        composable(route = AppScreens.CipherQuest.NAME) {
            CipherScreen(navController)
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
            val coroutineScope = rememberCoroutineScope()

            HintScreen(
                hint = hint,
                onClick = {
                    coroutineScope.launch {
                        Quests.setMainQuestIndex(Quests.currentMainQuestIndex + 1)
                        val nextQuest = Quests.mainQuests.first { it.miniQuest.isFinished }
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
        composable(route = AppScreens.FinalScreen.NAME) {
            FinalScreen(navController)
        }
    }
}

