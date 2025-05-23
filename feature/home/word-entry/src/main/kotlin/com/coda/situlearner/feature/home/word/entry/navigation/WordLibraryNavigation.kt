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
data object WordLibraryBaseRoute

@Serializable
data object WordLibraryRoute

fun NavController.navigateToWordLibrary(navOptions: NavOptions) {
    navigate(route = WordLibraryBaseRoute, navOptions = navOptions)
}

fun NavGraphBuilder.wordLibrarySection(
    onNavigateToWordBook: (String) -> Unit,
    onNavigateToWordList: (WordListType, String?) -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
    onNavigateToWordQuiz: () -> Unit,
    destination: NavGraphBuilder.() -> Unit,
) {
    navigation<WordLibraryBaseRoute>(startDestination = WordLibraryRoute) {
        composable<WordLibraryRoute> {
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