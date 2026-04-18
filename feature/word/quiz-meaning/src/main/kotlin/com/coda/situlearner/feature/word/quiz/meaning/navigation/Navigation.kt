package com.coda.situlearner.feature.word.quiz.meaning.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.core.model.data.QuizDueMode
import com.coda.situlearner.feature.word.quiz.meaning.WordQuizScreen
import kotlinx.serialization.Serializable

@Serializable
data class WordQuizMeaningRoute(
    val quizDueMode: QuizDueMode
)

fun NavController.navigateToWordQuizMeaning(
    quizDueMode: QuizDueMode
) {
    navigate(WordQuizMeaningRoute(quizDueMode))
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