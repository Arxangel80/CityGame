import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.citygame.CardanGrilleQuest
import com.example.citygame.ChatScreen
import com.example.citygame.CipherScreen
import com.example.citygame.GestureQuestScreen
import com.example.citygame.LoginScreen
import com.example.citygame.MainScreen
import com.example.citygame.NFCQuest
import com.example.citygame.QuestsScreen
import com.example.citygame.RLEQuestScreen
import com.example.citygame.Screens

@Composable
fun AppNavGraph(navController: NavHostController, debugMode: Boolean, readedMsg: String) {
    NavHost(
        navController = navController,
        startDestination = Screens.Login.name
    ) {
        composable(route = Screens.Login.name) {
            LoginScreen(onNextButtonClicked = {
                navController.navigate(Screens.Quests.name) {
                    popUpTo(0)
                }
            })
        }
        composable(route = Screens.Quests.name) {
            QuestsScreen(navigateToMain = { navController.navigate(Screens.Main.name) })
        }
        composable(route = Screens.FeedBack.name) {
            QuestsScreen(navigateToMain = { navController.navigate(Screens.FeedBack.name) })
        }
        composable(route = Screens.Main.name) {
            val navigateToQuestMap = mapOf(
                "RLE Quest" to { navController.navigate(Screens.RLEQuest.name) },
                "Cipher Quest" to { navController.navigate(Screens.CipherQuest.name) },
                "Gesture Quest" to { navController.navigate(Screens.GestureQuest.name) },
                "Cardan Grille Quest" to { navController.navigate(Screens.CardanGrilleQuest.name) },
                "NFC Quest" to { navController.navigate(Screens.NFCQuest.name) },
            )
            MainScreen(
                debugMode,
                navigateToQuestMap,
                navToChat = { navController.navigate(Screens.Chat.name) },
                navToStats = { navController.navigate(Screens.FeedBack.name) }
            )
        }
        composable(route = Screens.Chat.name) {
            ChatScreen()
        }

        composable(route = Screens.RLEQuest.name) {
            RLEQuestScreen(8, 8, debugMode)
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
        composable(route = Screens.NFCQuest.name) {
            NFCQuest(readedMsg = readedMsg)
        }
        composable(route = Screens.FeedBack.name) {
            FeedbackScreen()
        }
    }
}