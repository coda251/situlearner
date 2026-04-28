package com.coda.situlearner.feature.player.fullscreen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.Subtitle
import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.feature.player.fullscreen.PlayerScreen
import kotlinx.serialization.Serializable

@Serializable
data object PlayerFullscreenRoute

fun NavController.navigateToPlayerFullscreen() {
    navigate(PlayerFullscreenRoute)
}

fun NavGraphBuilder.playerFullscreen(
    onBack: () -> Unit,
    onNavigateToPlayerWord: (Token, Subtitle, Language, String) -> Unit,
) {
    composable<PlayerFullscreenRoute> {
        PlayerScreen(
            onBack = onBack,
            onNavigateToPlayerWord
        )
    }
}