package com.coda.situlearner.feature.word.quiz.meaning.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.word.quiz.meaning.WordQuizScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordQuizMeaningRoute

fun NavController.navigateToWordQuizMeaning() {
    navigate(WordQuizMeaningRoute)
}

fun NavGraphBuilder.wordQuizMeaningScreen(
    onBack: () -> Unit,
) {
    composable<WordQuizMeaningRoute> {
        WordQuizScreen(
            onBack = onBack
        )
    }
}