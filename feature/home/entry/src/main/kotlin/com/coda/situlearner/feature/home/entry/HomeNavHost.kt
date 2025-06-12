package com.coda.situlearner.feature.home.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.feature.home.explore.collection.navigation.homeExploreCollectionScreen
import com.coda.situlearner.feature.home.explore.collection.navigation.navigateToHomeExploreCollection
import com.coda.situlearner.feature.home.explore.entry.navigation.homeExploreEntryScreen
import com.coda.situlearner.feature.home.explore.entry.navigation.navigateToHomeExploreEntry
import com.coda.situlearner.feature.home.media.collection.navigation.homeMediaCollectionScreen
import com.coda.situlearner.feature.home.media.collection.navigation.navigateToHomeMediaCollection
import com.coda.situlearner.feature.home.media.entry.navigation.homeMediaEntryScreen
import com.coda.situlearner.feature.home.settings.chatbot.navigation.homeSettingsChatbotScreen
import com.coda.situlearner.feature.home.settings.chatbot.navigation.navigateToHomeSettingsChatbot
import com.coda.situlearner.feature.home.settings.entry.navigation.homeSettingsEntryScreen
import com.coda.situlearner.feature.home.settings.quiz.navigation.homeSettingsQuizScreen
import com.coda.situlearner.feature.home.settings.quiz.navigation.navigateToHomeSettingsQuiz
import com.coda.situlearner.feature.home.word.book.navigation.homeWordBookScreen
import com.coda.situlearner.feature.home.word.book.navigation.navigateToHomeWordBook
import com.coda.situlearner.feature.home.word.entry.navigation.HomeWordBaseRoute
import com.coda.situlearner.feature.home.word.entry.navigation.homeWordEntryScreen

@Composable
internal fun HomeNavHost(
    onNavigateToWordList: (WordListType, String?) -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToWordQuiz: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeWordBaseRoute
    ) {
        homeMediaEntryScreen(
            onNavigateToCollection = navController::navigateToHomeMediaCollection,
            onNavigateToExplore = navController::navigateToHomeExploreEntry
        ) {
            homeMediaCollectionScreen(
                onBack = navController::popBackStack
            )

            homeExploreEntryScreen(
                onNavigateToCollection = navController::navigateToHomeExploreCollection,
                onBack = navController::popBackStack
            ) {
                homeExploreCollectionScreen(
                    onBack = navController::popBackStack
                )
            }
        }

        homeWordEntryScreen(
            onNavigateToWordDetail = onNavigateToWordDetail,
            onNavigateToWordBook = navController::navigateToHomeWordBook,
            onNavigateToWordQuiz = onNavigateToWordQuiz,
            onNavigateToWordList = onNavigateToWordList,
        ) {
            homeWordBookScreen(
                onBack = navController::popBackStack,
                onNavigateToWordList = onNavigateToWordList
            )
        }

        homeSettingsEntryScreen(
            onNavigateToChatbot = navController::navigateToHomeSettingsChatbot,
            onNavigateToQuiz = navController::navigateToHomeSettingsQuiz
        ) {
            homeSettingsChatbotScreen(
                onBack = navController::popBackStack
            )

            homeSettingsQuizScreen(
                onBack = navController::popBackStack
            )
        }
    }
}