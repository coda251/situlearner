package com.coda.situlearner.feature.word.echo.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.word.echo.WordEchoScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordEchoRoute

fun NavController.navigateToWordEcho() {
    navigate(WordEchoRoute)
}

fun NavGraphBuilder.wordEchoScreen(
    onBack: () -> Unit,
) {
    composable<WordEchoRoute> {
        WordEchoScreen(
            onBack = onBack,
        )
    }
}