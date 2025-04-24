package com.coda.situlearner.feature.player.entry.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.Subtitle
import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.feature.player.entry.PlayerEntryScreen
import kotlinx.serialization.Serializable

@Serializable
data object PlayerBaseRoute

@Serializable
data object PlayerEntryRoute

fun NavController.navigateToPlayerEntry() {
    navigate(PlayerEntryRoute)
}

fun NavGraphBuilder.playerEntryScreen(
    resetTokenFlagProvider: () -> Int,
    onBack: () -> Unit,
    onNavigateToPlaylist: () -> Unit,
    onNavigateToPlayerWord: (Token, Subtitle, Language, String) -> Unit,
    destination: NavGraphBuilder.() -> Unit,
) {
    navigation<PlayerBaseRoute>(startDestination = PlayerEntryRoute) {
        composable<PlayerEntryRoute> {
            PlayerEntryScreen(
                resetTokenFlag = resetTokenFlagProvider(),
                onBack = onBack,
                onNavigateToPlaylist = onNavigateToPlaylist,
                onNavigateToPlayerWord = onNavigateToPlayerWord
            )
        }

        destination()
    }
}