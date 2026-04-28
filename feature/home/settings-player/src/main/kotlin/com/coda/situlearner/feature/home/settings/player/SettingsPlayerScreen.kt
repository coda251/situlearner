package com.coda.situlearner.feature.home.settings.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.PlaybackOnWordClick
import com.coda.situlearner.core.model.data.SubtitleDisplayMode
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.SingleChoiceSelector
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SettingsPlayerScreen(
    onBack: () -> Unit,
    viewModel: SettingsPlayerViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsPlayerScreen(
        uiState = uiState,
        onBack = onBack,
        onSetPlaybackOnWordClick = viewModel::setPlaybackOnWordClick,
        onSetSubtitleDisplayMode = viewModel::setSubtitleDisplayMode
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsPlayerScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onSetPlaybackOnWordClick: (PlaybackOnWordClick) -> Unit,
    onSetSubtitleDisplayMode: (SubtitleDisplayMode) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(onBack)
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (uiState) {
                UiState.Loading -> {}
                is UiState.Success -> {
                    ContentBoard(
                        playbackOnWordClick = uiState.playbackOnWordClick,
                        subtitleDisplayMode = uiState.subtitleDisplayMode,
                        onSetPlaybackOnWordClick = onSetPlaybackOnWordClick,
                        onSetSubtitleDisplayMode = onSetSubtitleDisplayMode
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentBoard(
    playbackOnWordClick: PlaybackOnWordClick,
    subtitleDisplayMode: SubtitleDisplayMode,
    onSetPlaybackOnWordClick: (PlaybackOnWordClick) -> Unit,
    onSetSubtitleDisplayMode: (SubtitleDisplayMode) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        PlaybackOnWordClickSelector(playbackOnWordClick, onSetPlaybackOnWordClick)
        SubtitleDisplayModeSelector(subtitleDisplayMode, onSetSubtitleDisplayMode)
    }
}

@Composable
private fun PlaybackOnWordClickSelector(
    playbackOnWordClick: PlaybackOnWordClick,
    onSelect: (PlaybackOnWordClick) -> Unit
) {
    SingleChoiceSelector(
        currentValue = playbackOnWordClick,
        choices = PlaybackOnWordClick.entries,
        headline = stringResource(R.string.home_settings_player_screen_playback_on_word_click),
        supportingText = playbackOnWordClick.asText(),
        valueToText = { it.asText() },
        onConfirm = onSelect
    )
}

@Composable
private fun SubtitleDisplayModeSelector(
    subtitleDisplayMode: SubtitleDisplayMode,
    onSelect: (SubtitleDisplayMode) -> Unit
) {
    SingleChoiceSelector(
        currentValue = subtitleDisplayMode,
        choices = SubtitleDisplayMode.entries,
        headline = stringResource(R.string.home_settings_player_screen_subtitle_display_mode),
        supportingText = subtitleDisplayMode.asText(),
        valueToText = { it.asText() },
        onConfirm = onSelect
    )
}

@Composable
private fun PlaybackOnWordClick.asText() = when (this) {
    PlaybackOnWordClick.Pause -> stringResource(R.string.home_settings_player_screen_playback_on_word_click_pause)
    PlaybackOnWordClick.Unchange -> stringResource(R.string.home_settings_player_screen_playback_on_word_click_unchange)
    PlaybackOnWordClick.PlayInLoop -> stringResource(R.string.home_settings_player_screen_playback_on_word_click_play_in_loop)
}

@Composable
private fun SubtitleDisplayMode.asText() = when (this) {
    SubtitleDisplayMode.All -> stringResource(R.string.home_settings_player_screen_subtitle_display_mode_all)
    SubtitleDisplayMode.OnlySourceText -> stringResource(R.string.home_settings_player_screen_subtitle_display_mode_only_source_text)
}