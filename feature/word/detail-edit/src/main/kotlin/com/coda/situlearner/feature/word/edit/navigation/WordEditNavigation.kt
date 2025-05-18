package com.coda.situlearner.feature.word.edit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.word.edit.WordEditScreen
import kotlinx.serialization.Serializable

@Serializable
data class WordEditRoute(val wordId: String)

fun NavController.navigateToWordEdit(fromWordId: String) {
    navigate(WordEditRoute(wordId = fromWordId))
}

fun NavGraphBuilder.wordEditScreen(
    onBack: () -> Unit,
) {
    composable<WordEditRoute> {
        WordEditScreen(
            onBack = onBack,
        )
    }
}