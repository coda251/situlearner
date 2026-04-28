package com.coda.situlearner.feature.player.fullscreen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.PlaybackOnWordClick
import com.coda.situlearner.core.model.data.SubtitleDisplayMode
import com.coda.situlearner.core.model.infra.Subtitle
import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.feature.player.fullscreen.util.ChangeScreenOrientation
import com.coda.situlearner.feature.player.fullscreen.util.SetupWindowForFullScreenVideo
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PlayerScreen(
    onBack: () -> Unit,
    onNavigateToPlayerWord: (Token, Subtitle, Language, String) -> Unit,
    viewModel: PlayerViewModel = koinViewModel()
) {
    val subtitleUiState by viewModel.subtitleUiState.collectAsStateWithLifecycle()
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()
    val playerState by PlayerStateProvider.state.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current

    // 1. indicating exiting status
    var isExiting by remember { mutableStateOf(false) }

    // 2. manually request for PORTRAIT orientation before calling onBack()
    // so that we could set up a black screen for transition and avoid the
    // upcoming screen layouts in the LANDSCAPE orientation
    LaunchedEffect(configuration.orientation, isExiting) {
        if (isExiting && configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            onBack()
        }
    }

    BackHandler(enabled = !isExiting) { isExiting = true }
    SetupWindowForFullScreenVideo(isExiting)
    ChangeScreenOrientation(isExiting)

    if (isExiting) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    } else {
        PlayerScreen(
            subtitleUiState = subtitleUiState,
            settingsUiState = settingsUiState,
            playerState = playerState,
            onNavigateToPlayerWord = onNavigateToPlayerWord,
            onBack = {
                isExiting = true
            }
        )
    }
}

@Composable
private fun PlayerScreen(
    subtitleUiState: SubtitleUiState,
    settingsUiState: SettingsUiState,
    playerState: PlayerState,
    onBack: () -> Unit,
    onNavigateToPlayerWord: (Token, Subtitle, Language, String) -> Unit
) {
    val nextSubtitle by rememberUpdatedState(
        when (subtitleUiState) {
            is SubtitleUiState.Success -> subtitleUiState.nextSubtitle
            else -> null
        }
    )

    val prevSubtitle by rememberUpdatedState(
        when (subtitleUiState) {
            is SubtitleUiState.Success -> subtitleUiState.prevSubtitle
            else -> null
        }
    )

    val onSeekToNextSubtitle: () -> Unit = {
        nextSubtitle?.let { playerState.seekTo(it.startTimeInMs) }
    }

    val onSeekToPrevSubtitle: () -> Unit = {
        prevSubtitle?.let { playerState.seekTo(it.startTimeInMs) }
    }

    var showController by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize()) {
        MediaBoard(
            playerState = playerState,
            onTapMediaBoard = { showController = true },
            onDoubleTapMediaBoardLeft = onSeekToPrevSubtitle,
            onDoubleTapMediaBoardRight = onSeekToNextSubtitle
        )

        if (showController) {
            ControllerBoard(
                playerState = playerState,
                onNoAction = { showController = false },
                onDoubleTapMediaBoardLeft = onSeekToPrevSubtitle,
                onDoubleTapMediaBoardRight = onSeekToNextSubtitle,
                onDismissFullscreen = onBack
            )
        }

        SubtitleBoard(
            subtitleUiState = subtitleUiState,
            settingsUiState = settingsUiState,
            playerState = playerState,
            onClickToken = { a, b, c, d ->
                onNavigateToPlayerWord(a, b, c, d)
            },
        )
    }
}

@Composable
private fun SubtitleBoard(
    settingsUiState: SettingsUiState,
    subtitleUiState: SubtitleUiState,
    playerState: PlayerState,
    onClickToken: (Token, Subtitle, Language, String) -> Unit,
) {
    val subtitleData = (subtitleUiState as? SubtitleUiState.Success) ?: return
    val settings = (settingsUiState as? SettingsUiState.Success) ?: return
    val subtitle = subtitleData.currentSubtitle ?: return
    val language = subtitleData.language
    val mediaId = subtitleData.mediaId

    var currentShownSubtitle by remember { mutableStateOf<Subtitle?>(null) }

    SubtitleBoard(
        playerState = playerState,
        subtitle = subtitle,
        showTargetText = when (settings.subtitleDisplayMode) {
            SubtitleDisplayMode.All -> true
            SubtitleDisplayMode.OnlySourceText ->
                if (currentShownSubtitle == null) false
                else currentShownSubtitle == subtitle
        },
        subtitleDisplayMode = settings.subtitleDisplayMode,
        onShowTargetText = {
            currentShownSubtitle = if (currentShownSubtitle == it) null else it
        },
        onClickToken = { a, b ->
            val playbackOnWordClick = settings.playbackOnWordClick
            when (playbackOnWordClick) {
                PlaybackOnWordClick.Unchange -> {}
                PlaybackOnWordClick.Pause -> playerState.pause()
                PlaybackOnWordClick.PlayInLoop -> {
                    playerState.setPlaybackLoop(
                        subtitle.startTimeInMs,
                        subtitle.endTimeInMs
                    )
                    playerState.play()
                }
            }
            onClickToken(a, b, language, mediaId)
        }
    )
}