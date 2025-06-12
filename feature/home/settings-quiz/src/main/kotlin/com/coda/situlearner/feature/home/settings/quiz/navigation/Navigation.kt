package com.coda.situlearner.feature.home.settings.quiz.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.home.settings.quiz.SettingsQuizScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeSettingsQuizRoute

fun NavController.navigateToHomeSettingsQuiz() {
    navigate(HomeSettingsQuizRoute)
}

fun NavGraphBuilder.homeSettingsQuizScreen(
    onBack: () -> Unit
) {
    composable<HomeSettingsQuizRoute> {
        SettingsQuizScreen(onBack = onBack)
    }
}