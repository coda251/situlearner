package com.coda.situlearner.feature.home.word.entry.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.coda.situlearner.core.model.feature.WordListType
import com.coda.situlearner.feature.home.word.entry.WordLibraryScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeWordBaseRoute

@Serializable
data object HomeWordEntryRoute

fun NavController.navigateToHomeWordEntry(navOptions: NavOptions) {
    navigate(route = HomeWordBaseRoute, navOptions = navOptions)
}

fun NavGraphBuilder.homeWordEntryScreen(
    onNavigateToWordBook: (String) -> Unit,
    onNavigateToWordList: (WordListType, String?) -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToWordQuiz: () -> Unit,
    destination: NavGraphBuilder.() -> Unit,
) {
    navigation<HomeWordBaseRoute>(startDestination = HomeWordEntryRoute) {
        composable<HomeWordEntryRoute> {
            WordLibraryScreen(
                onNavigateToWordBook = onNavigateToWordBook,
                onNavigateToWordDetail = onNavigateToWordDetail,
                onNavigateToWordQuiz = onNavigateToWordQuiz,
                onNavigateToWordList = onNavigateToWordList,
            )
        }

        destination()
    }
}