package com.coda.situlearner.feature.player.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider

@Composable
internal fun PlaylistScreen(
    onBackToPlayer: () -> Unit,
    onBackToParentOfPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by PlayerStateProvider.state.collectAsStateWithLifecycle()

    PlaylistScreen(
        playerState = state,
        onBackToPlayer = onBackToPlayer,
        onBackToParentOfPlayer = onBackToParentOfPlayer,
        modifier = modifier
    )
}

@Composable
private fun PlaylistScreen(
    playerState: PlayerState,
    onBackToPlayer: () -> Unit,
    onBackToParentOfPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playlist by playerState.playlist.collectAsStateWithLifecycle()
    val isPlaying by playerState.isPlaying.collectAsStateWithLifecycle()
    val playbackMode by playerState.repeatMode.collectAsStateWithLifecycle()

    LaunchedEffect(playlist) {
        if (playlist.isEmpty()) {
            onBackToParentOfPlayer()
        }
    }

    PlaylistContentBoard(
        playlist = playlist,
        isPlaying = isPlaying,
        repeatMode = playbackMode,
        onSwapItems = playerState::swapItems,
        onSeekToItem = playerState::seekToItem,
        onToggleShouldBePlaying = {
            if (it) playerState.play()
            else playerState.pause()
        },
        onRemoveItem = playerState::removeItem,
        onClearItems = playerState::clear,
        onSetRepeatMode = playerState::setRepeatMode,
        onShufflePlaylist = playerState::shufflePlaylist,
        onBack = onBackToPlayer,
        modifier = modifier
    )
}