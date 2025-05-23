package com.coda.situlearner.feature.home.settings.entry.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.coda.situlearner.feature.home.settings.entry.SettingsCommonScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeSettingsBaseRoute

@Serializable
data object HomeSettingsEntryRoute

fun NavController.navigateToHomeSettingsEntry(navOptions: NavOptions) {
    navigate(HomeSettingsEntryRoute, navOptions = navOptions)
}

fun NavGraphBuilder.homeSettingsEntryScreen(
    onNavigateToChatbot: () -> Unit,
    destination: NavGraphBuilder.() -> Unit
) {
    navigation<HomeSettingsBaseRoute>(startDestination = HomeSettingsEntryRoute) {
        composable<HomeSettingsEntryRoute> {
            SettingsCommonScreen(onNavigateToChatbot)
        }

        destination()
    }
}