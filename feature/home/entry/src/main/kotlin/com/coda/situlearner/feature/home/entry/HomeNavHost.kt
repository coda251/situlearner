package com.coda.situlearner.feature.home.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.coda.situlearner.core.model.data.WordProficiencyType
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.feature.home.explore.collection.navigation.homeExploreCollectionScreen
import com.coda.situlearner.feature.home.explore.collection.navigation.navigateToHomeExploreCollection
import com.coda.situlearner.feature.home.explore.entry.navigation.homeExploreEntryScreen
import com.coda.situlearner.feature.home.explore.entry.navigation.navigateToHomeExploreEntry
import com.coda.situlearner.feature.home.media.collection.navigation.homeMediaCollectionScreen
import com.coda.situlearner.feature.home.media.collection.navigation.navigateToHomeMediaCollection
import com.coda.situlearner.feature.home.media.edit.navigation.homeMediaEditScreen
import com.coda.situlearner.feature.home.media.edit.navigation.navigateToHomeMediaEdit
import com.coda.situlearner.feature.home.media.entry.navigation.homeMediaEntryScreen
import com.coda.situlearner.feature.home.settings.chatbot.navigation.homeSettingsChatbotScreen
import com.coda.situlearner.feature.home.settings.chatbot.navigation.navigateToHomeSettingsChatbot
import com.coda.situlearner.feature.home.settings.entry.navigation.homeSettingsEntryScreen
import com.coda.situlearner.feature.home.settings.player.navigation.homeSettingsPlayerScreen
import com.coda.situlearner.feature.home.settings.player.navigation.navigateToHomeSettingsPlayer
import com.coda.situlearner.feature.home.settings.theme.navigation.homeSettingsThemeScreen
import com.coda.situlearner.feature.home.settings.theme.navigation.navigateToHomeSettingsTheme
import com.coda.situlearner.feature.home.settings.word.navigation.homeSettingsWordScreen
import com.coda.situlearner.feature.home.settings.word.navigation.navigateToHomeSettingsWord
import com.coda.situlearner.feature.home.word.book.navigation.homeWordBookScreen
import com.coda.situlearner.feature.home.word.book.navigation.navigateToHomeWordBook
import com.coda.situlearner.feature.home.word.entry.navigation.HomeWordBaseRoute
import com.coda.situlearner.feature.home.word.entry.navigation.homeWordEntryScreen

@Composable
internal fun HomeNavHost(
    onNavigateToWordList: (WordListType, String?, WordProficiencyType?) -> Unit,
    onNavigateToWordDetail: (String, WordProficiencyType) -> Unit,
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
                onBack = navController::popBackStack,
                onNavigateToExplore = navController::navigateToHomeExploreCollection,
                onNavigateToEdit = navController::navigateToHomeMediaEdit
            )

            homeExploreEntryScreen(
                onNavigateToCollection = navController::navigateToHomeExploreCollection,
                onBack = navController::popBackStack
            ) {
                homeExploreCollectionScreen(
                    onBack = navController::popBackStack
                )
            }

            homeMediaEditScreen(
                onBack = navController::popBackStack
            )
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
            onNavigateToWord = navController::navigateToHomeSettingsWord,
            onNavigateToPlayer = navController::navigateToHomeSettingsPlayer,
            onNavigateToTheme = navController::navigateToHomeSettingsTheme
        ) {
            homeSettingsChatbotScreen(
                onBack = navController::popBackStack
            )

            homeSettingsWordScreen(
                onBack = navController::popBackStack
            )

            homeSettingsPlayerScreen(
                onBack = navController::popBackStack
            )

            homeSettingsThemeScreen(
                onBack = navController::popBackStack
            )
        }
    }
}