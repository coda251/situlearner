package com.coda.situlearner.feature.word.quiz.sentence.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.word.quiz.sentence.QuizSentenceScreen
import kotlinx.serialization.Serializable

@Serializable
data object QuizSentenceRoute

fun NavController.navigateToQuizSentence() {
    navigate(QuizSentenceRoute)

}

fun NavGraphBuilder.quizSentenceScreen(
    onBack: () -> Unit
) {
    composable<QuizSentenceRoute> {
        QuizSentenceScreen(onBack)
    }
}