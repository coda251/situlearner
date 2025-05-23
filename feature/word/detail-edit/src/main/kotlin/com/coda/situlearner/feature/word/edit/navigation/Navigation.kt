package com.coda.situlearner.feature.word.edit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.word.edit.WordEditScreen
import kotlinx.serialization.Serializable

@Serializable
data class WordDetailEditRoute(val wordId: String)

fun NavController.navigateToWordDetailEdit(fromWordId: String) {
    navigate(WordDetailEditRoute(wordId = fromWordId))
}

fun NavGraphBuilder.wordDetailEditScreen(
    onBack: () -> Unit,
) {
    composable<WordDetailEditRoute> {
        WordEditScreen(
            onBack = onBack,
        )
    }
}