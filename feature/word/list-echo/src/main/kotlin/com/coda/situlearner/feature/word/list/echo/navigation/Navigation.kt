package com.coda.situlearner.feature.word.list.echo.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.word.list.echo.WordEchoScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordListEchoRoute

fun NavController.navigateToWordListEcho() {
    navigate(WordListEchoRoute)
}

fun NavGraphBuilder.wordListEchoScreen(
    onBack: () -> Unit,
) {
    composable<WordListEchoRoute> {
        WordEchoScreen(
            onBack = onBack,
        )
    }
}