package com.coda.situlearner.feature.home.settings.player.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.home.settings.player.SettingsPlayerScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeSettingsPlayerRoute

fun NavController.navigateToHomeSettingsPlayer() {
    navigate(HomeSettingsPlayerRoute)
}

fun NavGraphBuilder.homeSettingsPlayerScreen(
    onBack: () -> Unit
) {
    composable<HomeSettingsPlayerRoute> {
        SettingsPlayerScreen(onBack = onBack)
    }
}