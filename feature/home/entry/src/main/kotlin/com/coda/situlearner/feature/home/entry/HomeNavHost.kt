package com.coda.situlearner.feature.home.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.feature.home.explore.collection.navigation.exploreCollectionSection
import com.coda.situlearner.feature.home.explore.collection.navigation.navigateToExploreCollection
import com.coda.situlearner.feature.home.explore.entry.navigation.exploreLibrarySection
import com.coda.situlearner.feature.home.explore.entry.navigation.navigateToExploreLibrary
import com.coda.situlearner.feature.home.media.collection.navigation.mediaCollectionSection
import com.coda.situlearner.feature.home.media.collection.navigation.navigateToMediaCollection
import com.coda.situlearner.feature.home.media.entry.navigation.mediaLibrarySection
import com.coda.situlearner.feature.home.settings.chatbot.navigation.navigateToSettingsChatbot
import com.coda.situlearner.feature.home.settings.chatbot.navigation.settingsChatbotSection
import com.coda.situlearner.feature.home.settings.entry.navigation.settingsCommonSection
import com.coda.situlearner.feature.home.word.book.navigation.navigateToWordBook
import com.coda.situlearner.feature.home.word.book.navigation.wordBookSection
import com.coda.situlearner.feature.home.word.entry.navigation.WordLibraryBaseRoute
import com.coda.situlearner.feature.home.word.entry.navigation.wordLibrarySection

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
        startDestination = WordLibraryBaseRoute
    ) {
        mediaLibrarySection(
            onNavigateToCollection = navController::navigateToMediaCollection,
            onNavigateToExplore = navController::navigateToExploreLibrary
        ) {
            mediaCollectionSection(
                onBack = navController::popBackStack
            )

            exploreLibrarySection(
                onNavigateToCollection = navController::navigateToExploreCollection,
                onBack = navController::popBackStack
            ) {
                exploreCollectionSection(
                    onBack = navController::popBackStack
                )
            }
        }

        wordLibrarySection(
            onNavigateToWordDetail = onNavigateToWordDetail,
            onNavigateToWordBook = navController::navigateToWordBook,
            onNavigateToWordQuiz = onNavigateToWordQuiz,
            onNavigateToWordList = onNavigateToWordList,
        ) {
            wordBookSection(
                onBack = navController::popBackStack,
                onNavigateToWordList = onNavigateToWordList
            )
        }

        settingsCommonSection(
            onNavigateToChatbot = navController::navigateToSettingsChatbot
        ) {
            settingsChatbotSection(
                onBack = navController::popBackStack
            )
        }
    }
}