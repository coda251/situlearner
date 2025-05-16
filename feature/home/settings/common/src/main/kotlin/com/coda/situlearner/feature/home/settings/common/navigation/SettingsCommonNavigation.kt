package com.coda.situlearner.feature.home.settings.common.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.coda.situlearner.feature.home.settings.common.SettingsCommonScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingsCommonBaseRoute

@Serializable
data object SettingsCommonRoute

fun NavController.navigateToSettingsCommon(navOptions: NavOptions) {
    navigate(SettingsCommonRoute, navOptions = navOptions)
}

fun NavGraphBuilder.settingsCommonSection(
    onNavigateToChatbot: () -> Unit,
    destination: NavGraphBuilder.() -> Unit
) {
    navigation<SettingsCommonBaseRoute>(startDestination = SettingsCommonRoute) {
        composable<SettingsCommonRoute> {
            SettingsCommonScreen(onNavigateToChatbot)
        }

        destination()
    }
}