package com.coda.situlearner.feature.player.entry.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
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
    onBack: () -> Unit,
    onNavigateToPlaylist: () -> Unit,
    destination: NavGraphBuilder.() -> Unit,
) {
    navigation<PlayerBaseRoute>(startDestination = PlayerEntryRoute) {
        composable<PlayerEntryRoute> {
            PlayerEntryScreen(
                onBack = onBack,
                onNavigateToPlaylist = onNavigateToPlaylist
            )
        }

        destination()
    }
}