package com.coda.situlearner.feature.word.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.word.detail.WordDetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class WordDetailRoute(val wordId: String)

fun NavController.navigateToWordDetail(fromWordId: String) {
    navigate(WordDetailRoute(wordId = fromWordId))
}

fun NavGraphBuilder.wordDetailScreen(
    onBack: () -> Unit,
    onNavigateToPlayer: () -> Unit
) {
    composable<WordDetailRoute> {
        WordDetailScreen(
            onBack = onBack,
            onNavigateToPlayer = onNavigateToPlayer
        )
    }
}