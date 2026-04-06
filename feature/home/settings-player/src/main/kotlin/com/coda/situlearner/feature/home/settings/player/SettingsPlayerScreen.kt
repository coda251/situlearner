package com.coda.situlearner.feature.home.settings.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.PlaybackOnWordClick
import com.coda.situlearner.core.ui.widget.BackButton
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun SettingsPlayerScreen(
    onBack: () -> Unit,
    viewModel: SettingsPlayerViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsPlayerScreen(
        uiState = uiState,
        onBack = onBack,
        onSetPlaybackOnWordClick = viewModel::setPlaybackOnWordClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsPlayerScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onSetPlaybackOnWordClick: (PlaybackOnWordClick) -> Unit,
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
                        onSetPlaybackOnWordClick = onSetPlaybackOnWordClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentBoard(
    playbackOnWordClick: PlaybackOnWordClick,
    onSetPlaybackOnWordClick: (PlaybackOnWordClick) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        PlaybackOnWordClickSelector(playbackOnWordClick, onSetPlaybackOnWordClick)
    }
}

@Composable
private fun PlaybackOnWordClickSelector(
    playbackOnWordClick: PlaybackOnWordClick,
    onSelect: (PlaybackOnWordClick) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_settings_player_screen_playback_on_word_click)
            )
        },
        supportingContent = {
            Text(
                text = playbackOnWordClick.asText()
            )
        },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text(text = stringResource(coreR.string.core_ui_ok))
                }
            },
            text = {
                Column {
                    PlaybackOnWordClick.entries.forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = it == playbackOnWordClick,
                                onClick = { onSelect(it) },
                            )
                            Text(text = it.asText())
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun PlaybackOnWordClick.asText() = when (this) {
    PlaybackOnWordClick.Pause -> stringResource(R.string.home_settings_player_screen_playback_on_word_click_pause)
    PlaybackOnWordClick.Unchange -> stringResource(R.string.home_settings_player_screen_playback_on_word_click_unchange)
    PlaybackOnWordClick.PlayInLoop -> stringResource(R.string.home_settings_player_screen_playback_on_word_click_play_in_loop)
}