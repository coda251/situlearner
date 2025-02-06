package com.coda.situlearner.feature.player.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider

@Composable
internal fun PlayerEntryScreen(
    onBack: () -> Unit,
    onNavigateToPlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()
    PlayerEntryScreen(
        playerState = playerState,
        onBack = onBack,
        onNavigateToPlaylist = onNavigateToPlaylist,
        modifier = modifier
    )
}

@Composable
private fun PlayerEntryScreen(
    playerState: PlayerState,
    onBack: () -> Unit,
    onNavigateToPlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playlist by playerState.playlist.collectAsStateWithLifecycle()

    val isPlaylistEmpty by remember { derivedStateOf { playlist.isEmpty() } }
    LaunchedEffect(isPlaylistEmpty) {
        if (isPlaylistEmpty) onBack()
    }

    var playerWordBottomSheetRoute by remember(key1 = playlist) {
        mutableStateOf<PlayerWordBottomSheetRoute?>(null)
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            Column {
                PlayerItemInfoBoard(
                    playerState = playerState,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                PlayerControllerBoard(
                    playerState = playerState,
                    onNavigateToPlaylist = onNavigateToPlaylist,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                        .navigationBarsPadding()
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            PlayerMediaBoard(playerState = playerState, onBack = onBack)
            PlayerSubtitleBoard(
                playerState = playerState,
                activeSubtitleIndex = playerWordBottomSheetRoute?.subtitleIndex ?: -1,
                activeTokenStartIndex = playerWordBottomSheetRoute?.wordStartIndex ?: -1,
                onClickTokenInSubtitleContext = { token, subtitleContext ->
                    playerWordBottomSheetRoute = PlayerWordBottomSheetRoute(
                        word = token.lemma,
                        language = subtitleContext.language,
                        mediaId = subtitleContext.mediaId,
                        subtitleIndex = subtitleContext.index,
                        subtitleSourceText = subtitleContext.subtitle.sourceText,
                        subtitleTargetText = subtitleContext.subtitle.targetText,
                        subtitleStartTimeInMs = subtitleContext.subtitle.startTimeInMs,
                        subtitleEndTimeInMs = subtitleContext.subtitle.endTimeInMs,
                        wordStartIndex = token.startIndex,
                        wordEndIndex = token.endIndex,
                    )
                },
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

    playerWordBottomSheetRoute?.let {
        PlayerWordBottomSheet(
            route = it,
            onDismiss = {
                playerWordBottomSheetRoute = null
            }
        )
    }
}

