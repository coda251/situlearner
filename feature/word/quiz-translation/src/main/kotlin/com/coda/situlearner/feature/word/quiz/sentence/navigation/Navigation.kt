package com.coda.situlearner.feature.word.quiz.sentence.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.core.model.data.QuizDueMode
import com.coda.situlearner.core.model.data.WordProficiencyType
import com.coda.situlearner.feature.word.quiz.sentence.QuizSentenceScreen
import kotlinx.serialization.Serializable

@Serializable
data class WordQuizTranslationRoute(
    val quizDueMode: QuizDueMode
)

fun NavController.navigateToWordQuizTranslation(quizDueMode: QuizDueMode) {
    navigate(WordQuizTranslationRoute(quizDueMode))
}

fun NavGraphBuilder.wordQuizTranslationScreen(
    onBack: () -> Unit,
    onNavigateToWordDetail: (String, WordProficiencyType?) -> Unit,
) {
    composable<WordQuizTranslationRoute> {
        QuizSentenceScreen(
            onBack = onBack,
            onNavigateToWordDetail = { onNavigateToWordDetail(it, null) }
        )
    }
}