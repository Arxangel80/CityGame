package navigation

import CompassScreen
import GestureQuestScreen
import NFCRaceViewModel
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.citygame.quests.cardianQuest.CardanGrilleQuest
import com.example.citygame.chat.ChatScreen
import com.example.citygame.quests.cipherQuest.CipherScreen
import com.example.citygame.feedback.StatisticsScreen
import com.example.citygame.auth.LoginScreen
import com.example.citygame.MainScreen
import com.example.citygame.quests.nfcQuest.NFCRaceQuest
import com.example.citygame.questsScreen.QuestsScreen
import com.example.citygame.quests.rleQuest.RLEQuestScreen
import com.example.citygame.Screens

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
        composable(route = Screens.Login.name) {
            LoginScreen(onNextButtonClicked = {
                navController.navigate(Screens.Quests.name) {
                    popUpTo(0)
                }
            })
        }
        composable(route = Screens.Quests.name) {
            QuestsScreen(navigateToMain = { navController.navigate(Screens.Map.name) })
        }
        composable(route = Screens.FeedBack.name) {
            StatisticsScreen()
        }
        composable(route = Screens.Map.name) @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
            val navigateToQuestMap = mapOf(
                "RLE Quest" to { navController.navigate(Screens.RLEQuest.name) },
                "Cipher Quest" to { navController.navigate(Screens.CipherQuest.name) },
                "Gesture Quest" to { navController.navigate(Screens.GestureQuest.name) },
                "Cardan Grille Quest" to { navController.navigate(Screens.CardanGrilleQuest.name) },
                "NFC Quest" to { navController.navigate(Screens.NFCRaceQuest.name) },
                "SuddenMessage Quest" to { navController.navigate(Screens.SuddenMessage.name) }
            )
            MainScreen(
                navigateToQuestMap,
                navToChat = { navController.navigate(Screens.Chat.name) },
                navToStats = { navController.navigate(Screens.FeedBack.name) }
            )
        }
        composable(route = Screens.Chat.name) {
            ChatScreen()
        }
        composable(route = Screens.RLEQuest.name) {
            RLEQuestScreen(8, 8)
        }
        composable(route = Screens.CipherQuest.name) {
            CipherScreen()
        }
        composable(route = Screens.GestureQuest.name) {
            GestureQuestScreen()
        }
        composable(route = Screens.CardanGrilleQuest.name) {
            CardanGrilleQuest()
        }
        composable(route = Screens.NFCRaceQuest.name) {
            NFCRaceQuest(nfcRaceViewModel)
        }
        composable(route = Screens.FeedBack.name) {
            StatisticsScreen()
        }
        composable(route = Screens.FeedBack.name) {
            StatisticsScreen()
        }
        composable(route = Screens.CompassScreen.name) {
            val targetLatitude = 55.7558
            val targetLongitude = 37.6173

            CompassScreen(
                targetLatitude = targetLatitude,
                targetLongitude = targetLongitude
            )
        }
    }
}