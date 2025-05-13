package com.coda.situlearner.feature.word.quiz.meaning.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.word.quiz.meaning.WordQuizScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordQuizRoute

fun NavController.navigateToWordQuiz() {
    navigate(WordQuizRoute)
}

fun NavGraphBuilder.wordQuizScreen(
    onBack: () -> Unit,
) {
    composable<WordQuizRoute> {
        WordQuizScreen(
            onBack = onBack
        )
    }
}