package com.coda.situlearner.feature.home.settings.chatbot.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.home.settings.chatbot.SettingsChatbotScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeSettingsChatbotRoute

fun NavController.navigateToHomeSettingsChatbot() {
    navigate(HomeSettingsChatbotRoute)
}

fun NavGraphBuilder.homeSettingsChatbotScreen(
    onBack: () -> Unit
) {
    composable<HomeSettingsChatbotRoute> {
        SettingsChatbotScreen(onBack = onBack)
    }
}