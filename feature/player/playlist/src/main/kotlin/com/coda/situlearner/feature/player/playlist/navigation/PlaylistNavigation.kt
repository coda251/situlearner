package com.coda.situlearner.feature.player.playlist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.coda.situlearner.feature.player.playlist.PlaylistScreen
import kotlinx.serialization.Serializable

@Serializable
data object PlaylistRoute

fun NavController.navigateToPlaylist() {
    navigate(PlaylistRoute)
}

fun NavGraphBuilder.playlistScreen(
    onBackToPlayer: () -> Unit,
    onBackToParentOfPlayer: () -> Unit
) {
    composable<PlaylistRoute> {
        PlaylistScreen(
            onBackToPlayer = onBackToPlayer,
            onBackToParentOfPlayer = onBackToParentOfPlayer
        )
    }
}