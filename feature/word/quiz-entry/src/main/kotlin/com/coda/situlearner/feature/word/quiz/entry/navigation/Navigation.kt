package com.coda.situlearner.feature.word.quiz.entry.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.coda.situlearner.feature.word.quiz.entry.EntryScreen
import kotlinx.serialization.Serializable

@Serializable
data object WordQuizBaseRoute

@Serializable
data object WordQuizEntryRoute

fun NavController.navigateToWordQuizEntry() {
    navigate(WordQuizBaseRoute)
}

fun NavGraphBuilder.wordQuizEntryScreen(
    onBack: () -> Unit,
    onNavigateToMeaning: () -> Unit,
    onNavigateToTranslation: () -> Unit,
    destination: NavGraphBuilder.() -> Unit,
) {
    navigation<WordQuizBaseRoute>(startDestination = WordQuizEntryRoute) {
        composable<WordQuizEntryRoute> {
            EntryScreen(
                onBack = onBack,
                onNavigateToMeaning = onNavigateToMeaning,
                onNavigateToTranslation = onNavigateToTranslation
            )
        }

        destination()
    }
}