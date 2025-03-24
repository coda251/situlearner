package com.coda.situlearner.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.coda.situlearner.core.model.data.WordCategoryType
import com.coda.situlearner.feature.home.explore.collection.navigation.exploreCollectionSection
import com.coda.situlearner.feature.home.explore.collection.navigation.navigateToExploreCollection
import com.coda.situlearner.feature.home.explore.library.navigation.exploreLibrarySection
import com.coda.situlearner.feature.home.explore.library.navigation.navigateToExploreLibrary
import com.coda.situlearner.feature.home.media.collection.navigation.mediaCollectionSection
import com.coda.situlearner.feature.home.media.collection.navigation.navigateToMediaCollection
import com.coda.situlearner.feature.home.media.library.navigation.mediaLibrarySection
import com.coda.situlearner.feature.home.settings.common.navigation.settingsCommonSection
import com.coda.situlearner.feature.home.word.library.navigation.WordLibraryRoute
import com.coda.situlearner.feature.home.word.library.navigation.wordLibrarySection

@Composable
internal fun HomeNavHost(
    onNavigateToWordCategory: (WordCategoryType, String) -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToWordQuiz: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = WordLibraryRoute
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
            onNavigateToWordCategory = onNavigateToWordCategory,
            onNavigateToWordQuiz = onNavigateToWordQuiz
        )

        settingsCommonSection()
    }
}