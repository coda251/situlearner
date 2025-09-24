package com.coda.situlearner.feature.home.settings.word.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.home.settings.word.SettingsWordScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeSettingsWordRoute

fun NavController.navigateToHomeSettingsWord() {
    navigate(HomeSettingsWordRoute)
}

fun NavGraphBuilder.homeSettingsWordScreen(
    onBack: () -> Unit
) {
    composable<HomeSettingsWordRoute> {
        SettingsWordScreen(onBack = onBack)
    }
}