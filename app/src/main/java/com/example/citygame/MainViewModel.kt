//package com.example.citygame
//
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlin.collections.plus
//
//class MainViewModel : ViewModel() {
//    private val _gameState = MutableStateFlow(GameState())
//    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
//
////    // Обработка встряхивания телефона
////    fun handleShake() {
////        when (_gameState.value.currentScreen) {
////            AppScreen.Welcome -> navigateTo(AppScreen.Compass)
////            else -> { /* Игнорируем встряхивание на других экранах */
////            }
////        }
////    }
//
//    // Обработка сканирования NFC метки
//    fun handleNfcTag(tagId: String) {
//        val quest = Quests.getQuestByTag(tagId) ?: run {
//            _gameState.value = _gameState.value.copy(
//                toastMessage = "Неизвестная метка: $tagId"
//            )
//            return
//        }
//
//        _gameState.value = _gameState.value.copy(
//            currentQuest = quest,
//            toastMessage = "Найден квест: ${quest.name}"
//        )
//
//        when (quest.type) {
//            QuestType.NFCRace -> navigateTo(AppScreens.NFCRace)
//            QuestType.RedFilter -> navigateTo(AppScreens.RedFilter)
//            QuestType.RLE -> navigateTo(AppScreens.RLE)
//            QuestType.Cipher -> navigateTo(AppScreens.Cipher)
//            QuestType.Gesture -> navigateTo(AppScreens.Gesture)
//        }
//    }
//
//    // Завершение квеста
//    fun completeCurrentQuest() {
//        _gameState.value.currentQuest?.let { quest ->
//            _gameState.value = _gameState.value.copy(
//                completedQuests = _gameState.value.completedQuests + quest.type,
//                currentQuest = null,
//                toastMessage = "Квест ${quest.name} завершён!"
//            )
//            navigateTo(AppScreens.Compass)
//        }
//    }
//
//    // Навигация между экранами
//    private fun navigateTo(screen: AppScreens) {
//        _gameState.value = _gameState.value.copy(currentScreen = screen)
//
//        navController?.navigate(screen.route) {
//            when (screen) {
//                AppScreens.Compass -> popUpTo(AppScreens.Welcome.route)
//                else -> { /* Стандартное поведение */
//                }
//            }
//        }
//    }
//
//    // Сброс сообщения toast
//    fun resetToastMessage() {
//        _gameState.value = _gameState.value.copy(toastMessage = null)
//    }
//}