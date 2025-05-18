package com.coda.situlearner.feature.word.detail.entry.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.coda.situlearner.feature.word.detail.entry.WordDetailScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordDetailBaseRoute

@Serializable
data class WordDetailRoute(val wordId: String)

fun NavController.navigateToWordDetail(fromWordId: String) {
    navigate(WordDetailRoute(wordId = fromWordId))
}

fun NavGraphBuilder.wordDetailScreen(
    onBack: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    onNavigateToWordEdit: (String) -> Unit,
    destination: NavGraphBuilder.() -> Unit,
) {
    navigation<WordDetailBaseRoute>(startDestination = WordDetailRoute::class) {
        composable<WordDetailRoute> {
            WordDetailScreen(
                onBack = onBack,
                onNavigateToPlayer = onNavigateToPlayer,
                onNavigateToWordEdit = onNavigateToWordEdit
            )
        }

        destination()
    }
}