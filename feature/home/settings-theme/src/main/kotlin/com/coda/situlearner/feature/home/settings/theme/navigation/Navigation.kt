package com.coda.situlearner.feature.home.settings.theme.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.home.settings.theme.SettingsThemeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeSettingsThemeRoute

fun NavController.navigateToHomeSettingsTheme() {
    navigate(HomeSettingsThemeRoute)
}

fun NavGraphBuilder.homeSettingsThemeScreen(
    onBack: () -> Unit
) {
    composable<HomeSettingsThemeRoute> {
        SettingsThemeScreen(onBack = onBack)
    }
}