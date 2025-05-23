package com.coda.situlearner.feature.word.quiz.sentence.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.word.quiz.sentence.QuizSentenceScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordQuizTranslationRoute

fun NavController.navigateToWordQuizTranslation() {
    navigate(WordQuizTranslationRoute)
}

fun NavGraphBuilder.wordQuizTranslationScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String) -> Unit,
) {
    composable<WordQuizTranslationRoute> {
        QuizSentenceScreen(onBack, onNavigateToWordDetail)
    }
}