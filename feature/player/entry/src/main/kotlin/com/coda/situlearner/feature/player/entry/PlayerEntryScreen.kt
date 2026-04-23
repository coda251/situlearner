package com.coda.situlearner.feature.player.entry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.PlaybackOnWordClick
import com.coda.situlearner.core.model.infra.Subtitle
import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PlayerEntryScreen(
    resetTokenFlag: Int,
    onBack: () -> Unit,
    onNavigateToPlaylist: () -> Unit,
    onNavigateToPlayerWord: (Token, Subtitle, Language, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerEntryViewModel = koinViewModel()
) {
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()
    PlayerEntryScreen(
        playerState = playerState,
        settingsUiState = settingsUiState,
        resetTokenFlag = resetTokenFlag,
        onBack = onBack,
        onNavigateToPlaylist = onNavigateToPlaylist,
        onNavigateToPlayerWord = onNavigateToPlayerWord,
        modifier = modifier
    )
}

@Composable
private fun PlayerEntryScreen(
    playerState: PlayerState,
    settingsUiState: PlayerSettingsUiState,
    resetTokenFlag: Int,
    onBack: () -> Unit,
    onNavigateToPlaylist: () -> Unit,
    onNavigateToPlayerWord: (Token, Subtitle, Language, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val playlist by playerState.playlist.collectAsStateWithLifecycle()

    val isPlaylistEmpty by remember { derivedStateOf { playlist.isEmpty() } }
    LaunchedEffect(isPlaylistEmpty) {
        if (isPlaylistEmpty) onBack()
    }

    // the reset token flag is used to indicate the navigation action
    // from player bottom sheet back to player entry screen
    var activeTokenId by remember(resetTokenFlag) {
        mutableStateOf(Pair(-1, -1))
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
            when (settingsUiState) {
                PlayerSettingsUiState.Loading -> {
                    Box(modifier = modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is PlayerSettingsUiState.Success -> {
                    PlayerSubtitleBoard(
                        playerState = playerState,
                        activeSubtitleIndex = activeTokenId.first,
                        activeTokenStartIndex = activeTokenId.second,
                        subtitleDisplayMode = settingsUiState.subtitleDisplayMode,
                        onClickTokenInSubtitleContext = { token, subtitleContext ->

                            val playbackOnWordClick = settingsUiState.playbackOnWordClick
                            when (playbackOnWordClick) {
                                PlaybackOnWordClick.Unchange -> {}
                                PlaybackOnWordClick.Pause -> playerState.pause()
                                PlaybackOnWordClick.PlayInLoop -> {
                                    playerState.setPlaybackLoop(
                                        subtitleContext.subtitle.startTimeInMs,
                                        subtitleContext.subtitle.endTimeInMs
                                    )
                                    playerState.play()
                                }
                            }

                            onNavigateToPlayerWord(
                                token,
                                subtitleContext.subtitle,
                                subtitleContext.language,
                                subtitleContext.mediaId
                            )
                            activeTokenId = Pair(subtitleContext.index, token.startIndex)
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}