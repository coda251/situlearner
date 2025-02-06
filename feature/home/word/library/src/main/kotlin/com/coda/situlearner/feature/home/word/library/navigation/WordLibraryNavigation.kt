package com.coda.situlearner.feature.home.word.library.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.home.word.library.WordLibraryScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordLibraryRoute

fun NavController.navigateToWordLibrary(navOptions: NavOptions) {
    navigate(route = WordLibraryRoute, navOptions = navOptions)
}

fun NavGraphBuilder.wordLibrarySection(
    onNavigateToWordCategory: (String) -> Unit,
    onNavigateToWordDetail: (String) -> Unit
) {
    composable<WordLibraryRoute> {
        WordLibraryScreen(
            onNavigateToWordCategory = onNavigateToWordCategory,
            onNavigateToWordDetail = onNavigateToWordDetail
        )
    }
}