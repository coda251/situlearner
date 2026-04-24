package com.coda.situlearner.feature.word.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.word.search.SearchScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordSearchRoute

fun NavController.navigateToWordSearch() {
    navigate(route = WordSearchRoute)
}

fun NavGraphBuilder.wordSearchScreen(
    onBack: () -> Unit,
    onNavigateToPlayer: () -> Unit,
) {
    composable<WordSearchRoute> {
        SearchScreen(
            onBack = onBack,
            onNavigateToPlayer = onNavigateToPlayer
        )
    }
}