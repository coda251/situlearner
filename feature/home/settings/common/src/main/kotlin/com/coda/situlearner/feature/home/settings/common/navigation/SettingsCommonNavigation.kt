package com.coda.situlearner.feature.home.settings.common.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.home.settings.common.SettingsCommonScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingsCommonRoute

fun NavController.navigateToSettingsCommon(navOptions: NavOptions) {
    navigate(SettingsCommonRoute, navOptions = navOptions)
}

fun NavGraphBuilder.settingsCommonSection() {
    composable<SettingsCommonRoute> {
        SettingsCommonScreen()
    }
}