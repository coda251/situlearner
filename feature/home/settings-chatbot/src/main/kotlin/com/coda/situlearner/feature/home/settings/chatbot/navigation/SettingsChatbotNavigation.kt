package com.coda.situlearner.feature.home.settings.chatbot.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.home.settings.chatbot.SettingsChatbotScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingsChatbotRoute

fun NavController.navigateToSettingsChatbot() {
    navigate(SettingsChatbotRoute)
}

fun NavGraphBuilder.settingsChatbotSection(
    onBack: () -> Unit
) {
    composable<SettingsChatbotRoute> {
        SettingsChatbotScreen(onBack = onBack)
    }
}