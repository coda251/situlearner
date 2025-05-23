package com.coda.situlearner.feature.player.playlist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.player.playlist.PlaylistScreen
import kotlinx.serialization.Serializable

@Serializable
data object PlayerPlaylistRoute

fun NavController.navigateToPlayerPlaylist() {
    navigate(PlayerPlaylistRoute)
}

fun NavGraphBuilder.playerPlaylistScreen(
    onBackToPlayer: () -> Unit,
    onBackToParentOfPlayer: () -> Unit
) {
    composable<PlayerPlaylistRoute> {
        PlaylistScreen(
            onBackToPlayer = onBackToPlayer,
            onBackToParentOfPlayer = onBackToParentOfPlayer
        )
    }
}